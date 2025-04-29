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

  private val repository=BranchesRepo

  val allBranches: StateFlow<List<Branches>> = repository.getAllBranches()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

  fun upsertBranch(branch: Branches) {
    viewModelScope.launch {
      repository.upsertBranches(branch)
    }
  }

  fun deleteBranch(branch: Branches) {
    viewModelScope.launch {
      repository.deleteBranches(branch)
    }
  }
}
