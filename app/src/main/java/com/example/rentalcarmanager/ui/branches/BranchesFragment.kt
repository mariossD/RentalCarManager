package com.example.rentalcarmanager.ui.branches

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentalcarmanager.R
import com.example.rentalcarmanager.data.local.DatabaseProvider
import com.example.rentalcarmanager.data.local.entity.Branches
import com.example.rentalcarmanager.data.local.repository.BranchesRepo
import com.example.rentalcarmanager.databinding.FragmentBranchesBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BranchesFragment : Fragment() {

  private var _binding: FragmentBranchesBinding? = null
  private val binding get() = _binding!!

  private val viewModel: BranchesViewModel by viewModels()

  private lateinit var adapter: BranchesAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentBranchesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    BranchesRepo.init(DatabaseProvider.getDatabase(requireContext()).daoBranches())

    adapter = BranchesAdapter(
      onItemClick = { selectedBranch -> upsertBranch(selectedBranch) },
      onDeleteClick = { selectedBranch -> deleteBranch(selectedBranch) }
    )

    setupRecyclerView()
    observeBranches()
    setupButton()
  }

  private fun observeBranches() {
    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.allBranches.collectLatest { branches ->
        adapter.submitList(branches)
      }
    }
  }

  private fun setupRecyclerView() {
    binding.recyclerView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = this@BranchesFragment.adapter
    }
  }

  private fun setupButton() {
    binding.buttonAddCustomer.setOnClickListener {
      upsertBranch()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun upsertBranch(branch: Branches? = null) {
    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.upsert_branch, null)
    val nameEditText = dialogView.findViewById<EditText>(R.id.editTextBranchName)
    val locationEditText = dialogView.findViewById<EditText>(R.id.editTextBranchLocation)
    val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancelBranch)
    val buttonSave = dialogView.findViewById<Button>(R.id.buttonSaveBranch)

    branch?.let {
      nameEditText.setText(it.name)
      locationEditText.setText(it.location)
    }

    val dialog = AlertDialog.Builder(requireContext())
      .setView(dialogView)
      .create()

    buttonCancel.setOnClickListener {
      dialog.dismiss()
    }

    buttonSave.setOnClickListener {
      val name = nameEditText.text.toString()
      val location = locationEditText.text.toString()

      if (name.isNotBlank() && location.isNotBlank()) {
        val newBranch = Branches(
          id = branch?.id ?: 0,
          name = name,
          location = location)
        viewModel.upsertBranch(newBranch)
        dialog.dismiss()
      } else {
        Toast.makeText(requireContext(), "Συμπλήρωσε όλα τα πεδία", Toast.LENGTH_SHORT).show()
      }
    }

    dialog.show()
  }

  private fun deleteBranch(branch: Branches) {
    val view = LayoutInflater.from(requireContext()).inflate(R.layout.delete_branch, null)
    val buttonDelete = view.findViewById<Button>(R.id.buttonDeleteBranch)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancelBranch)

    val dialog = AlertDialog.Builder(requireContext())
      .setView(view)
      .create()

    buttonCancel.setOnClickListener { dialog.dismiss() }
    buttonDelete.setOnClickListener {
      viewModel.deleteBranch(branch)
      Toast.makeText(requireContext(), "Διαγράφηκε το υποκατάστημα", Toast.LENGTH_SHORT).show()
      dialog.dismiss()
    }

    dialog.show()
  }
}