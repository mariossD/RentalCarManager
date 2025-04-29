package com.example.rentalcarmanager.data.local.repository

import com.example.rentalcarmanager.data.local.dao.DaoCars
import com.example.rentalcarmanager.data.local.entity.Cars
import kotlinx.coroutines.flow.Flow

object CarsRepo {

  private lateinit var carsDao: DaoCars

  fun init(carsDao: DaoCars) {
    this.carsDao = carsDao
  }

  fun getAllCars(): Flow<List<Cars>> = carsDao.getAllCars()

  suspend fun upsertCar(car: Cars) {
    carsDao.upsertCar(car)
  }

  suspend fun deleteCar(car: Cars) {
    carsDao.deleteCar(car)
  }
}
