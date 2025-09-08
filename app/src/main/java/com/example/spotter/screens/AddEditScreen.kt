package com.example.spotter.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.spotter.ImageUtils
import com.example.spotter.MainActivity
import com.example.spotter.screens.NavScreen
import com.example.spotter.SpotVM
import com.example.spotter.database.SpotData
import com.example.spotter.location.LocationUtils
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AddEditScreen(
    id: Long,
    viewModel: SpotVM,
    navController: NavController,
    locationUtils: LocationUtils,
    context: Context,
) {
    val snackMessage = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions->
            if(
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                locationUtils.requestLocationUpdates(viewModel)
            }else{
                val rationaleRequired =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                context as MainActivity,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                if (rationaleRequired){
                    Toast.makeText(context,"Location permission needed",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,"Location permission needed, check settings",Toast.LENGTH_LONG).show()
                }
            }
        }
    )

    var showImageDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.saveImageFromUri(context, uri)?.let {
                viewModel.imagePath = it
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ){ success ->
        if(success){
            tempPhotoUri?.let {path->
                viewModel.saveImageFromUri(context,path)?.let { path->
                    viewModel.imagePath = path
                }
            }
        }
        showImageDialog = false
    }

    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val file = ImageUtils.createImageFile(context)
                tempPhotoUri = ImageUtils.getImageUri(context, file)
                cameraLauncher.launch(tempPhotoUri!!)
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.CAMERA
                )
                if (rationaleRequired) {
                    Toast.makeText(context, "Camera permission needed", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Camera permission needed, check settings", Toast.LENGTH_LONG).show()
                }
            }
        }
    )

    val requestGalleryPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
                if (rationaleRequired) {
                    Toast.makeText(context, "Gallery permission needed", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Gallery permission needed, check settings", Toast.LENGTH_LONG).show()
                }
            }
        }
    )

    if(showImageDialog){
        AlertDialog(
            onDismissRequest = {showImageDialog=false},
            title = { Text(
                text = "Choose an option",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.secondary
            )},
            backgroundColor = MaterialTheme.colorScheme.background
            ,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Button(onClick = {
                        if(ImageUtils.hasCameraPermission(context)){
                            val file = ImageUtils.createImageFile(context)
                            tempPhotoUri = ImageUtils.getImageUri(context, file)
                            cameraLauncher.launch(tempPhotoUri!!)
                        }else{
                            requestCameraPermission.launch(Manifest.permission.CAMERA)
                        }
                    },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Text("Make photo", color = MaterialTheme.colorScheme.onSecondary)
                    }
                    Button(onClick = {
                        if(ImageUtils.hasPhotoPermission(context)){
                            showImageDialog = false
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts
                                .PickVisualMedia.ImageOnly))
                        }else{
                            requestGalleryPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Text(text = "Choose from gallery", color = MaterialTheme.colorScheme.onSecondary)
                    }

                    if(viewModel.imagePath != null){
                        Button(
                            onClick = { viewModel.deleteImage()
                                showImageDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 2.dp,
                                disabledElevation = 0.dp
                            )
                        ) {
                            Text("Delete photo", color = MaterialTheme.colorScheme.onSecondary)
                        }
                    }

                }
            },
            confirmButton = {
                Button(
                    onClick = { showImageDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp,
                        disabledElevation = 0.dp)
                ) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
        )
    }
    
    if (id != 0L) {
        val spot = viewModel.getSpotById(id).collectAsState(initial = SpotData(0L,"",""))
        LaunchedEffect(spot.value) {
            if (spot.value.id != 0L) {
                viewModel.initEditData(spot.value)
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar( title =
        if(id!=0L)
            "Spot details"
        else
            "Add Spot"
        ){ navController.navigateUp() } },

        bottomBar = { BottomBar(navController) },
        backgroundColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            SpotTextField(
                label = "Name",
                value = viewModel.titleState,
                onValueChanged = {viewModel.onTitleChange(it)}
            )

            Spacer(modifier = Modifier.height(10.dp))

            SpotTextField(
                label = "Description",
                value = viewModel.descState,
                onValueChanged = {viewModel.onDescChange(it)}
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (locationUtils.hasLocationPermission(context)) {
                            locationUtils.requestLocationUpdates(viewModel)
                            navController.navigate(NavScreen.LocScreen.route) {
                                this.launchSingleTop
                            }
                        } else {
                            requestPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp,
                        disabledElevation = 0.dp
                    )
                ){
                    Text(text = "Map", color = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        showImageDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp,
                        disabledElevation = 0.dp
                    ),
                ) {
                    Text(text = "Photo", color = Color.White)
                }
            }

            Button(onClick = {

                if(viewModel.titleState.isNotEmpty()){

                    val finalAddress = when{
                        id != 0L && viewModel.address.value.isEmpty() -> viewModel.initialAddress
                        else -> viewModel.address.value.firstOrNull()?.formatted_address ?: ""
                    }

                    if(id!=0L){
                        viewModel.updateSpot(
                            SpotData(
                                id = id,
                                name = viewModel.titleState.trim(),
                                description = viewModel.descState.trim(),
                                address = finalAddress,
                                latitude = viewModel.spotLocation?.latitude,
                                longitude = viewModel.spotLocation?.longitude,
                                imagePath = viewModel.imagePath ?: ""
                            )
                        )
                        snackMessage.value = "Spot updated!"
                    }else{
                        viewModel.addSpot(
                            SpotData(
                                name = viewModel.titleState.trim(),
                                description = viewModel.descState.trim(),
                                address = finalAddress,
                                latitude = viewModel.spotLocation?.latitude,
                                longitude = viewModel.spotLocation?.longitude,
                                imagePath = viewModel.imagePath ?: ""
                            )
                        )
                        snackMessage.value = "Spot added!"
                    }
                }else{
                    snackMessage.value = "Name cannot be empty"
                }

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(snackMessage.value)
                    navController.navigateUp()
                }

            },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (id!=0L) "Update spot" else "Add spot",
                    style = TextStyle(fontSize = 18.sp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if(id!=0L){
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = viewModel.address.value.firstOrNull()?.formatted_address ?: "Address empty",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        
                    }else{
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Selected address: ${viewModel.address.value.firstOrNull()?.formatted_address ?: "None"}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            viewModel.imagePath?.let { path ->
                if (ImageUtils.isImageFileExists(path)) {
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(File(path)),
                            contentDescription = "Photo preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                                .pointerInput(Unit){
                                    detectTapGestures(
                                        onLongPress = {
                                            if(viewModel.imagePath != null){
                                                navController.navigate(NavScreen.PhotoScreen.createRoute(viewModel.imagePath!!))
                                            }
                                        }
                                    )
                                }
                        )
                    }
                } else {
                    viewModel.imagePath = null
                }
            }
        }
    }
}

@Composable
fun SpotTextField(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit
){
    OutlinedTextField(
        label = { Text(text = label, color = MaterialTheme.colorScheme.onBackground)},
        value = value,
        onValueChange = onValueChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
    )
}

