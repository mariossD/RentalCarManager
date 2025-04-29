package com.example.rentalcarmanager.data.local.repository

import com.example.rentalcarmanager.data.local.dao.DaoCustomers
import com.example.rentalcarmanager.data.local.entity.Customers
import kotlinx.coroutines.flow.Flow

object CustomersRepo {

  private lateinit var daoCustomers: DaoCustomers

  fun init(daoCustomers: DaoCustomers) {
    this.daoCustomers = daoCustomers
  }

  fun getAllCustomers(): Flow<List<Customers>> {
    return daoCustomers.getAllCustomers()
  }

  suspend fun upsertCustomer(customer: Customers) {
    daoCustomers.upsertCustomer(customer)
  }
  suspend fun deleteCustomer(customer: Customers) {
    daoCustomers.deleteCustomer(customer)
  }
}