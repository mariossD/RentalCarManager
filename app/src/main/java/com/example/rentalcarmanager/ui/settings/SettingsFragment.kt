package com.example.rentalcarmanager.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.rentalcarmanager.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

  private var _binding: FragmentSettingsBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentSettingsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Detect current mode and set switch position
    val isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    binding.switchTheme.isChecked = isDark

    // Handle switch toggle
    binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
      AppCompatDelegate.setDefaultNightMode(
        if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
      )
      requireActivity().recreate() // Restart to apply theme
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
