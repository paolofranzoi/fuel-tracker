package com.cartracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cartracker.R
import com.cartracker.data.FuelEntry
import com.cartracker.databinding.ActivityAddFuelBinding
import com.cartracker.viewmodel.CarViewModel
import com.cartracker.viewmodel.FuelEntryViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddFuelActivity : AppCompatActivity() {
    private lateinit var b: ActivityAddFuelBinding
    private val carVM: CarViewModel by viewModels()
    private val fuelVM: FuelEntryViewModel by viewModels()
    private var targa: String? = null
    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddFuelBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbar.setNavigationOnClickListener { finish() }

        targa = intent.getStringExtra("targa") ?: run { finish(); return }

        val fuelTypes = resources.getStringArray(R.array.fuel_types_array)
        b.dropdownFuel.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, fuelTypes))

        carVM.getCarByTarga(targa!!).observe(this) { car ->
            car?.let {
                val idx = fuelTypes.indexOf(it.tipoCarburantePreferito)
                if (idx >= 0) b.dropdownFuel.setText(fuelTypes[idx], false)
            }
        }

        updateDateLabel()
        b.editDate.setOnClickListener { showDatePicker() }
        b.buttonCancel.setOnClickListener { finish() }
        b.buttonSave.setOnClickListener { saveEntry() }
    }

    private fun updateDateLabel() {
        b.editDate.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(selectedDate)))
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance().apply { timeInMillis = selectedDate }
        DatePickerDialog(this, { _, y, m, d ->
            val nc = Calendar.getInstance().apply { set(y, m, d) }
            selectedDate = nc.timeInMillis
            updateDateLabel()
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            .apply { datePicker.maxDate = System.currentTimeMillis() }
            .show()
    }

    private fun saveEntry() {
        val tipo = b.dropdownFuel.text.toString().trim()
        val litri = b.editLitri.text.toString().replace(',', '.').toDoubleOrNull()
        val prezzo = b.editPrezzo.text.toString().replace(',', '.').toDoubleOrNull()
        val km = b.editKm.text.toString().toIntOrNull()

        if (tipo.isBlank()) {
            Toast.makeText(this, R.string.fuel_type_required, Toast.LENGTH_SHORT).show(); return
        }
        if (litri == null || litri <= 0) { b.editLitri.error = getString(R.string.invalid_liters); return }
        if (prezzo == null || prezzo <= 0) { b.editPrezzo.error = getString(R.string.invalid_price); return }

        fuelVM.insertFuelEntry(FuelEntry(
            data = selectedDate, targa = targa!!, tipoCarburante = tipo,
            litri = litri, prezzoTotale = prezzo, prezzoLitro = prezzo / litri,
            kmAttuali = km))
        Toast.makeText(this, R.string.fuel_saved, Toast.LENGTH_SHORT).show()
        finish()
    }
}
