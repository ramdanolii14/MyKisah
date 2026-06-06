package com.ramdan.MyKisah

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.ramdan.MyKisah.presentation.MainScreen
import com.ramdan.MyKisah.presentation.permissions.PermissionScreen
import com.ramdan.MyKisah.presentation.theme.MyKisahTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var permissionsGranted by mutableStateOf(false)

    // perbaiki sebelum deploy — cek ulang status permission saat resume
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionsGranted = results.values.all { it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // cek permission awal
        permissionsGranted = checkPermissionsGranted()

        setContent {
            MyKisahTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = permissionsGranted,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "permission_transition"
                    ) { granted ->
                        if (granted) {
                            MainScreen()
                        } else {
                            PermissionScreen(
                                onGranted = { requestPermissions() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(perms)
    }

    private fun checkPermissionsGranted(): Boolean {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return perms.all { checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED }
    }
}