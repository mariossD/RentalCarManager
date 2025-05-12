package com.example.rentalcarmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rentalcarmanager.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

  private var homeBinding: FragmentHomeBinding? = null
  private val binding get() = homeBinding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {


    // Inflate the layout for this fragment
    homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    return root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    homeBinding = null
  }
}
