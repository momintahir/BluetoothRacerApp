package com.momin.bluetoothracerapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.momin.bluetoothracerapp.feature.lobby.presentation.MainScreen

sealed class Screen(val route: String) {
    object Lobby : Screen("lobby")
    object Game : Screen("game") // You'll add this later
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Lobby.route) {
        composable(Screen.Lobby.route) {
            MainScreen()
        }

        // Later
        // composable(Screen.Game.route) {
        //     GameScreen()
        // }
    }
}
