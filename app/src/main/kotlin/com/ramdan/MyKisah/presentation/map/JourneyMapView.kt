package com.ramdan.MyKisah.presentation.map

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ramdan.MyKisah.domain.model.PhotoLocation
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun JourneyMapView(
    photos: List<PhotoLocation>,
    selectedIndex: Int?,
    modifier: Modifier = Modifier
) {
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    AndroidView(
        factory = { ctx ->
            initOsmdroid(ctx)
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(12.0)
                mapViewRef.value = this
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            if (photos.isNotEmpty()) {
                drawRoute(mapView, photos, selectedIndex)
            }
            mapView.invalidate()
        },
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
    )
}

// logic — inisialisasi config sekali saja
private fun initOsmdroid(context: Context) {
    Configuration.getInstance().apply {
        userAgentValue = "com.ramdan.MyKisah"
        load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    }
}

private fun drawRoute(mapView: MapView, photos: List<PhotoLocation>, selectedIndex: Int?) {
    val geoPoints = photos.map { GeoPoint(it.latitude, it.longitude) }

    // polyline rute
    if (geoPoints.size > 1) {
        val polyline = Polyline().apply {
            setPoints(geoPoints)
            outlinePaint.color = android.graphics.Color.parseColor("#E8A838")
            outlinePaint.strokeWidth = 5f
            outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
        }
        mapView.overlays.add(polyline)
    }

    // marker setiap foto
    photos.forEachIndexed { index, photo ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(photo.latitude, photo.longitude)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "${photo.dateFormatted} ${photo.timeFormatted}"
            // logic — marker selected bisa diberi ikon berbeda
        }
        mapView.overlays.add(marker)
    }

    // auto-fit bounding box
    if (geoPoints.size == 1) {
        mapView.controller.animateTo(geoPoints.first(), 14.0, 800)
    } else if (geoPoints.size > 1) {
        val box = BoundingBox.fromGeoPoints(geoPoints)
        mapView.zoomToBoundingBox(box.increaseByScale(1.2f), true, 80)
    }

    // scroll ke selected
    if (selectedIndex != null && selectedIndex < geoPoints.size) {
        mapView.controller.animateTo(geoPoints[selectedIndex], 15.0, 600)
    }
}
