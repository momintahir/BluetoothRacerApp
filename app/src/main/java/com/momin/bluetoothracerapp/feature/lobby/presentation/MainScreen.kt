package com.momin.bluetoothracerapp.feature.lobby.presentation

import GameMainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.momin.bluetoothracerapp.core.navigation.Screen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Lobby.route) {
        composable(Screen.Lobby.route) {
            RequestBluetoothPermissions {
                LobbyScreen(navController = navController)
            }
        }
        composable(Screen.Game.route) {
            GameMainScreen()
        }
    }
}