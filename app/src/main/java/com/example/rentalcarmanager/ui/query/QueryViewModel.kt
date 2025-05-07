package com.example.rentalcarmanager.ui.query

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalcarmanager.data.local.repository.BranchesRepo
import com.example.rentalcarmanager.data.local.repository.CarsRepo
import com.example.rentalcarmanager.data.local.repository.CustomersRepo
import com.example.rentalcarmanager.data.remote.RentalFirestore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class QueryViewModel : ViewModel() {

  // --- ROOM STATE ---

  // Get all branches from Room
  val branches = BranchesRepo.getAllBranches()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Get all cars from Room
  val allCars = CarsRepo.getAllCars()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Get all customers from Room
  val customers = CustomersRepo.getAllCustomers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Extract distinct brands from all cars
  val brands = allCars
    .map { list -> list.map { it.brand }.distinct() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Extract distinct categories from all cars
  val categories = allCars
    .map { list -> list.map { it.category }.distinct() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Generate a customerId ➜ customerName map
  val customerMap: StateFlow<Map<Int, String>> = customers
    .map { list -> list.associate { it.id to it.customersName } }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

  // --- ROOM QUERIES ---

  fun getCarsByBranch(branchId: Int) = CarsRepo.getCarsByBranch(branchId)
  fun getCarsByBrand(brand: String) = CarsRepo.getCarsByBrand(brand)
  fun getCarsByCategory(category: String) = CarsRepo.getCarsByCategory(category)

  // --- FIRESTORE STATE & LOGIC ---

  private val _rentals = MutableStateFlow<List<RentalFirestore>>(emptyList())
  val rentals: StateFlow<List<RentalFirestore>> = _rentals

  private val rentalsCollection = FirebaseFirestore.getInstance().collection("rentals")

  init {
    loadRentalsFromFirestore()
  }

  // Load all rentals from Firestore once at init
  fun loadRentalsFromFirestore() {
    rentalsCollection.get().addOnSuccessListener { snapshot ->
      val list = snapshot.documents.mapNotNull { doc ->
        doc.toObject(RentalFirestore::class.java)?.copy(id = doc.id)
      }
      _rentals.value = list
    }
  }

  // Return rentals where customer name matches the input
  fun getRentalsByCustomerName(name: String): Flow<List<RentalFirestore>> {
    return rentals.combine(customerMap) { rentalsList, customerMap ->
      rentalsList.filter { rental -> customerMap[rental.customerId] == name }
    }
  }

  // Rentals filtered by Branch Name (using branchId from Room)
  fun getRentalsByBranchName(branchName: String): Flow<List<RentalFirestore>> = rentals
    .combine(branches) { rentalsList, branchesList ->
      val branch = branchesList.find { it.name == branchName }
      if (branch != null) {
        rentalsList.filter { it.branchId == branch.id }
      } else emptyList()
    }

  // Rentals filtered by Car Brand (using carId from Room)
  fun getRentalsByCarBrand(brand: String): Flow<List<RentalFirestore>> = rentals
    .combine(allCars) { rentalsList, carsList ->
      val matchingCarIds = carsList.filter { it.brand == brand }.map { it.id }.toSet()
      rentalsList.filter { it.carId in matchingCarIds }
    }

}
