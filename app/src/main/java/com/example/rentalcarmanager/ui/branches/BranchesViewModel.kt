package com.example.rentalcarmanager.ui.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalcarmanager.data.local.entity.Branches
import com.example.rentalcarmanager.data.local.repository.BranchesRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BranchesViewModel() : ViewModel() {

  private val repository = BranchesRepo

  // Collect all branches as a StateFlow for UI observation
  val allBranches: StateFlow<List<Branches>> = repository.getAllBranches()
    .stateIn(
      viewModelScope, // Scope tied to ViewModel lifecycle
      SharingStarted.WhileSubscribed(5000), // Keep the flow alive briefly when not observed
      emptyList() // Initial empty state
    )

  // Insert or update a branch
  fun upsertBranch(branch: Branches) {
    viewModelScope.launch {
      repository.upsertBranches(branch)
    }
  }

  // Delete a branch
  fun deleteBranch(branch: Branches) {
    viewModelScope.launch {
      repository.deleteBranches(branch)
    }
  }
}
