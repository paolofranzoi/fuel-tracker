package com.cartracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CarDao {
    @Query("SELECT * FROM cars ORDER BY targa ASC")
    fun getAllCars(): LiveData<List<Car>>

    @Query("SELECT * FROM cars WHERE targa = :targa LIMIT 1")
    fun getCarByTarga(targa: String): LiveData<Car?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)
}
