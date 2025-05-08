package com.example.rentalcarmanager.ui.query

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentalcarmanager.R
import com.example.rentalcarmanager.databinding.FragmentQueryBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QueryFragment : Fragment() {

  private var _binding: FragmentQueryBinding? = null
  private val binding get() = _binding!!

  private val viewModel: QueryViewModel by viewModels()

  private lateinit var roomAdapter: QueryRoomAdapter
  private lateinit var firebaseAdapter: QueryFirebaseAdapter

  private var branchMap: Map<Int, String> = emptyMap()
  private var carMap: Map<Int, String> = emptyMap()
  private var customerMap: Map<Int, String> = emptyMap()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentQueryBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Load car info for Firebase queries
    lifecycleScope.launch {
      viewModel.allCars.collectLatest { cars ->
        carMap = cars.associate { it.id to "${it.brand} ${it.model}" }
      }
    }

    // Load branches for mapping and room adapter initialization
    lifecycleScope.launch {
      viewModel.branches.collectLatest { branches ->
        branchMap = branches.associate { it.id to it.name }
        roomAdapter = QueryRoomAdapter(0, branchMap)
        binding.recyclerViewQueries.adapter = roomAdapter
      }
    }

    // Load customers for Firebase queries
    lifecycleScope.launch {
      viewModel.customers.collectLatest { customers ->
        customerMap = customers.associate { it.id to it.customersName }
      }
    }

    // Prepare Firebase adapter once data is available
    lifecycleScope.launch {
      viewModel.rentals.collectLatest {
        viewModel.branches.value.let { branches ->
          viewModel.allCars.value.let { cars ->
            carMap = cars.associate { it.id to "${it.brand} ${it.model}" }
            firebaseAdapter = QueryFirebaseAdapter(carMap, customerMap, branchMap)
          }
        }
      }
    }

    binding.recyclerViewQueries.layoutManager = LinearLayoutManager(requireContext())

    // Button listeners
    binding.roomQuery.setOnClickListener { showRoomDialog() }
    binding.firebaseQuery.setOnClickListener { firebaseQuery() }
  }

  // Dialog for Room-based local queries
  private fun showRoomDialog() {
    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.room_query, null)

    val dropdownQuery = dialogView.findViewById<AutoCompleteTextView>(R.id.dropdown_select_query)
    val dropdownValue = dialogView.findViewById<AutoCompleteTextView>(R.id.dropdown_select_value)
    val buttonSearch = dialogView.findViewById<Button>(R.id.button_room_search)

    val queryOptions = listOf("Cars by Branch", "Cars by Brand", "Cars by Category")
    dropdownQuery.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, queryOptions))

    var selectedQueryId = 0
    var selectedValue = ""

    val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

    dropdownQuery.setOnItemClickListener { _, _, position, _ ->
      selectedQueryId = position + 1

      // Load values for the selected query type
      lifecycleScope.launch {
        when (selectedQueryId) {
          1 -> viewModel.branches.collectLatest {
            val items = it.map { branch -> branch.name }
            dropdownValue.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items))
          }
          2 -> viewModel.brands.collectLatest {
            dropdownValue.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it))
          }
          3 -> viewModel.categories.collectLatest {
            dropdownValue.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it))
          }
        }
      }
    }

    buttonSearch.setOnClickListener {
      selectedValue = dropdownValue.text.toString()
      lifecycleScope.launch {
        when (selectedQueryId) {
          1 -> {
            val branch = viewModel.branches.value.find { it.name == selectedValue }
            branch?.let {
              roomAdapter.updateQueryId(selectedQueryId)
              roomAdapter.submitList(viewModel.getCarsByBranch(it.id).stateIn(lifecycleScope).value)
              binding.recyclerViewQueries.adapter = roomAdapter
            }
          }
          2 -> {
            roomAdapter.updateQueryId(selectedQueryId)
            roomAdapter.submitList(viewModel.getCarsByBrand(selectedValue).stateIn(lifecycleScope).value)
            binding.recyclerViewQueries.adapter = roomAdapter
          }
          3 -> {
            roomAdapter.updateQueryId(selectedQueryId)
            roomAdapter.submitList(viewModel.getCarsByCategory(selectedValue).stateIn(lifecycleScope).value)
            binding.recyclerViewQueries.adapter = roomAdapter
          }
        }
        dialog.dismiss()
      }
    }

    dialog.show()
  }

  // Dialog for Firebase-based rental queries
  private fun firebaseQuery() {
    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.room_query, null)

    val dropdownQuery = dialogView.findViewById<AutoCompleteTextView>(R.id.dropdown_select_query)
    val dropdownValue = dialogView.findViewById<AutoCompleteTextView>(R.id.dropdown_select_value)
    val buttonSearch = dialogView.findViewById<Button>(R.id.button_room_search)

    val queryOptions = listOf(
      "Rentals by Customer",
      "Rentals by Branch Name",
      "Rentals by Car Brand"
    )
    dropdownQuery.setAdapter(
      ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, queryOptions)
    )

    val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

    dropdownQuery.setOnItemClickListener { _, _, position, _ ->
      when (queryOptions[position]) {
        "Rentals by Customer" -> {
          lifecycleScope.launch {
            viewModel.customers.collectLatest { list ->
              val names = list.map { it.customersName }
              dropdownValue.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
              )
            }
          }
        }

        "Rentals by Branch Name" -> {
          lifecycleScope.launch {
            viewModel.branches.collectLatest { list ->
              val names = list.map { it.name }
              dropdownValue.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
              )
            }
          }
        }

        "Rentals by Car Brand" -> {
          lifecycleScope.launch {
            viewModel.brands.collectLatest { list ->
              dropdownValue.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, list)
              )
            }
          }
        }
      }
    }

    buttonSearch.setOnClickListener {
      val selectedQuery = dropdownQuery.text.toString()
      val selectedValue = dropdownValue.text.toString()

      lifecycleScope.launch {
        when (selectedQuery) {
          "Rentals by Customer" -> viewModel.getRentalsByCustomerName(selectedValue).collectLatest {
            firebaseAdapter.submitList(it)
            binding.recyclerViewQueries.adapter = firebaseAdapter
          }

          "Rentals by Branch Name" -> viewModel.getRentalsByBranchName(selectedValue).collectLatest {
            firebaseAdapter.submitList(it)
            binding.recyclerViewQueries.adapter = firebaseAdapter
          }

          "Rentals by Car Brand" -> viewModel.getRentalsByCarBrand(selectedValue).collectLatest {
            firebaseAdapter.submitList(it)
            binding.recyclerViewQueries.adapter = firebaseAdapter
          }
        }
      }
      dialog.dismiss()
    }

    dialog.show()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
