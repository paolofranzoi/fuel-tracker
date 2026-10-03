package com.cartracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_entries")
data class FuelEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val data: Long,
    val targa: String,
    val tipoCarburante: String,
    val litri: Double,
    val prezzoTotale: Double,
    val prezzoLitro: Double,
    val kmAttuali: Int?
)
