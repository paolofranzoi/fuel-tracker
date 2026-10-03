package com.cartracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cartracker.data.AppDatabase
import com.cartracker.data.FuelEntry
import com.cartracker.data.FuelEntryRepository
import kotlinx.coroutines.launch

class FuelEntryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FuelEntryRepository

    init {
        val dao = AppDatabase.getInstance(application).fuelEntryDao()
        repository = FuelEntryRepository(dao)
    }

    fun getFuelEntriesByTarga(targa: String): LiveData<List<FuelEntry>> =
        repository.getFuelEntriesByTarga(targa)
    fun insertFuelEntry(entry: FuelEntry) = viewModelScope.launch { repository.insertFuelEntry(entry) }
    fun deleteFuelEntry(entry: FuelEntry) = viewModelScope.launch { repository.deleteFuelEntry(entry) }
    fun deleteAllForCar(targa: String) = viewModelScope.launch { repository.deleteAllForCar(targa) }
}
