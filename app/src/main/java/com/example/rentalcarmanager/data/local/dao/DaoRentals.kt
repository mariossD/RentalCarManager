package com.example.rentalcarmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.rentalcarmanager.data.local.entity.Rentals
import kotlinx.coroutines.flow.Flow


@Dao
interface DaoRentals {

  @Upsert
  suspend fun upsertRental(rental: Rentals)

  @Delete
  suspend fun deleteRental(rental: Rentals)


  @Query("SELECT * FROM rentals ORDER BY rentalDate DESC")
  fun getAllRentals(): Flow<List<Rentals>>

  @Query("SELECT * FROM rentals WHERE carId = :carId ORDER BY rentalDate DESC")
  fun getRentalsByCar(carId: Int): Flow<List<Rentals>>

  @Query("SELECT * FROM rentals WHERE customerId = :customerId ORDER BY rentalDate DESC")
  fun getRentalsByCustomer(customerId: Int): Flow<List<Rentals>>

 @Query("SELECT * FROM rentals WHERE branchId = :branchId ORDER BY rentalDate DESC")
  fun getRentalsByBranch(branchId: Int): Flow<List<Rentals>>


}