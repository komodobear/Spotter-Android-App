package com.example.spotter

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotter.database.Graph
import com.example.spotter.database.SpotData
import com.example.spotter.database.SpotRepository
import com.example.spotter.location.GeocodingResult
import com.example.spotter.location.LocationData
import com.example.spotter.location.LocationRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpotVM(
	private val spotRepository: SpotRepository = Graph.SpotRepository
): ViewModel() {

	lateinit var allSpotsFlow: Flow<List<SpotData>>

	private val _userLocation = MutableStateFlow<LocationData?>(null)
	val userLocation: StateFlow<LocationData?> = _userLocation.asStateFlow()

	private val _address = mutableStateOf(listOf<GeocodingResult>())
	val address: State<List<GeocodingResult>> = _address

	var titleState by mutableStateOf("")
	var descState by mutableStateOf("")
	var spotLocation by mutableStateOf<LocationData?>(null)
	var initialAddress by mutableStateOf("")
	var imagePath by mutableStateOf<String?>(null)

	init {
		viewModelScope.launch {
			try {
				allSpotsFlow = spotRepository.getAllSpots()
			} catch(e: Exception) {
				Log.e("SpotVM", "Error getting all spots", e)
			}
		}
	}

	val apikey = BuildConfig.API_KEY

	fun hasNetworkConnection(context: Context): Boolean {
		val connectivityManager =
			context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		return connectivityManager.activeNetworkInfo?.isConnected ?: false
	}

	fun onTitleChange(newString: String) {
		titleState = newString
	}

	fun onDescChange(newString: String) {
		descState = newString
	}

	fun updateLocation(newLocation: LocationData) {
		_userLocation.value = newLocation
	}

	fun fetchAddress(latlng: String) {
		try {
			viewModelScope.launch {
				val result = LocationRetrofit.createR().getAddressFromCoordinates(
					latlng, apikey
				)
				Log.d("fetchAddress", "Response ${result.results}")
				_address.value = result.results
			}
		} catch(e: Exception) {
			Log.e("fetchAddress", "Error at loading address: ${e.message}", e)
		}
	}

	fun resetAddress() {
		_address.value = emptyList()
	}

	fun initEditData(spot: SpotData) {
		titleState = spot.name
		descState = spot.description
		initialAddress = spot.address ?: ""
		imagePath = spot.imagePath
		spotLocation = if(spot.latitude != null && spot.longitude != null) {
			LocationData(spot.latitude, spot.longitude)
		} else {
			null
		}

		if(spot.latitude != null && spot.longitude != null && address.value.isEmpty()) {
			fetchAddress("${spot.latitude},${spot.longitude}")
		}
	}

	fun getSpotById(id: Long): Flow<SpotData> {
		return spotRepository.getSpotById(id)
	}

	fun addSpot(spot: SpotData) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				spotRepository.addSpot(spot)
			} catch(e: Exception) {
				Log.e("SpotVM", "Error adding spot", e)
			}
		}
	}

	fun updateSpot(spot: SpotData) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				spotRepository.updateSpot(spot)
			} catch(e: Exception) {
				Log.e("SpotVM", "Error updating spot", e)
			}
		}
	}

	fun deleteSpot(spot: SpotData) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				spotRepository.deleteSpot(spot)
			} catch(e: Exception) {
				Log.e("SpotVM", "Error deleting spot", e)
			}
		}
	}

	fun deleteImage() {
		imagePath?.let { path ->
			if(ImageUtils.deleteImageFile(path)) {
				imagePath = null
			}
		}
	}

	fun saveImageFromUri(context: Context, uri: Uri): String? {
		return try {
			val file = ImageUtils.createImageFile(context)
			if(ImageUtils.copyUriToFile(context, uri, file)) {
				file.absolutePath
			} else {
				null
			}
		} catch(e: Exception) {
			Log.e("SpotVM", "Error saving image", e)
			null
		}
	}

	fun resetVM() {
		titleState = ""
		descState = ""
		_address.value = emptyList()
		spotLocation = null
		imagePath = null
	}
}