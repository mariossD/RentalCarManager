package com.example.rentalcarmanager.data.remote


data class RentalFirestore(
  val id: String = "",
  val carId: Int = 0,
  val customerId: Int = 0,
  val branchId: Int = 0,
  val rentalDate: String = "",
  val returnDate: String = "",
)