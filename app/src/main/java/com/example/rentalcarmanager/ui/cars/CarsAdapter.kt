package com.example.rentalcarmanager.ui.cars

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.local.entity.Cars
import com.example.rentalcarmanager.databinding.ItemCarBinding

class CarsAdapter (
  private val onItemClick: (Cars) -> Unit)
  : ListAdapter<Cars, CarsAdapter.CarViewHolder>(CarDiffCallback())
{
    override fun onCreateViewHolder(
      parent: ViewGroup,
      viewType: Int)
    : CarViewHolder {
    val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return CarViewHolder(binding)
  }

  override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
    val car = getItem(position)
    holder.bind(car)
    holder.itemView.setOnClickListener {
      onItemClick(car)
    }
  }

  class CarViewHolder(private val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(car: Cars) {
      binding.textBrand.text = car.brand
      binding.textModel.text = car.model
      binding.textLicensePlate.text = car.licensePlate
      binding.textCategory.text = car.category
    }
  }

  class CarDiffCallback : DiffUtil.ItemCallback<Cars>() {
    override fun areItemsTheSame(oldItem: Cars, newItem: Cars): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cars, newItem: Cars): Boolean {
      return oldItem == newItem
    }
  }
  }
