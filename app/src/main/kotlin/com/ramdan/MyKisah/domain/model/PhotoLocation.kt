package com.ramdan.MyKisah.domain.model

data class PhotoLocation(
    val id: Long = 0,
    val imageUri: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val dateFormatted: String = "",
    val timeFormatted: String = ""
)
