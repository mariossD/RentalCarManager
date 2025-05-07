package com.example.rentalcarmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.rentalcarmanager.data.local.entity.Branches
import kotlinx.coroutines.flow.Flow


@Dao
interface DaoBranches {

  @Upsert
  suspend fun upsertBranch(branch: Branches)

  @Delete
  suspend fun deleteBranch(branch: Branches)

  @Query("SELECT * FROM branches")
  fun getAllBranches(): Flow<List<Branches>>

  @Query("DELETE FROM branches WHERE id = :branchId")
  suspend fun deleteById(branchId: Int)



}