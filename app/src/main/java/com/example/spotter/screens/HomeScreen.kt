package com.example.spotter.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spotter.SpotVM
import com.example.spotter.screens.NavScreen
import com.example.spotter.R
import com.example.spotter.database.SpotData

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
	viewModel: SpotVM,
	navController: NavController
) {
	val scaffoldState = rememberScaffoldState()

	val bg = if(isSystemInDarkTheme()) {
		painterResource(id = R.raw.darkbg)
	} else {
		painterResource(id = R.raw.lightbg)
	}

	Box(modifier = Modifier.fillMaxSize()) {
		Image(
			painter = bg,
			contentDescription = "Background",
			modifier = Modifier
				.fillMaxSize(),
			contentScale = ContentScale.Crop
		)

		Scaffold(
			scaffoldState = scaffoldState,
			topBar = { TopBar(title = "Spotter") },
			floatingActionButton = {
				FloatingActionButton(
					onClick = { navController.navigate(NavScreen.AddScreen.route + "/0L") },
					modifier = Modifier.padding(all = 20.dp),
					contentColor = Color.White,
					containerColor = MaterialTheme.colorScheme.primary
				) {
					Icon(imageVector = Icons.Default.Add, contentDescription = "add")
				}
			},
			bottomBar = { BottomBar(navController) },
			backgroundColor = Color.Transparent
		) {
			val spotlist = viewModel.allSpotsFlow.collectAsState(initial = listOf())
			LazyColumn(
				modifier = Modifier
					.padding(it)
					.fillMaxSize()
			) {
				items(spotlist.value, key = { spot -> spot.id }) { spot ->

					val dismissState = rememberDismissState(
						confirmStateChange = {
							if(it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
								viewModel.deleteSpot(spot)
							}
							true
						}
					)

					SwipeToDismiss(
						state = dismissState,
						background = {
							val currentFraction = dismissState.progress.fraction
							val targetDirection = dismissState.targetValue == DismissValue.DismissedToStart
							val backgroundColor by animateColorAsState(
								targetValue = if(dismissState.dismissDirection == DismissDirection.EndToStart) {
									MaterialTheme.colorScheme.secondary.copy(
										alpha = currentFraction.coerceIn(
											0f,
											1f
										)
									)
								} else {
									Color.Transparent
								},
								label = "SwipeBackgroundColor"
							)

							if(dismissState.dismissDirection == DismissDirection.EndToStart) {
								Box(
									modifier = Modifier
										.fillMaxSize()
										.background(backgroundColor)
										.padding(horizontal = 20.dp),
									contentAlignment = Alignment.CenterEnd
								) {
									val iconAlpha by animateFloatAsState(
										targetValue = if(currentFraction >= 0.1f) 1f else 0f,
										label = "SwipeIconAlpha"
									)

									if(iconAlpha > 0f) {
										Icon(
											imageVector = Icons.Filled.Delete,
											contentDescription = "delete",
											tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = iconAlpha * 2f)
										)
									}
								}
							}
						},
						directions = setOf(DismissDirection.EndToStart),
						dismissThresholds = { FractionalThreshold(0.70f) },
						dismissContent = {
							SpotCard(spot = spot) {
								val id = spot.id
								navController.navigate(NavScreen.AddScreen.route + "/$id")
							}
						}
					)
				}
			}

		}
	}
}

@Composable
fun SpotCard(
	blurRadius: Dp = 8.dp,
	cornerRadius: Dp = 16.dp,
	spot: SpotData,
	onClick: () -> Unit
) {
	val backgroundColor = if(isSystemInDarkTheme()) {
		Color.White.copy(alpha = 0.15f)
	} else {
		Color.White.copy(alpha = 0.25f)
	}

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(100.dp)
			.clip(RoundedCornerShape(cornerRadius))
			.background(Color.Transparent)
			.padding(8.dp)
			.clickable {
				onClick()
			}
	) {
		Box(
			modifier = Modifier
				.matchParentSize()
				.clip(RoundedCornerShape(cornerRadius))
				.background(backgroundColor)
				.blur(radius = blurRadius)
		)

		Box(
			modifier = Modifier
				.matchParentSize()
		) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					text = spot.name,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.onBackground
				)
				Text(text = spot.description, color = MaterialTheme.colorScheme.onBackground)
			}
		}
	}
}