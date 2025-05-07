package com.example.rentalcarmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.rentalcarmanager.data.local.entity.Cars
import kotlinx.coroutines.flow.Flow


@Dao
interface DaoCars {

  @Upsert
  suspend fun upsertCar(car: Cars)

  @Delete
  suspend fun deleteCar(car: Cars)

  @Query("SELECT * FROM cars ORDER BY brand ASC")
  fun getAllCars(): Flow<List<Cars>>


  @Query("SELECT * FROM cars WHERE branchId = :branchId ORDER BY brand ASC")
  fun getCarsByBranch(branchId: Int): Flow<List<Cars>>

  @Query("SELECT * FROM cars WHERE brand=:brand ORDER BY brand ASC")
  fun getCarsByBrand(brand:String): Flow<List<Cars>>

  @Query("SELECT * FROM cars WHERE category = :category ORDER BY brand ASC")
  fun getCarsByCategory(category: String): Flow<List<Cars>>
}