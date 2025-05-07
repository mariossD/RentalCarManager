package com.example.rentalcarmanager.ui.rentals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.remote.RentalFirestore
import com.example.rentalcarmanager.databinding.InfoRentalBinding

// Adapter for displaying rental items in a RecyclerView
class RentalsAdapter(
  private val carMap: Map<Int, String>,               // Maps carId to car display name
  private val customerMap: Map<Int, String>,          // Maps customerId to customer name
  private val branchMap: Map<Int, String>,            // Maps branchId to branch name
  private val onItemClick: (RentalFirestore) -> Unit, // Callback when an item is clicked (for edit)
  private val onDeleteClick: (RentalFirestore) -> Unit // Callback when delete button is clicked
) : RecyclerView.Adapter<RentalsAdapter.RentalViewHolder>() {

  private val rentals = mutableListOf<RentalFirestore>() // Internal list of rentals

  // Updates the list and refreshes the RecyclerView
  fun submitList(newList: List<RentalFirestore>) {
    rentals.clear()
    rentals.addAll(newList)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalViewHolder {
    val binding = InfoRentalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return RentalViewHolder(binding)
  }

  override fun onBindViewHolder(holder: RentalViewHolder, position: Int) {
    holder.bind(rentals[position])
  }

  override fun getItemCount(): Int = rentals.size

  // ViewHolder class responsible for binding rental data to the layout
  inner class RentalViewHolder(private val binding: InfoRentalBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(rental: RentalFirestore) {
      // Get display strings using the maps
      val carText = carMap[rental.carId] ?: "Unknown"
      val customerText = customerMap[rental.customerId] ?: "Unknown"
      val branchText = branchMap[rental.branchId] ?: "Unknown"

      // Set the data to the views
      binding.textRentalCar.text = "Car: $carText"
      binding.textRentalCustomer.text = "Customer: $customerText"
      binding.textRentalBranch.text = "Branch: $branchText"
      binding.textRentalDates.text = "${rental.rentalDate} ➜ ${rental.returnDate}"

      // Handle delete button click
      binding.buttonDelete.setOnClickListener {
        onDeleteClick(rental)
      }

      // Handle entire item click (for edit)
      binding.root.setOnClickListener {
        onItemClick(rental)
      }
    }
  }
}
