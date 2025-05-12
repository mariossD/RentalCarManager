package com.example.rentalcarmanager.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

  // Volatile ensures that changes made by one thread to INSTANCE are visible to others
  @Volatile
  private var INSTANCE: RentalCarManagerDatabase? = null

  // Returns the singleton Room database instance
  fun getDatabase(context: Context): RentalCarManagerDatabase {
    return INSTANCE ?: synchronized(this) {
      // Build the database if it doesn't exist yet
      val instance = Room.databaseBuilder(
        context.applicationContext,
        RentalCarManagerDatabase::class.java,
        "rental_car_manager_db" // Database name
      )
        .fallbackToDestructiveMigration() // Recreates database on schema mismatch (not for production use)
        .build()

      // Assign the new instance to INSTANCE
      INSTANCE = instance
      instance
    }
  }
}
