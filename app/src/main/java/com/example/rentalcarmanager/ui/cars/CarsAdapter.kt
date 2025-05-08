package com.example.rentalcarmanager.ui.cars

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.local.entity.Cars
import com.example.rentalcarmanager.databinding.InfoCarBinding

class CarsAdapter(
  private val onItemClick: (Cars) -> Unit,
  private val onDeleteClick: (Cars) -> Unit
) : ListAdapter<Cars, CarsAdapter.CarViewHolder>(CarDiffCallback()) {

  // Called to create a new ViewHolder
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
    val binding = InfoCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return CarViewHolder(binding)
  }

  // Called to bind data to the ViewHolder at the given position
  override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  // ViewHolder class responsible for displaying a single car item
  inner class CarViewHolder(
    private val binding: InfoCarBinding
  ) : RecyclerView.ViewHolder(binding.root) {

    // Bind car data to views
    fun bind(car: Cars) {
      binding.textBrand.text = car.brand
      binding.textModel.text = car.model
      binding.textLicensePlate.text = car.licensePlate
      binding.textCategory.text = car.category

      // Click on the card opens edit dialog
      binding.root.setOnClickListener {
        onItemClick(car)
      }

      // Click on the delete icon triggers deletion
      binding.buttonDeleteCar.setOnClickListener {
        onDeleteClick(car)
      }
    }
  }

  // Callback to optimize RecyclerView updates by comparing item identity and content
  class CarDiffCallback : DiffUtil.ItemCallback<Cars>() {
    override fun areItemsTheSame(oldItem: Cars, newItem: Cars): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cars, newItem: Cars): Boolean {
      return oldItem == newItem
    }
  }
}
