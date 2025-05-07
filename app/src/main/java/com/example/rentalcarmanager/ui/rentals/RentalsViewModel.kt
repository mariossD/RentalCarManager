package com.example.rentalcarmanager.ui.rentals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalcarmanager.data.local.repository.BranchesRepo
import com.example.rentalcarmanager.data.local.repository.CarsRepo
import com.example.rentalcarmanager.data.local.repository.CustomersRepo
import com.example.rentalcarmanager.data.remote.RentalFirestore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RentalsViewModel : ViewModel() {

  // StateFlow to expose the list of rentals from Firestore
  private val getRentalList = MutableStateFlow<List<RentalFirestore>>(emptyList())
  val rentals: StateFlow<List<RentalFirestore>> = getRentalList

  // Firebase Firestore reference
  private val firestore = FirebaseFirestore.getInstance()
  private val rentalsCollection = firestore.collection("rentals")

  // Load rentals when ViewModel is initialized
  init {
    loadRentalsFromFirestore()
  }

  // Fetch all rentals from Firestore and update the StateFlow
  fun loadRentalsFromFirestore() {
    rentalsCollection
      .get()
      .addOnSuccessListener { snapshot ->
        val list = snapshot.documents.mapNotNull { doc ->
          doc.toObject(RentalFirestore::class.java)?.copy(id = doc.id)
        }
        getRentalList.value = list
      }
  }
  // StateFlows from Room for dropdown usage (branches, cars, customers)
  val allBranches = BranchesRepo.getAllBranches()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allCars = CarsRepo.getAllCars()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allcustomers = CustomersRepo.getAllCustomers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
