package com.example.rentalcarmanager.ui.customers

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentalcarmanager.R
import com.example.rentalcarmanager.data.local.entity.Customers
import com.example.rentalcarmanager.databinding.FragmentCustomersBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CustomersFragment : Fragment() {

  private var _binding: FragmentCustomersBinding? = null
  private val binding get() = _binding!!

  private lateinit var customerAdapter: CustomersAdapter

  private val viewModel: CustomersViewModel by viewModels()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentCustomersBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    customerAdapter = CustomersAdapter(
      onItemClick = { selectedCustomer -> upsertCustomer(selectedCustomer) },
      onDeleteClick = { selectedCustomer -> deleteCustomer(selectedCustomer) }
    )

    setupRecyclerView()
    observeCustomers()
    setupButton()
  }

  private fun setupButton() {
    binding.buttonAddCustomer.setOnClickListener {
      upsertCustomer()
    }
  }

  private fun setupRecyclerView() {
    binding.recyclerView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = customerAdapter
    }
  }

  private fun observeCustomers() {
    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.allCustomers.collectLatest { customers ->
        customerAdapter.submitList(customers)
      }
    }
  }

  private fun upsertCustomer(customer: Customers? = null) {
    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.upsert_customer, null)
    val firstName = dialogView.findViewById<EditText>(R.id.editTextFirstName)
    val idNumber = dialogView.findViewById<EditText>(R.id.editTextIdNumber)
    val phone = dialogView.findViewById<EditText>(R.id.editTextPhone)
    val buttonSave = dialogView.findViewById<Button>(R.id.buttonSaveCustomer)
    val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancelCustomer)

    val dialog = AlertDialog.Builder(requireContext())
      .setView(dialogView)
      .create()

    customer?.let {
      firstName.setText(it.customersName)
      idNumber.setText(it.drivelLicenseNumber)
      phone.setText(it.phoneNumber)
    }

    buttonSave.setOnClickListener {
      val firstName = firstName.text.toString()
      val idNumber = idNumber.text.toString()
      val phone = phone.text.toString()

      if (firstName.isNotBlank() && idNumber.isNotBlank() && phone.isNotBlank()) {
        val newCustomer = Customers(
          id = customer?.id ?: 0,
          customersName = firstName,
          drivelLicenseNumber = idNumber,
          phoneNumber = phone )
        viewModel.upsertCustomer(newCustomer)
        dialog.dismiss()
      } else {
        Toast.makeText(requireContext(), "Συμπλήρωσε όλα τα πεδία", Toast.LENGTH_SHORT).show()
      }
    }

    buttonCancel.setOnClickListener {
      dialog.dismiss()
    }

    dialog.show()
  }

  private fun deleteCustomer(customer: Customers) {
    val view = LayoutInflater.from(requireContext()).inflate(R.layout.delete_customer, null)
    val dialog = AlertDialog.Builder(requireContext()).setView(view).create()

    val buttonDelete = view.findViewById<Button>(R.id.buttonDeleteCustomer)
    val buttonCancel = view.findViewById<Button>(R.id.buttonDeleteCancelCustomer)

    buttonDelete.setOnClickListener {
      viewModel.deleteCustomer(customer)
      dialog.dismiss()
    }

    buttonCancel.setOnClickListener {
      dialog.dismiss()
    }

    dialog.show()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}