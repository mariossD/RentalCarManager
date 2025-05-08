package com.example.rentalcarmanager.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalcarmanager.data.local.entity.Cars
import com.example.rentalcarmanager.data.local.repository.CarsRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarsViewModel : ViewModel() {

  private val repository = CarsRepo  // Using the singleton object CarsRepo

  // Collect all cars from the repository as StateFlow for observation
  val allCars = repository.getAllCars()
    .stateIn(
      viewModelScope, // Lifecycle-aware scope
      SharingStarted.WhileSubscribed(5000), // Keeps the flow active briefly when not observed
      emptyList() // Initial empty value
    )

  // Insert or update a car
  fun upsertCar(car: Cars) {
    viewModelScope.launch {
      repository.upsertCar(car)
    }
  }

  // Delete a car
  fun deleteCar(car: Cars) {
    viewModelScope.launch {
      repository.deleteCar(car)
    }
  }
}
