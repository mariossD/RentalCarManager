package com.example.rentalcarmanager.ui.branches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.local.entity.Branches
import com.example.rentalcarmanager.databinding.InfoBranchBinding

class BranchesAdapter(
  private val onItemClick: (Branches) -> Unit,
  private val onDeleteClick: (Branches) -> Unit
) : ListAdapter<Branches, BranchesAdapter.BranchViewHolder>(BranchDiffCallback()) {

  // Called to create a new ViewHolder
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
    val binding = InfoBranchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return BranchViewHolder(binding)
  }

  // Called to bind data to a ViewHolder
  override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  // ViewHolder as an inner class (has access to outer class members if needed)
  inner class BranchViewHolder(private val binding: InfoBranchBinding) :
    RecyclerView.ViewHolder(binding.root) {

    // Bind data to views
    fun bind(branch: Branches) {
      binding.textBranchName.text = branch.name
      binding.textBranchLocation.text = branch.location

      // Handle item click
      binding.root.setOnClickListener {
        onItemClick(branch)
      }

      // Handle delete button click
      binding.buttonDelete.setOnClickListener {
        onDeleteClick(branch)
      }
    }
  }

  // DiffUtil optimizes updates for the RecyclerView
  class BranchDiffCallback : DiffUtil.ItemCallback<Branches>() {
    override fun areItemsTheSame(oldItem: Branches, newItem: Branches): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Branches, newItem: Branches): Boolean {
      return oldItem == newItem
    }
  }
}
