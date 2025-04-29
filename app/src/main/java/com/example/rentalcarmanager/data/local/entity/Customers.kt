package com.example.rentalcarmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "customers")
data class Customers(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val customersName: String,
  val drivelLicenseNumber: String,
  val phoneNumber: String
)
