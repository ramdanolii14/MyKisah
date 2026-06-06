package com.ramdan.MyKisah.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramdan.MyKisah.presentation.map.JourneyMapView
import com.ramdan.MyKisah.presentation.timeline.TimelineList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "sync")
    val syncRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing)),
        label = "sync_rotation"
    )
    val staticRotation = 0f
    val rotationState = if (uiState.isSyncing) syncRotation else staticRotation

    uiState.error?.let {
        LaunchedEffect(it) { viewModel.clearError() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MyKisah",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.syncPhotos() },
                        enabled = !uiState.isSyncing
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Sync,
                            contentDescription = "Sync",
                            modifier = Modifier.rotate(rotationState),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            JourneyMapView(
                photos = uiState.photos,
                selectedIndex = uiState.selectedIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.40f)
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp
            )

            AnimatedVisibility(
                visible = uiState.photos.isNotEmpty(),
                enter = fadeIn() + expandVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Perjalanan",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${uiState.photos.size} foto",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (uiState.isSyncing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Memuat data perjalanan...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            TimelineList(
                photos = uiState.photos,
                selectedIndex = uiState.selectedIndex,
                onPhotoClick = viewModel::selectPhoto,
                modifier = Modifier.weight(1f)
            )
        }
    }
}