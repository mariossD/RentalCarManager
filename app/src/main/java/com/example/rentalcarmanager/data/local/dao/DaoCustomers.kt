package com.example.rentalcarmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.rentalcarmanager.data.local.entity.Customers
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoCustomers {

  @Upsert
  suspend fun upsertCustomer(customers: Customers)

  @Delete
  suspend fun deleteCustomer(customers: Customers)

  @Query("SELECT * FROM customers ORDER BY customersName ASC")
  fun getAllCustomers(): Flow<List<Customers>>

}
