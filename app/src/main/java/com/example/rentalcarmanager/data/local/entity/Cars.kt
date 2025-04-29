package com.example.rentalcarmanager.data.local.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
 tableName = "cars",
 foreignKeys = [
  ForeignKey(
   entity = Branches::class,
   parentColumns = ["id"],
   childColumns = ["branchId"],
   onDelete = androidx.room.ForeignKey.CASCADE
  )
 ]
)
data class Cars(
 @PrimaryKey(autoGenerate = true)
  val id: Int=0,
  val brand:String,
  val model:String,
  val licensePlate:String,
  val category:String,
  val branchId:Int
)
