package com.example.rentalcarmanager.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalcarmanager.data.local.entity.Cars
import com.example.rentalcarmanager.data.local.repository.CarsRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarsViewModel : ViewModel() {

  private val repository = CarsRepo  // ➔ Χρησιμοποιούμε το object CarsRepo!

  val allCars=repository.getAllCars()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

  fun upsertCar(car: Cars) {
    viewModelScope.launch {
      repository.upsertCar(car)
    }
  }

  fun deleteCar(car:Cars) {
    viewModelScope.launch {
      repository.deleteCar(car)
    }
  }
}
