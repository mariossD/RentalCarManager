package com.example.rentalcarmanager.ui.customers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.local.entity.Customers
import com.example.rentalcarmanager.databinding.InfoCustomerBinding

class CustomersAdapter(
  private val onItemClick: (Customers) -> Unit,
  private val onDeleteClick: (Customers) -> Unit
) : ListAdapter<Customers, CustomersAdapter.CustomerViewHolder>(CustomerDiffCallback()) {

  // Called when a new ViewHolder is created
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
    val binding = InfoCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return CustomerViewHolder(binding)
  }

  // Binds data to the ViewHolder at the given position
  override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
    val customer = getItem(position)
    holder.bind(customer)
  }

  // ViewHolder responsible for a single customer item
  inner class CustomerViewHolder(private val binding: InfoCustomerBinding) : RecyclerView.ViewHolder(binding.root) {

    // Bind customer data to the UI
    fun bind(customer: Customers) {
      binding.textCustomerName.text = customer.customersName
      binding.textCustomerIdNumber.text = customer.drivelLicenseNumber
      binding.textCustomerPhone.text = customer.phoneNumber

      // Handle item click (e.g., edit)
      binding.root.setOnClickListener {
        onItemClick(customer)
      }

      // Handle delete button click
      binding.buttonDeleteCustomer.setOnClickListener {
        onDeleteClick(customer)
      }
    }
  }

  // DiffUtil for efficient updates to the RecyclerView
  class CustomerDiffCallback : DiffUtil.ItemCallback<Customers>() {
    override fun areItemsTheSame(oldItem: Customers, newItem: Customers): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Customers, newItem: Customers): Boolean {
      return oldItem == newItem
    }
  }
}
