package com.example.rentalcarmanager.ui.branches

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

  private val viewModel: BranchesViewModel by viewModels ()


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

    adapter = BranchesAdapter { selectedBranch ->
      showAddBranchDialog(selectedBranch)
    }

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

   private fun setupRecyclerView() {binding.recyclerView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      this.adapter = this@BranchesFragment.adapter
    }
   }

  private fun setupButton() {
    binding.buttonAddCustomer.setOnClickListener {
      showAddBranchDialog()
    }
  }


  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun showAddBranchDialog(branch: Branches? = null) {
    context?.let { safeContext ->
      val dialogView = LayoutInflater.from(safeContext).inflate(R.layout.add_branch_dialog, null)

      val nameEditText = dialogView.findViewById<EditText>(R.id.editTextBranchName)
      val locationEditText = dialogView.findViewById<EditText>(R.id.editTextBranchLocation)

      branch?.let {
        nameEditText.setText(it.name)
        locationEditText.setText(it.location)
      }

      val dialog = AlertDialog.Builder(safeContext)
        .setTitle(if (branch == null) "Προσθήκη Υποκαταστήματος" else "Επεξεργασία Υποκαταστήματος")
        .setView(dialogView)
        .setPositiveButton(if (branch == null) "Προσθήκη" else "Αποθήκευση") { _, _ ->
          val name = nameEditText.text.toString()
          val location = locationEditText.text.toString()

          if (name.isNotBlank() && location.isNotBlank()) {
            val newBranch = Branches(
              id = branch?.id ?: 0,
              name = name,
              location = location
            )
            viewModel.upsertBranch(newBranch)
          }
        }
        .setNegativeButton("Άκυρο", null)
        .create()

      dialog.show()
    }
  }

}


