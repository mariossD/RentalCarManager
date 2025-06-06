package com.example.rentalcarmanager.ui.rentals

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentalcarmanager.R
import com.example.rentalcarmanager.data.remote.RentalFirestore
import com.example.rentalcarmanager.databinding.FragmentRentalsBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RentalsFragment : Fragment() {

  private var rentalsBinding: FragmentRentalsBinding? = null
  private val binding get() = rentalsBinding!!

  // ViewModel to access rentals, cars, customers, and branches
  private val viewModel: RentalsViewModel by viewModels()
  private lateinit var rentalsAdapter: RentalsAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    // Inflate layout using ViewBinding
    rentalsBinding = FragmentRentalsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupAddRentalButton()

    // Observe local data from Room and remote data from Firestore
    lifecycleScope.launch {
      viewModel.allCars.collectLatest { cars ->
        val carMap = cars.associate { it.id to "${it.brand} ${it.model}" }
        viewModel.allcustomers.collectLatest { customers ->
          val customerMap = customers.associate { it.id to it.customersName }
          viewModel.allBranches.collectLatest { branches ->
            val branchMap = branches.associate { it.id to it.name }

            // Create adapter with maps and set click actions for edit/delete
            rentalsAdapter = RentalsAdapter(
              carMap,
              customerMap,
              branchMap,
              onItemClick = { rental -> upsertRental(rental) },
              onDeleteClick = { rental -> deleteRental(rental) }
            )

            // Setup RecyclerView with adapter
            binding.recyclerView.apply {
              layoutManager = LinearLayoutManager(requireContext())
              adapter = rentalsAdapter
            }

            // Observe rentals from Firestore and update UI list
            viewModel.rentals.collectLatest { list ->
              rentalsAdapter.submitList(list)
            }
          }
        }
      }
    }
  }


  // Show dialog to add a new rental
  private fun setupAddRentalButton() {
    binding.buttonAddRental.setOnClickListener {
      upsertRental()
    }
  }


  // Show dialog for adding or editing a rental
  @SuppressLint("MissingPermission")
  private fun upsertRental(existingRental: RentalFirestore? = null) {
    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.upsert_rental, null)

    // UI components from dialog layout
    val branchDropdown = dialogView.findViewById<AutoCompleteTextView>(R.id.autoCompleteBranch)
    val carDropdown = dialogView.findViewById<AutoCompleteTextView>(R.id.autoCompleteCar)
    val customerDropdown = dialogView.findViewById<AutoCompleteTextView>(R.id.autoCompleteCustomer)
    val rentalDateEditText = dialogView.findViewById<EditText>(R.id.editTextRentalDate)
    val returnDateEditText = dialogView.findViewById<EditText>(R.id.editTextRentalReturn)

    // Selected values
    var selectedBranchId = existingRental?.branchId ?: -1
    var selectedCarId = existingRental?.carId ?: -1
    var selectedCustomerId = existingRental?.customerId ?: -1

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var selectedRentalDate: String? = null
    var selectedReturnDate: String? = null

    // Load available cars based on date and branch
    fun availableCars() {
      val start = selectedRentalDate
      val end = selectedReturnDate

      if (start != null && end != null && selectedBranchId != -1) {
        val startDate = dateFormat.parse(start)
        val endDate = dateFormat.parse(end)

        // Exclude cars that are already rented during the selected period
        val unavailableCarIds = viewModel.rentals.value.filter { rental ->
          if (existingRental != null && rental.id == existingRental.id) return@filter false
          val rStart = dateFormat.parse(rental.rentalDate)
          val rEnd = dateFormat.parse(rental.returnDate)
          !(endDate.before(rStart) || startDate.after(rEnd))
        }.map { it.carId }.toSet()

        val availableCars = viewModel.allCars.value.filter { car ->
          car.branchId == selectedBranchId && !unavailableCarIds.contains(car.id)
        }

        if (availableCars.isEmpty()) {
          carDropdown.setText("")
          carDropdown.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listOf(" No cars available"))
          )
          selectedCarId = -1
          return
        }

        val carNames = availableCars.map { "${it.brand} ${it.model}" }
        carDropdown.setAdapter(
          ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, carNames)
        )
        carDropdown.setOnItemClickListener { _, _, pos, _ ->
          selectedCarId = availableCars[pos].id
        }

        if (existingRental != null) {
          val match = availableCars.find { it.id == existingRental.carId }
          carDropdown.setText(match?.let { "${it.brand} ${it.model}" } ?: "", false)
        }
      }
    }

    // Show Material date picker and update dates
    val datePicker = { editText: EditText, tag: String ->
      val picker = com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
        .setTitleText(tag)
        .build()
      picker.addOnPositiveButtonClickListener { selection ->
        val formatted = dateFormat.format(Date(selection))
        editText.setText(formatted)
        if (tag == "rental_date") selectedRentalDate = formatted else selectedReturnDate = formatted
        availableCars()
      }
      picker.show(parentFragmentManager, tag)
    }

    rentalDateEditText.setOnClickListener { datePicker(rentalDateEditText, "rental_date") }
    returnDateEditText.setOnClickListener { datePicker(returnDateEditText, "return_date") }

    // Populate dropdowns for branches and customers
    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.allBranches.collectLatest { branches ->
        val items = branches.map { it.name }
        branchDropdown.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items))
        branchDropdown.setOnItemClickListener { _, _, pos, _ ->
          selectedBranchId = branches[pos].id
          availableCars()
        }
        if (existingRental != null) {
          branchDropdown.setText(branches.find { it.id == selectedBranchId }?.name ?: "", false)
        }
      }
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.allcustomers.collectLatest { customers ->
        val items = customers.map { it.customersName }
        customerDropdown.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items))
        customerDropdown.setOnItemClickListener { _, _, pos, _ -> selectedCustomerId = customers[pos].id }
        if (existingRental != null) {
          customerDropdown.setText(customers.find { it.id == selectedCustomerId }?.customersName ?: "", false)
        }
      }
    }

    // Pre-fill dates if editing
    existingRental?.let {
      selectedRentalDate = it.rentalDate
      selectedReturnDate = it.returnDate
      rentalDateEditText.setText(it.rentalDate)
      returnDateEditText.setText(it.returnDate)
    }

    val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

    val buttonSave = dialogView.findViewById<Button>(R.id.buttonSaveRental)
    val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancelRental)

    buttonCancel.setOnClickListener { dialog.dismiss() }

    // Save or update rental to Firestore
    buttonSave.setOnClickListener {
      val rentalDate = selectedRentalDate
      val returnDate = selectedReturnDate

      if (rentalDate.isNullOrBlank() || returnDate.isNullOrBlank()) {
        Toast.makeText(requireContext(), "Please select both dates", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      if (rentalDate >= returnDate) {
        Toast.makeText(requireContext(), "Return date must be after rental date", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      if (selectedCarId == -1) {
        Toast.makeText(requireContext(), "No available car selected", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      val id = existingRental?.id ?: UUID.randomUUID().toString()
      val rental = RentalFirestore(
        id = id,
        carId = selectedCarId,
        customerId = selectedCustomerId,
        branchId = selectedBranchId,
        rentalDate = rentalDate,
        returnDate = returnDate
      )

      FirebaseFirestore.getInstance().collection("rentals")
        .document(id).set(rental)
        .addOnSuccessListener {
          Toast.makeText(requireContext(), "Rental saved successfully", Toast.LENGTH_SHORT).show()
          viewModel.loadRentalsFromFirestore()
          requireContext().sendRentalNotification(
            viewModel.allcustomers.value.find { it.id == selectedCustomerId }?.customersName ?: "Unknown"
          )
          dialog.dismiss()
        }
        .addOnFailureListener {
          Toast.makeText(requireContext(), "Failed to save rental", Toast.LENGTH_SHORT).show()
        }
    }

    dialog.show()
  }

  // Show confirmation dialog to delete a rental
  private fun deleteRental(rental: RentalFirestore) {
    val view = LayoutInflater.from(requireContext()).inflate(R.layout.delete_rental, null)

    val dialog = AlertDialog.Builder(requireContext()).setView(view).create()

    val buttonDelete = view.findViewById<Button>(R.id.buttonDelete)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)

    buttonDelete.setOnClickListener {
      FirebaseFirestore.getInstance().collection("rentals")
        .document(rental.id)
        .delete()
        .addOnSuccessListener {
          Toast.makeText(requireContext(), "Rental deleted", Toast.LENGTH_SHORT).show()
          viewModel.loadRentalsFromFirestore()
          dialog.dismiss()
        }
        .addOnFailureListener {
          Toast.makeText(requireContext(), "Failed to delete rental", Toast.LENGTH_SHORT).show()
        }
    }

    buttonCancel.setOnClickListener { dialog.dismiss() }

    dialog.show()
  }

  // Send a local notification about a new rental (requires POST_NOTIFICATIONS permission)
  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun Context.sendRentalNotification(customerName: String) {
    val builder = NotificationCompat.Builder(this, "rentals_channel")
      .setSmallIcon(R.drawable.drawer_main_icon)
      .setContentTitle("New Rental Created")
      .setContentText("Customer: $customerName")
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    NotificationManagerCompat.from(this).notify(1001, builder.build())
  }

  override fun onDestroyView() {
    super.onDestroyView()
    rentalsBinding = null // Clear view binding to avoid memory leaks
  }
}
