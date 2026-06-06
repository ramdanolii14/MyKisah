package com.ramdan.MyKisah.data.local

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreReader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // query hanya dari DCIM/Camera
    suspend fun getCameraPhotos(): List<Uri> = withContext(Dispatchers.IO) {
        val uris = mutableListOf<Uri>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.RELATIVE_PATH)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("DCIM/Camera%")
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} ASC"

        context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)
            ?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val uri = Uri.withAppendedPath(collection, id.toString())
                    uris.add(uri)
                }
            }
        uris
    }
}
