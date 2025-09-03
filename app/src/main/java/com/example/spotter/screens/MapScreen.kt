package com.example.spotter.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spotter.SpotVM
import com.example.spotter.location.LocationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
	locationUtils: LocationUtils,
	viewModel: SpotVM,
	navController: NavController,
) {
    var permissionDenied by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hasLocationPermission = locationUtils.hasLocationPermission(context)
    var zoomDone by remember { mutableStateOf(false) }

    val userLocation = viewModel.userLocation.collectAsState()

    val cameraPosition = rememberCameraPositionState()
    val allSpots = viewModel.getAllKotek.collectAsState(initial = emptyList())
    val spotsWithLocation = allSpots.value.filter {
        it.latitude != null && it.longitude != null
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val fineLocationGranted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            permissionDenied = false
            locationUtils.requestLocationUpdates(viewModel)
        } else if (spotsWithLocation.isEmpty()){
            permissionDenied = true
        }
    }

    val darkMapStyle = """
        [
  {
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#242f3e"
      }
    ]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#746855"
      }
    ]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [
      {
        "color": "#242f3e"
      }
    ]
  },
  {
    "featureType": "administrative.locality",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#d59563"
      }
    ]
  },
  {
    "featureType": "poi",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#d59563"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#263c3f"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#6b9a76"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#38414e"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "geometry.stroke",
    "stylers": [
      {
        "color": "#212a37"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#9ca5b3"
      }
    ]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#746855"
      }
    ]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry.stroke",
    "stylers": [
      {
        "color": "#1f2835"
      }
    ]
  },
  {
    "featureType": "road.highway",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#f3d19c"
      }
    ]
  },
  {
    "featureType": "transit",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#2f3948"
      }
    ]
  },
  {
    "featureType": "transit.station",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#d59563"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#17263c"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#515c6d"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.stroke",
    "stylers": [
      {
        "color": "#17263c"
      }
    ]
  }
]
    """.trimIndent()

    val darkTheme = isSystemInDarkTheme()

    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = if(darkTheme) MapStyleOptions(darkMapStyle) else null,
                isMyLocationEnabled = hasLocationPermission
            )
        )
    }

    LaunchedEffect(darkTheme) {
        mapProperties = mapProperties.copy(
            mapStyleOptions = if(darkTheme) MapStyleOptions(darkMapStyle) else null
        )
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            locationUtils.requestLocationUpdates(viewModel)
        }
    }

    LaunchedEffect(userLocation.value, spotsWithLocation, permissionDenied) {
        when {
            userLocation.value != null && !zoomDone -> {
                val location = userLocation.value!!
                cameraPosition.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude),
                        15f
                    ), 1000
                )
                zoomDone = true
            }
            spotsWithLocation.isNotEmpty() && !permissionDenied -> {
                scope.launch {
                    val bounds = LatLngBounds.Builder().apply {
                        spotsWithLocation.forEach {
                            include(LatLng(it.latitude!!, it.longitude!!))
                        }
                    }.build()
                    cameraPosition.animate(
                        CameraUpdateFactory.newLatLngBounds(bounds, 200),
                        1000
                    )
                    Log.d("MapScreen","$bounds")
                }
            }
            permissionDenied -> {
                cameraPosition.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(54.5177128, 18.5319718),
                        18f
                    ), 500
                )
            }
        }
    }

    Scaffold(
        topBar = { TopBar(title = "Spot Map") { navController.navigateUp() } },
        bottomBar = { BottomBar(navController) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPosition,
                properties = mapProperties
            ) {
                spotsWithLocation.forEach { spot ->
                    val markerState = rememberMarkerState(
                        position = LatLng(spot.latitude!!, spot.longitude!!)
                    )
                    Marker(
                        state = markerState,
                        title = spot.name,
                        snippet = spot.address,
                        onClick = {
                            markerState.showInfoWindow()
                            true
                        },
                        onInfoWindowClick = { markerState.hideInfoWindow() }
                    )
                }
            }

            if (spotsWithLocation.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val bounds = LatLngBounds.Builder().apply {
                                spotsWithLocation.forEach {
                                    include(LatLng(it.latitude!!, it.longitude!!))
                                }
                            }.build()
                            cameraPosition.animate(
                                CameraUpdateFactory.newLatLngBounds(bounds, 200),
                                1000
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.BottomStart),
                    contentColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Adjust, contentDescription = "Zoom to all spots")
                }
            }

            if (hasLocationPermission && userLocation.value == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}