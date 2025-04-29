package com.example.rentalcarmanager.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalcarmanager.data.local.entity.Customers
import com.example.rentalcarmanager.data.local.repository.CustomersRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CustomersViewModel() : ViewModel() {

  private val repository = CustomersRepo

  val allCustomers: StateFlow<List<Customers>> = repository.getAllCustomers()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

  fun upsertCustomer(customer: Customers) {
    viewModelScope.launch {
      repository.upsertCustomer(customer)
    }
  }

  fun deleteCustomer(customer: Customers) {
    viewModelScope.launch {
      repository.deleteCustomer(customer)
    }
  }
}
