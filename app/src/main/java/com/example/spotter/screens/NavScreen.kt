package com.example.spotter.screens

import android.net.Uri

sealed class NavScreen(val route: String){
    object HomeScreen: NavScreen("home_screen")
    object AddScreen: NavScreen("add_screen")
    object LocScreen: NavScreen("select-screen")
    object MapScreen: NavScreen("map-screen")
    object PhotoScreen: NavScreen("photo-screen/{imagePath}"){
        fun createRoute(imagePath: String): String{
            val encodedPath = Uri.encode(imagePath)
            return "photo-screen/${encodedPath}"
        }
    }
}