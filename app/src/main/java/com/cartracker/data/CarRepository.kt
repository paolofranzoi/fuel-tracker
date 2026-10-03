package com.cartracker.data

import androidx.lifecycle.LiveData

class CarRepository(private val carDao: CarDao) {
    val allCars: LiveData<List<Car>> = carDao.getAllCars()
    fun getCarByTarga(targa: String): LiveData<Car?> = carDao.getCarByTarga(targa)
    suspend fun insertCar(car: Car) = carDao.insertCar(car)
    suspend fun deleteCar(car: Car) = carDao.deleteCar(car)
}
