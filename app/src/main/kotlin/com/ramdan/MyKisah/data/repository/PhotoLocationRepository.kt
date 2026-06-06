package com.ramdan.MyKisah.data.repository

import android.content.Context
import com.ramdan.MyKisah.data.local.*
import com.ramdan.MyKisah.domain.model.PhotoLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoLocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: PhotoLocationDao,
    private val mediaStoreReader: MediaStoreReader
) {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val photoLocations: Flow<List<PhotoLocation>> = dao.getAllChronological().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun syncFromCamera() = withContext(Dispatchers.IO) {
        val uris = mediaStoreReader.getCameraPhotos()
        val entities = mutableListOf<PhotoLocationEntity>()

        for (uri in uris) {
            val exif = ExifParser.parse(context, uri) ?: continue
            entities.add(
                PhotoLocationEntity(
                    imageUri = uri.toString(),
                    latitude = exif.latitude,
                    longitude = exif.longitude,
                    timestamp = exif.timestamp
                )
            )
        }

        // logic — replace all setiap sync; bisa dioptimasi incremental
        dao.clearAll()
        dao.insertAll(entities)
    }

    private fun PhotoLocationEntity.toDomain(): PhotoLocation {
        val date = Date(timestamp)
        return PhotoLocation(
            id = id,
            imageUri = imageUri,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp,
            dateFormatted = dateFormat.format(date),
            timeFormatted = timeFormat.format(date)
        )
    }
}
