package com.example.spotter

import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spotter.location.LocationData
import com.example.spotter.location.LocationUtils
import com.example.spotter.screens.AddEditScreen
import com.example.spotter.screens.HomeScreen
import com.example.spotter.screens.LocationSelect
import com.example.spotter.screens.MapScreen
import com.example.spotter.screens.PhotoView

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun navigation(
    viewModel: SpotVM = viewModel(),
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
    locationUtils: LocationUtils = LocationUtils(context)
    ){
    NavHost(
        navController = navController,
        startDestination = NavScreen.HomeScreen.route
    ){
        composable(NavScreen.HomeScreen.route){
            HomeScreen(viewModel,navController)
            viewModel.resetVM()
        }

        composable(
            NavScreen.AddScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            )
        ){
            val id = if(it.arguments != null) it.arguments!!.getLong("id") else 0L
            AddEditScreen(id,viewModel,navController,locationUtils,context)
        }

        dialog(NavScreen.LocScreen.route){
            viewModel.userLocation.value?.let{
                LocationSelect(location = it,
                    viewModel,
                    navController,
                    onLocationSelected = {
                    if(viewModel.hasNetworkConnection(context)) {
                        viewModel.fetchAddress("${it.latitude},${it.longitude}")
                    }else{
                        Toast.makeText(context,"Błąd sieci, nie można zmienić lokalizacji",Toast.LENGTH_SHORT).show()
                    }
                    viewModel.kotLocation = LocationData(it.latitude,it.longitude)
                    navController.popBackStack()
                })
            }
        }

        composable(NavScreen.MapScreen.route){
            MapScreen(locationUtils,viewModel,navController)
        }

        dialog(
            route = NavScreen.PhotoScreen.route,
            arguments = listOf(navArgument("imagePath") {type = NavType.StringType} ),
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ){ navBackStackEntry ->
            val imagePath = navBackStackEntry.arguments?.getString("imagePath")
            val decodedPath = imagePath?.let { Uri.decode(it) }
            PhotoView(navController,decodedPath)
        }
    }
}