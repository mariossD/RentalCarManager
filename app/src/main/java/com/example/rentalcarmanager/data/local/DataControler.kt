package com.example.rentalcarmanager.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

  @Volatile
  private var INSTANCE: RentalCarManagerDatabase? = null

  fun getDatabase(context: Context): RentalCarManagerDatabase {
    return INSTANCE ?: synchronized(this) {
      val instance = Room.databaseBuilder(
        context.applicationContext,
        RentalCarManagerDatabase::class.java,
        "rental_car_manager_db" 
      )
        .fallbackToDestructiveMigration()
        .build()

      INSTANCE = instance
      instance
    }
  }
}
