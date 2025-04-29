package com.example.rentalcarmanager.data.local.repository

import com.example.rentalcarmanager.data.local.dao.DaoBranches
import com.example.rentalcarmanager.data.local.entity.Branches
import kotlinx.coroutines.flow.Flow

object BranchesRepo {

  private lateinit var daoBranches: DaoBranches

  fun init(daoBranches: DaoBranches) {
    this.daoBranches = daoBranches
  }

  fun getAllBranches():Flow<List<Branches>>{
    return daoBranches.getAllBranches()
  }

  suspend fun upsertBranches(branch: Branches){
    daoBranches.upsertBranch(branch)
  }

  suspend fun deleteBranches(branch: Branches){
    daoBranches.deleteBranch(branch)
  }
}