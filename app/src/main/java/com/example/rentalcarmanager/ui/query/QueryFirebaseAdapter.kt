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

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebaseViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = InfoQueriesBinding.inflate(inflater, parent, false)
    return FirebaseViewHolder(binding)
  }

  override fun onBindViewHolder(holder: FirebaseViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  inner class FirebaseViewHolder(private val binding: InfoQueriesBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(rental: RentalFirestore) {
      val carName = carMap[rental.carId] ?: "Unknown car"
      val customerName = customerMap[rental.customerId] ?: "Unknown customer"
      val branchName = branchMap[rental.branchId] ?: "Unknown branch"

      binding.infoLine1.text = "Customer: $customerName"
      binding.infoLine2.text = "Car: $carName"
      binding.infoLine3.text = "Branch: $branchName\n${rental.rentalDate} ➜ ${rental.returnDate}"
    }
  }

  class RentalDiffCallback : DiffUtil.ItemCallback<RentalFirestore>() {
    override fun areItemsTheSame(oldItem: RentalFirestore, newItem: RentalFirestore): Boolean =
      oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: RentalFirestore, newItem: RentalFirestore): Boolean =
      oldItem == newItem
  }
}
