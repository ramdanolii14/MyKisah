package com.ramdan.MyKisah.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_locations")
data class PhotoLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageUri: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
