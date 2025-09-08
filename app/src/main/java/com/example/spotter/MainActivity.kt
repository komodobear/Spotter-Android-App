package com.example.spotter

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.spotter.screens.navigation
import com.example.spotter.ui.theme.SpotterAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpotterAppTheme {

                SystemAppearance()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navigation()
                }
            }
        }
    }
}

@Composable
fun SystemAppearance() {
    val view = LocalView.current
    val window = (view.context as Activity).window
    val darkTheme = isSystemInDarkTheme()
    val color = MaterialTheme.colorScheme.primary

    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = false
        }

        window.statusBarColor = when {
            darkTheme -> color
            else -> color
        }.toArgb()
        window.navigationBarColor = color.toArgb()
    }
}