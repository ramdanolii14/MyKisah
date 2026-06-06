package com.ramdan.MyKisah.presentation.timeline

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ramdan.MyKisah.domain.model.PhotoLocation

@Composable
fun TimelineList(
    photos: List<PhotoLocation>,
    selectedIndex: Int?,
    onPhotoClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        if (selectedIndex != null) listState.animateScrollToItem(selectedIndex)
    }

    if (photos.isEmpty()) {
        EmptyTimeline(modifier = modifier)
        return
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        itemsIndexed(photos, key = { _, p -> p.id }) { index, photo ->
            TimelineItem(
                photo = photo,
                isSelected = selectedIndex == index,
                isFirst = index == 0,
                isLast = index == photos.lastIndex,
                onClick = { onPhotoClick(index) }
            )
        }
    }
}

@Composable
private fun TimelineItem(
    photo: PhotoLocation,
    isSelected: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // connector garis vertikal + dot
        TimelineConnector(isSelected = isSelected, isFirst = isFirst, isLast = isLast)

        // card foto
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInHorizontally { it / 4 }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable(onClick = onClick),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface,
                tonalElevation = if (isSelected) 2.dp else 0.dp,
                shadowElevation = if (isSelected) 4.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = Uri.parse(photo.imageUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = photo.dateFormatted,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = photo.timeFormatted,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "%.4f, %.4f".format(photo.latitude, photo.longitude),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineConnector(isSelected: Boolean, isFirst: Boolean, isLast: Boolean) {
    val dotColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline
    val lineColor = MaterialTheme.colorScheme.outline

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(20.dp)
    ) {
        // top line
        if (!isFirst) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(12.dp)
                    .background(lineColor)
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // dot
        Box(
            modifier = Modifier
                .size(if (isSelected) 14.dp else 10.dp)
                .background(dotColor, CircleShape)
                .then(
                    if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    else Modifier
                )
        )

        // bottom line
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .defaultMinSize(minHeight = 50.dp)
                    .background(lineColor)
            )
        }
    }
}

@Composable
private fun EmptyTimeline(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Belum ada kisah",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Tap ikon sync untuk memuat foto dari kamera",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
