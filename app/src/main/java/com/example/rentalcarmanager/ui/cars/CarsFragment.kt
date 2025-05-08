package com.example.rentalcarmanager.ui.cars

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rentalcarmanager.R
import com.example.rentalcarmanager.data.local.entity.Cars
import com.example.rentalcarmanager.data.local.enums.CarCategory
import com.example.rentalcarmanager.data.local.repository.BranchesRepo
import com.example.rentalcarmanager.databinding.FragmentCarsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CarsFragment : Fragment() {

  private var _binding: FragmentCarsBinding? = null
  private val binding get() = _binding!!

  private val viewModel: CarsViewModel by viewModels()

  private lateinit var carAdapter: CarsAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentCarsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Adapter for RecyclerView with click handling for edit and delete
    carAdapter = CarsAdapter(
      onItemClick = { selectedCar ->
        upsertCar(selectedCar)
      },
      onDeleteClick = { selectedCar ->
        deleteCar(selectedCar)
      }
    )

    setupRecyclerView()
    observeCars()
    setupButton()
  }

  // Collect all cars from the ViewModel
  private fun observeCars() {
    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.allCars.collectLatest { cars ->
        carAdapter.submitList(cars)
      }
    }
  }

  // Display cars in a 2-column grid layout
  private fun setupRecyclerView() {
    binding.recyclerView.apply {
      layoutManager = GridLayoutManager(requireContext(), 2)
      adapter = carAdapter
    }
  }

  // Button to add a new car
  private fun setupButton() {
    binding.buttonAddCustomer.setOnClickListener {
      upsertCar()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  // Show dialog to add or update a car
  private fun upsertCar(car: Cars? = null) {
    context?.let { safeContext ->
      val dialogView = LayoutInflater.from(safeContext).inflate(R.layout.upsert_car, null)

      // References to views
      val brandEditText = dialogView.findViewById<EditText>(R.id.editTextBrand)
      val modelEditText = dialogView.findViewById<EditText>(R.id.editTextModel)
      val plateEditText = dialogView.findViewById<EditText>(R.id.editTextLicensePlate)
      val categoryEditText = dialogView.findViewById<AutoCompleteTextView>(R.id.autoCompleteCategory)
      val branchDropdown = dialogView.findViewById<AutoCompleteTextView>(R.id.autoCompleteBranch)
      val buttonSave = dialogView.findViewById<Button>(R.id.buttonSaveCar)
      val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancelCar)

      // Load car categories from enum
      val categories = CarCategory.values().map { it.name }
      val categoryAdapter = ArrayAdapter(
        safeContext,
        android.R.layout.simple_dropdown_item_1line,
        categories
      )
      categoryEditText.setAdapter(categoryAdapter)
      categoryEditText.setOnClickListener {
        categoryEditText.requestFocus()
        categoryEditText.showDropDown()
      }

      var selectedBranchId = car?.branchId ?: -1

      // Load branches and bind to dropdown
      lifecycleScope.launch {
        BranchesRepo.getAllBranches().collectLatest { branches ->
          val branchNames = branches.map { it.name }
          branchDropdown.setAdapter(ArrayAdapter(safeContext, android.R.layout.simple_dropdown_item_1line, branchNames))
          branchDropdown.setOnItemClickListener { _, _, pos, _ ->
            selectedBranchId = branches[pos].id
          }

          // Pre-fill branch if editing
          if (car != null) {
            val current = branches.find { it.id == car.branchId }
            current?.let {
              branchDropdown.setText(it.name, false)
            }
          }
        }
      }

      // Pre-fill fields if editing
      car?.let {
        brandEditText.setText(it.brand)
        modelEditText.setText(it.model)
        plateEditText.setText(it.licensePlate)
        categoryEditText.setText(it.category, false)
      }

      // Create dialog
      val dialog = AlertDialog.Builder(safeContext)
        .setView(dialogView)
        .create()

      buttonCancel.setOnClickListener {
        dialog.dismiss()
      }

      buttonSave.setOnClickListener {
        val brand = brandEditText.text.toString()
        val model = modelEditText.text.toString()
        val plate = plateEditText.text.toString()
        val category = categoryEditText.text.toString()

        if (brand.isNotBlank() && model.isNotBlank() && plate.isNotBlank() && category.isNotBlank() && selectedBranchId != -1) {
          val newCar = Cars(
            id = car?.id ?: 0,
            brand = brand,
            model = model,
            licensePlate = plate,
            category = category,
            branchId = selectedBranchId
          )
          viewModel.upsertCar(newCar)
          dialog.dismiss()
        } else {
          Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
      }

      dialog.show()
    }
  }

  // Show confirmation dialog for deleting a car
  private fun deleteCar(car: Cars) {
    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.delete_car, null)

    val dialog = AlertDialog.Builder(requireContext())
      .setView(dialogView)
      .create()

    val buttonDelete = dialogView.findViewById<Button>(R.id.buttonDelete)
    val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)

    buttonDelete.setOnClickListener {
      viewModel.deleteCar(car)
      Toast.makeText(requireContext(), "Car deleted", Toast.LENGTH_SHORT).show()
      dialog.dismiss()

    }

    buttonCancel.setOnClickListener {
      dialog.dismiss()
    }

    dialog.show()
  }
}
