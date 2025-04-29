package com.example.rentalcarmanager.ui.branches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalcarmanager.data.local.entity.Branches
import com.example.rentalcarmanager.databinding.ItemBranchBinding

class BranchesAdapter(
  private val onItemClick: (Branches) -> Unit
) : ListAdapter<Branches, BranchesAdapter.BranchViewHolder>(BranchDiffCallback()) {

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int)
    : BranchViewHolder {
    val binding = ItemBranchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return BranchViewHolder(binding)
  }

  override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
    val branch = getItem(position)
    holder.bind(branch)
    holder.itemView.setOnClickListener {
      onItemClick(branch)
    }
  }

  class BranchViewHolder(private val binding: ItemBranchBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(branch: Branches) {
      binding.textBranchName.text = branch.name
      binding.textBranchLocation.text = branch.location
    }
  }

  class BranchDiffCallback : DiffUtil.ItemCallback<Branches>() {
    override fun areItemsTheSame(oldItem: Branches, newItem: Branches): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Branches, newItem: Branches): Boolean {
      return oldItem == newItem
    }
  }
}
