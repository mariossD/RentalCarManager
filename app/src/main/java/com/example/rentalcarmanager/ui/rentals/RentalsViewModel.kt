package com.example.rentalcarmanager.ui.rentals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalcarmanager.data.local.entity.Rentals
import com.example.rentalcarmanager.data.local.repository.RentalsRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RentalsViewModel() : ViewModel() {

  private val repository = RentalsRepo

  val allRentals: StateFlow<List<Rentals>> = repository.getAllRentals()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

  fun upsertRental(rental: Rentals) {
    viewModelScope.launch {
      repository.upsertRental(rental)
    }
  }

  fun deleteRental(rental: Rentals) {
    viewModelScope.launch {
      repository.deleteRental(rental)
    }
  }
}
