package com.example.spotter.screens

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.spotter.screens.NavScreen

@Composable
fun BottomBar(
    navController: NavController
){
    val items = listOf(
        BottomNavItem("home",Icons.Default.Home, NavScreen.HomeScreen.route),
        BottomNavItem("map", Icons.Default.Map, NavScreen.MapScreen.route),
    )
    
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primary
    ) {
        val currentRoute = navController.currentDestination?.route
        items.forEach{item->
            BottomNavigationItem(
                label = { Text(item.name) },
                icon = { Icon(item.icon, contentDescription = item.name) },
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                selectedContentColor = Color.White
            )
        }
    }
}

data class BottomNavItem(
    val name: String,
    val icon: ImageVector,
    val route: String
)