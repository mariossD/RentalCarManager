package com.example.rentalcarmanager.data.local.repository

import com.example.rentalcarmanager.data.local.dao.DaoRentals
import com.example.rentalcarmanager.data.local.entity.Rentals
import kotlinx.coroutines.flow.Flow


object RentalsRepo {

  private lateinit var daoRentals: DaoRentals

  fun init(daoRentals: DaoRentals) {
    this.daoRentals = daoRentals
  }

  fun getAllRentals(): Flow<List<Rentals>> {
    return daoRentals.getAllRentals()
  }

  fun getRentalsByCar(carId: Int): Flow<List<Rentals>> {
    return daoRentals.getRentalsByCar(carId)
  }

  fun getRentalsByCustomer(customerId: Int): Flow<List<Rentals>> {
    return daoRentals.getRentalsByCustomer(customerId)
  }

  suspend fun upsertRental(rental: Rentals) {
    daoRentals.upsertRental(rental)
  }

  suspend fun deleteRental(rental: Rentals) {
    daoRentals.deleteRental(rental)
  }
}