package com.momin.bluetoothracerapp.core.navigation

import GameMainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Lobby : Screen("lobby_screen")
    object Game : Screen("game_screen")
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Lobby.route) {
        composable(Screen.Lobby.route) {
            GameMainScreen()
        }

        // Later
        // composable(Screen.Game.route) {
        //     GameScreen()
        // }
    }
}
