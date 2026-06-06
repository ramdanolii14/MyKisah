package com.ramdan.MyKisah.data.local

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
import java.text.SimpleDateFormat
import java.util.Locale

object ExifParser {

    // format: "2024:01:15 14:30:00"
    private val exifDateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US)

    fun parse(context: Context, uri: Uri): ExifData? {
        return try {
            val stream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getStreamWithMediaLocation(context, uri)
            } else {
                context.contentResolver.openInputStream(uri)
            } ?: return null

            val exif = ExifInterface(stream)
            val latLon = FloatArray(2)
            val hasGps = exif.getLatLong(latLon)
            if (!hasGps) return null

            val dateStr = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
                ?: return null

            val date = exifDateFormat.parse(dateStr) ?: return null
            stream.close()

            ExifData(
                latitude = latLon[0].toDouble(),
                longitude = latLon[1].toDouble(),
                timestamp = date.time
            )
        } catch (e: Exception) {
            null
        }
    }

    // perbaiki sebelum deploy — pastikan ACCESS_MEDIA_LOCATION granted
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getStreamWithMediaLocation(context: Context, uri: Uri) =
        context.contentResolver.openInputStream(uri)

    data class ExifData(
        val latitude: Double,
        val longitude: Double,
        val timestamp: Long
    )
}
