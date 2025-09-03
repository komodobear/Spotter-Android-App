package com.example.spotter.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(
    title: String,
    onBackNavClicked: () -> Unit={}
){
    val context = LocalContext.current
    val showToast = title == "Spotter"

    val navIcon: (@Composable () -> Unit) = {
        if(!title.contains("Spotter")){
            IconButton(onClick = { onBackNavClicked() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = "back"
                )
            }
        }
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
                    .clickable(
                        enabled = showToast,
                        onClick = {
                            Toast.makeText(context,"Hi there",Toast.LENGTH_LONG).show()
                        }
                    )
            )
        },
        elevation = 3.dp,
        backgroundColor = MaterialTheme.colorScheme.primary,
        navigationIcon = navIcon,
    )
}