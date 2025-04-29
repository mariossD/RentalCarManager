package com.example.rentalcarmanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
  tableName = "rentals",
  foreignKeys = [
    ForeignKey(
      entity = Cars::class,
      parentColumns = ["id"],
      childColumns = ["carId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = Customers::class,
      parentColumns = ["id"],
      childColumns = ["customerId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = Branches::class,
      parentColumns = ["id"],
      childColumns = ["branchId"],
      onDelete = ForeignKey.CASCADE
)
  ]
)

data class Rentals(
  @PrimaryKey(autoGenerate = true)
   val id: Int = 0,
   val carId: Int,
   val customerId: Int,
   val branchId: Int,
   val rentalDate: String,
   val returnDate: String
)
