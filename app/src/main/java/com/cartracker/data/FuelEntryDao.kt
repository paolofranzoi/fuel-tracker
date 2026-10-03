package com.cartracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FuelEntryDao {
    @Query("SELECT * FROM fuel_entries WHERE targa = :targa ORDER BY data DESC")
    fun getFuelEntriesByTarga(targa: String): LiveData<List<FuelEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelEntry(entry: FuelEntry)

    @Delete
    suspend fun deleteFuelEntry(entry: FuelEntry)

    @Query("DELETE FROM fuel_entries WHERE targa = :targa")
    suspend fun deleteAllForCar(targa: String)
}
