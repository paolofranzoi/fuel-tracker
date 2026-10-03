package com.cartracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cartracker.R
import com.cartracker.data.Car
import com.cartracker.databinding.ActivityMainBinding
import com.cartracker.databinding.DialogAddCarBinding
import com.cartracker.viewmodel.CarViewModel
import com.cartracker.viewmodel.FuelEntryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val carViewModel: CarViewModel by viewModels()
    private val fuelEntryViewModel: FuelEntryViewModel by viewModels()
    private lateinit var adapter: CarAdapter
    private var selectedTarga: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = CarAdapter(
            onCarClick = { selectCar(it) },
            onCarLongClick = { car, v -> showCarContextMenu(car, v) }
        )
        binding.recyclerCars.layoutManager = LinearLayoutManager(this)
        binding.recyclerCars.adapter = adapter

        carViewModel.allCars.observe(this) { cars ->
            adapter.submitList(cars)
            updateEmptyState(cars)
        }

        binding.fabAddCar.setOnClickListener { showAddCarDialog() }

        binding.buttonAddFuel.setOnClickListener {
            if (selectedTarga == null) {
                Toast.makeText(this, R.string.select_vehicle_first, Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, AddFuelActivity::class.java)
                    .putExtra("targa", selectedTarga))
            }
        }
        binding.buttonCharts.setOnClickListener {
            if (selectedTarga == null) {
                Toast.makeText(this, R.string.select_vehicle_first, Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, ChartsActivity::class.java)
                    .putExtra("targa", selectedTarga))
            }
        }
    }

    private fun updateEmptyState(cars: List<Car>) {
        binding.emptyState.visibility = if (cars.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerCars.visibility = if (cars.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun selectCar(car: Car) {
        selectedTarga = car.targa
        adapter.setSelectedTarga(car.targa)
        binding.textSelectedVehicle.text = getString(R.string.selected_vehicle, car.nome ?: car.targa)
        binding.textSelectedVehicle.visibility = View.VISIBLE
    }

    private fun showCarContextMenu(car: Car, anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 1, 0, R.string.menu_view_details)
        popup.menu.add(0, 2, 1, R.string.menu_delete)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                1 -> { selectCar(car); true }
                2 -> { confirmDeleteCar(car); true }
                else -> false
            }
        }
        popup.show()
    }

    private fun confirmDeleteCar(car: Car) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(getString(R.string.confirm_delete_message, car.nome ?: car.targa, car.targa))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                fuelEntryViewModel.deleteAllForCar(car.targa)
                carViewModel.deleteCar(car)
                if (selectedTarga == car.targa) {
                    selectedTarga = null
                    adapter.setSelectedTarga(null)
                    binding.textSelectedVehicle.visibility = View.GONE
                }
                Toast.makeText(this, R.string.vehicle_deleted, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showAddCarDialog() {
        val db = DialogAddCarBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_vehicle)
            .setView(db.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.add, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nome = db.editNome.text.toString().trim().ifBlank { null }
                val targa = db.editTarga.text.toString().trim().uppercase()
                val carb = db.editCarburante.text.toString().trim()
                    .ifBlank { getString(R.string.default_fuel) }

                if (targa.isBlank()) {
                    db.editTarga.error = getString(R.string.plate_required); return@setOnClickListener
                }
                carViewModel.insertCar(Car(targa, nome, carb))
                Toast.makeText(this, R.string.vehicle_added, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
