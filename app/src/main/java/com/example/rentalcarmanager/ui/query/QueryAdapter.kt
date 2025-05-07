package com.example.rentalcarmanager.ui.query

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.local.entity.Cars
import com.example.rentalcarmanager.databinding.InfoQueriesBinding

class QueryAdapter(
  private var queryId: Int,
  private val branchMap: Map<Int, String>
) : ListAdapter<Cars, QueryAdapter.QueryViewHolder>(CarDiffCallback()) {

  fun updateQueryId(newId: Int) {
    queryId = newId
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = InfoQueriesBinding.inflate(inflater, parent, false)
    return QueryViewHolder(binding)
  }

  override fun onBindViewHolder(holder: QueryViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  inner class QueryViewHolder(private val binding: InfoQueriesBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(car: Cars) {
      val branchName = branchMap[car.branchId] ?: "Unknown"

      val (line1, line2, line3) = when (queryId) {
        1 -> listOf("Branch: $branchName", "Car: ${car.brand} ${car.model}", "Category: ${car.category}")
        2 -> listOf("Brand: ${car.brand}", "Model: ${car.model}", "Category: ${car.category}")
        3 -> listOf("Category: ${car.category}", "Car: ${car.brand} ${car.model}", "Branch: $branchName")
        else -> listOf("Car: ${car.brand} ${car.model}", "Category: ${car.category}", "Branch: $branchName")
      }

      binding.infoLine1.text = line1
      binding.infoLine2.text = line2
      binding.infoLine3.text = line3
    }
  }

  class CarDiffCallback : DiffUtil.ItemCallback<Cars>() {
    override fun areItemsTheSame(oldItem: Cars, newItem: Cars): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Cars, newItem: Cars): Boolean = oldItem == newItem
  }
}
