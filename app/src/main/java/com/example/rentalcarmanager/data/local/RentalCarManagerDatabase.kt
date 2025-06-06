package com.example.rentalcarmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rentalcarmanager.data.local.dao.DaoBranches
import com.example.rentalcarmanager.data.local.dao.DaoCars
import com.example.rentalcarmanager.data.local.dao.DaoCustomers
import com.example.rentalcarmanager.data.local.entity.Branches
import com.example.rentalcarmanager.data.local.entity.Cars
import com.example.rentalcarmanager.data.local.entity.Customers

@Database(
  entities = [
    Branches::class,
    Cars::class,
    Customers::class
  ],
  version = 5, // Current schema version of the database
  exportSchema = false
)
abstract class RentalCarManagerDatabase: RoomDatabase() {

  abstract fun daoBranches(): DaoBranches
  abstract fun daoCars(): DaoCars
  abstract fun daoCustomers(): DaoCustomers
}
