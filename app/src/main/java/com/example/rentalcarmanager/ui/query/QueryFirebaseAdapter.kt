package com.example.rentalcarmanager.ui.query

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.remote.RentalFirestore
import com.example.rentalcarmanager.databinding.InfoQueriesBinding

class QueryFirebaseAdapter(
  private val carMap: Map<Int, String>,
  private val customerMap: Map<Int, String>,
  private val branchMap: Map<Int, String>
) : ListAdapter<RentalFirestore, QueryFirebaseAdapter.FirebaseViewHolder>(RentalDiffCallback()) {

  // Creates a new ViewHolder
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebaseViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = InfoQueriesBinding.inflate(inflater, parent, false)
    return FirebaseViewHolder(binding)
  }

  // Binds the data at the given position to the ViewHolder
  override fun onBindViewHolder(holder: FirebaseViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  // ViewHolder responsible for displaying a single rental item
  inner class FirebaseViewHolder(private val binding: InfoQueriesBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(rental: RentalFirestore) {
      // Get readable names using the provided maps
      val carName = carMap[rental.carId] ?: "Unknown car"
      val customerName = customerMap[rental.customerId] ?: "Unknown customer"
      val branchName = branchMap[rental.branchId] ?: "Unknown branch"

      // Bind data to the three lines of the InfoQueries layout
      binding.infoLine1.text = "Customer: $customerName"
      binding.infoLine2.text = "Car: $carName"
      binding.infoLine3.text = "Branch: $branchName\n${rental.rentalDate} ➜ ${rental.returnDate}"
    }
  }

  // DiffUtil for efficiently updating RecyclerView items
  class RentalDiffCallback : DiffUtil.ItemCallback<RentalFirestore>() {
    override fun areItemsTheSame(oldItem: RentalFirestore, newItem: RentalFirestore): Boolean =
      oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: RentalFirestore, newItem: RentalFirestore): Boolean =
      oldItem == newItem
  }
}
