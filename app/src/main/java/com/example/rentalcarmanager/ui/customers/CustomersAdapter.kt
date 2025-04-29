package com.example.rentalcarmanager.ui.customers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.local.entity.Customers
import com.example.rentalcarmanager.databinding.ItemCustomerBinding

class CustomersAdapter(
  private val onItemClick: (Customers) -> Unit
) : ListAdapter<Customers, CustomersAdapter.CustomerViewHolder>(CustomerDiffCallback()) {

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int)
  : CustomerViewHolder {
    val binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return CustomerViewHolder(binding)
  }

  override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
    val customer = getItem(position)
    holder.bind(customer)
    holder.itemView.setOnClickListener {
      onItemClick(customer)
    }
  }

  class CustomerViewHolder(private val binding: ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(customer: Customers) {
      binding.textCustomerName.text = customer.customersName
      binding.textCustomerIdNumber.text = customer.drivelLicenseNumber
      binding.textCustomerPhone.text = customer.phoneNumber
      binding.textCustomerIdNumber.text = customer.drivelLicenseNumber
    }
  }

  class CustomerDiffCallback : DiffUtil.ItemCallback<Customers>() {
    override fun areItemsTheSame(oldItem: Customers, newItem: Customers): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Customers, newItem: Customers): Boolean {
      return oldItem == newItem
    }
  }
}
