package com.example.rentalcarmanager.ui.rentals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentalcarmanager.data.local.DatabaseProvider
import com.example.rentalcarmanager.data.local.repository.RentalsRepo
import com.example.rentalcarmanager.databinding.FragmentRentalsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RentalsFragment : Fragment() {

  private var _binding: FragmentRentalsBinding? = null
  private val binding get() = _binding!!

  private val viewModel: RentalsViewModel by viewModels ()


  private lateinit var rentalsAdapter: RentalsAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentRentalsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    RentalsRepo.init(DatabaseProvider.getDatabase(requireContext()).daoRentals())

    rentalsAdapter = RentalsAdapter { selectedRental ->
    }
    setupRecuclerView()
    observeRentals()
  }

  private fun setupRecuclerView(){
    binding.recyclerView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter= rentalsAdapter
    }
  }

  private fun observeRentals() {
    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.allRentals.collectLatest { rentals ->
        rentalsAdapter.submitList(rentals)
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }


}
