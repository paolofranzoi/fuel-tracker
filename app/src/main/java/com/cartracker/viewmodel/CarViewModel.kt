package com.cartracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cartracker.data.AppDatabase
import com.cartracker.data.Car
import com.cartracker.data.CarRepository
import kotlinx.coroutines.launch

class CarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CarRepository
    val allCars: LiveData<List<Car>>

    init {
        val dao = AppDatabase.getInstance(application).carDao()
        repository = CarRepository(dao)
        allCars = repository.allCars
    }

    fun insertCar(car: Car) = viewModelScope.launch { repository.insertCar(car) }
    fun deleteCar(car: Car) = viewModelScope.launch { repository.deleteCar(car) }
    fun getCarByTarga(targa: String): LiveData<Car?> = repository.getCarByTarga(targa)
}
