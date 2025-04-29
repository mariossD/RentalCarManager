package com.example.rentalcarmanager.ui.customers

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentalcarmanager.R
import com.example.rentalcarmanager.data.local.DatabaseProvider
import com.example.rentalcarmanager.data.local.entity.Customers
import com.example.rentalcarmanager.data.local.repository.CustomersRepo
import com.example.rentalcarmanager.databinding.FragmentCustomersBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CustomersFragment : Fragment() {

  private var _binding: FragmentCustomersBinding? = null
  private val binding get() = _binding!!

  private lateinit var customerAdapter: CustomersAdapter

  private val viewModel: CustomersViewModel by viewModels ()



  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentCustomersBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    CustomersRepo.init(DatabaseProvider.getDatabase(requireContext()).daoCustomers())
    customerAdapter = CustomersAdapter { selectedCustomer ->
      showAddCustomerDialog(selectedCustomer)
    }

    setupRecyclerView()
    observeCustomers()
    setupButton()
  }

  private fun setupButton() {
    binding.buttonAddCustomer.setOnClickListener {
      showAddCustomerDialog()
    }
  }

  private fun setupRecyclerView() {
    customerAdapter = CustomersAdapter { selectedCustomer ->
      showAddCustomerDialog(selectedCustomer)
    }
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

  private fun showAddCustomerDialog(customer: Customers? = null) {
    context?.let { safeContext ->
      val dialogView = LayoutInflater.from(safeContext).inflate(R.layout.add_customer_dialog, null)

      val firstNameEditText = dialogView.findViewById<EditText>(R.id.editTextFirstName)
      val idNumberEditText = dialogView.findViewById<EditText>(R.id.editTextIdNumber)
      val phoneEditText = dialogView.findViewById<EditText>(R.id.editTextPhone)

      customer?.let {
        firstNameEditText.setText(it.customersName)
        idNumberEditText.setText(it.drivelLicenseNumber)
        phoneEditText.setText(it.phoneNumber)
      }

      val dialog = AlertDialog.Builder(safeContext)
        .setTitle(if (customer == null) "Προσθήκη Πελάτη" else "Επεξεργασία Πελάτη")
        .setView(dialogView)
        .setPositiveButton(if (customer == null) "Προσθήκη" else "Αποθήκευση") { _, _ ->
          val firstName = firstNameEditText.text.toString()
          val idNumber = idNumberEditText.text.toString()
          val phone = phoneEditText.text.toString()

          if (firstName.isNotBlank() && idNumber.isNotBlank() && phone.isNotBlank()) {
            val newCustomer = Customers(
              id = customer?.id ?: 0,
              customersName = firstName,
              drivelLicenseNumber = idNumber,
              phoneNumber = phone
            )
            viewModel.upsertCustomer(newCustomer)
          }
        }
        .setNegativeButton("Άκυρο", null)
        .create()

      dialog.show()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
