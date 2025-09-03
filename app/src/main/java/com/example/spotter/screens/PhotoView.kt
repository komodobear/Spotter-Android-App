package com.example.spotter.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun PhotoView(
	navController: NavController,
	imagePath: String?
) {
	if(imagePath == null) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color.Black.copy(alpha = 0.5f))
				.clickable { navController.popBackStack() },
			contentAlignment = Alignment.Center
		) {
			Text("No image to display", color = Color.White)
			Icon(
				imageVector = Icons.Filled.Close,
				contentDescription = "Close",
				tint = Color.White,
				modifier = Modifier
					.align(Alignment.TopEnd)
					.padding(16.dp)
					.size(32.dp)
					.clickable { navController.popBackStack() }
			)
		}
		return
	}

	Box(
		Modifier
			.fillMaxSize()
			.background(Color.Black.copy(alpha = 0.5f))
			.clickable(onClick = { navController.popBackStack() }),
		contentAlignment = Alignment.Center
	) {
		Box(
			Modifier
				.padding(8.dp)
				.fillMaxSize(0.97f)
				.clickable(enabled = false, onClick = {})
		) {
			if(imagePath.isNotEmpty() && File(imagePath).exists()) {
				Image(
					painter = rememberAsyncImagePainter(imagePath),
					contentDescription = "full photo",
					contentScale = ContentScale.Fit,
					modifier = Modifier.fillMaxSize()
				)
				Icon(
					imageVector = Icons.Filled.Close,
					contentDescription = "Close",
					tint = Color.White,
					modifier = Modifier
						.align(Alignment.TopEnd)
						.padding(8.dp)
						.size(32.dp)
						.clip(CircleShape)
						.clickable { navController.popBackStack() }
				)
			} else {
				Text("Can't load image", color = Color.White)
				Icon(
					imageVector = Icons.Filled.Close,
					contentDescription = "Close",
					tint = Color.White,
					modifier = Modifier
						.align(Alignment.TopEnd)
						.padding(16.dp)
						.size(32.dp)
						.clickable { navController.popBackStack() }
				)
			}
		}

	}
}



