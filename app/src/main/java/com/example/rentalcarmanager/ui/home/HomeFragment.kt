package com.example.rentalcarmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rentalcarmanager.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

  private var _binding: FragmentHomeBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    // Initialize ViewModel (currently not used)
    val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

    // Inflate the layout for this fragment
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    return root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
