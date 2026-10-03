package com.cartracker.data

import androidx.lifecycle.LiveData

class FuelEntryRepository(private val fuelEntryDao: FuelEntryDao) {
    fun getFuelEntriesByTarga(targa: String): LiveData<List<FuelEntry>> =
        fuelEntryDao.getFuelEntriesByTarga(targa)
    suspend fun insertFuelEntry(entry: FuelEntry) = fuelEntryDao.insertFuelEntry(entry)
    suspend fun deleteFuelEntry(entry: FuelEntry) = fuelEntryDao.deleteFuelEntry(entry)
    suspend fun deleteAllForCar(targa: String) = fuelEntryDao.deleteAllForCar(targa)
}
