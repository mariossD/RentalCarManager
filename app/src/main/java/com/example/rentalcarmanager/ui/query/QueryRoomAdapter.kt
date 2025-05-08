package com.example.rentalcarmanager.ui.query

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.local.entity.Cars
import com.example.rentalcarmanager.databinding.InfoQueriesBinding

class QueryRoomAdapter(
  private var queryId: Int,
  private val branchMap: Map<Int, String>
) : ListAdapter<Cars, QueryRoomAdapter.QueryViewHolder>(CarDiffCallback()) {

  // Updates the query type (used to decide how lines are displayed)
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

  // ViewHolder responsible for displaying a single query result row
  inner class QueryViewHolder(private val binding: InfoQueriesBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(car: Cars) {
      // Get the branch name from the map using car's branch ID
      val branchName = branchMap[car.branchId] ?: "Unknown"

      // Choose the text lines to display based on queryId
      val (line1, line2, line3) = when (queryId) {
        1 -> listOf("Branch: $branchName", "Car: ${car.brand} ${car.model}", "Category: ${car.category}")
        2 -> listOf("Brand: ${car.brand}", "Model: ${car.model}", "Category: ${car.category}")
        3 -> listOf("Category: ${car.category}", "Car: ${car.brand} ${car.model}", "Branch: $branchName")
        else -> listOf("Car: ${car.brand} ${car.model}", "Category: ${car.category}", "Branch: $branchName")
      }

      // Assign text to UI components
      binding.infoLine1.text = line1
      binding.infoLine2.text = line2
      binding.infoLine3.text = line3
    }
  }

  // Optimizes RecyclerView updates by comparing item identity and content
  class CarDiffCallback : DiffUtil.ItemCallback<Cars>() {
    override fun areItemsTheSame(oldItem: Cars, newItem: Cars): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Cars, newItem: Cars): Boolean = oldItem == newItem
  }
}
