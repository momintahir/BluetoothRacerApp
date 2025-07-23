package com.momin.bluetoothracerapp.feature.lobby.presentation

import GameMainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.momin.bluetoothracerapp.core.navigation.Screen

@Composable
fun MainScreen(navController: NavController) {
            RequestBluetoothPermissions {
                LobbyScreen(navController = navController)
            }
        }
