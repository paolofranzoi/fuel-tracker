package com.cartracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey val targa: String,
    val nome: String?,
    val tipoCarburantePreferito: String
)
