package com.example.rentalcarmanager.ui.cars

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
import com.example.rentalcarmanager.data.local.entity.Cars
import com.example.rentalcarmanager.data.local.repository.CarsRepo
import com.example.rentalcarmanager.databinding.FragmentCarsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CarsFragment : Fragment() {

  private var _binding: FragmentCarsBinding? = null
  private val binding get() = _binding!!

  private val viewModel: CarsViewModel by viewModels()

  private lateinit var carAdapter: CarsAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentCarsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    CarsRepo.init(DatabaseProvider.getDatabase(requireContext()).daoCars())
    carAdapter = CarsAdapter { selectedCar ->
      showAddCarDialog(selectedCar)
    }

    setupRecyclerView()
    observeCars()
    setupButton()
  }

 private fun observeCars() {
   viewLifecycleOwner.lifecycleScope.launch {
      viewModel.allCars.collectLatest { cars ->
        carAdapter.submitList(cars)
      }
    }
 }

  private fun setupRecyclerView() {
    binding.recyclerView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = carAdapter
    }

  }
  private fun setupButton() {
    binding.buttonAddCustomer.setOnClickListener {
      showAddCarDialog()
    }
  }



  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun showAddCarDialog(car: Cars? = null) {
    context?.let { safeContext ->
      val dialogView = LayoutInflater.from(safeContext).inflate(R.layout.add_car_dialog, null)

      val brandEditText = dialogView.findViewById<EditText>(R.id.editTextBrand)
      val modelEditText = dialogView.findViewById<EditText>(R.id.editTextModel)
      val plateEditText = dialogView.findViewById<EditText>(R.id.editTextLicensePlate)
      val categoryEditText = dialogView.findViewById<EditText>(R.id.editTextCategory)

      car?.let {
        brandEditText.setText(it.brand)
        modelEditText.setText(it.model)
        plateEditText.setText(it.licensePlate)
        categoryEditText.setText(it.category)
      }

      val dialog = AlertDialog.Builder(safeContext)
        .setTitle(if (car == null) "Προσθήκη Αυτοκινήτου" else "Επεξεργασία Αυτοκινήτου")
        .setView(dialogView)
        .setPositiveButton(if (car == null) "Προσθήκη" else "Αποθήκευση") { _, _ ->
          val brand = brandEditText.text.toString()
          val model = modelEditText.text.toString()
          val plate = plateEditText.text.toString()
          val category = categoryEditText.text.toString()

          if (brand.isNotBlank() && model.isNotBlank() && plate.isNotBlank() && category.isNotBlank()) {
            val newCar = Cars(
              id = car?.id ?: 0,
              brand = brand,
              model = model,
              licensePlate = plate,
              category = category,
              branchId = 1  // ή ό,τι έχεις για branchId
            )
            viewModel.upsertCar(newCar)
          }
        }
        .setNegativeButton("Άκυρο", null)
        .create()

      dialog.show()
    }
  }
}
