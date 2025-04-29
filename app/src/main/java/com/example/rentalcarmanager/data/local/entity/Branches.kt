package com.example.rentalcarmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branches")

data class Branches(
  @PrimaryKey(autoGenerate = true)
  val id :Int =0,
  val name:String,
  val location:String,
)
