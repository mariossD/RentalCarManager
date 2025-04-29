package com.example.rentalcarmanager.ui.rentals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.local.entity.Rentals
import com.example.rentalcarmanager.databinding.ItemRentalBinding

class RentalsAdapter(
  private val onItemClick: (Rentals) -> Unit
) : ListAdapter<Rentals, RentalsAdapter.RentalViewHolder>(RentalDiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalViewHolder {
    val binding = ItemRentalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return RentalViewHolder(binding)
  }

  override fun onBindViewHolder(holder: RentalViewHolder, position: Int) {
    val rental = getItem(position)
    holder.bind(rental)
    holder.itemView.setOnClickListener {
      onItemClick(rental)
    }
  }

  class RentalViewHolder(private val binding: ItemRentalBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(rental: Rentals) {
      binding.textRentalCar.text = "Car ID: ${rental.carId}"
      binding.textRentalCustomer.text = "Customer ID: ${rental.customerId}"
      binding.textRentalDates.text = "From: ${rental.rentalDate} To: ${rental.returnDate}"
    }
  }

  class RentalDiffCallback : DiffUtil.ItemCallback<Rentals>() {
    override fun areItemsTheSame(oldItem: Rentals, newItem: Rentals): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Rentals, newItem: Rentals): Boolean {
      return oldItem == newItem
    }
  }
}
