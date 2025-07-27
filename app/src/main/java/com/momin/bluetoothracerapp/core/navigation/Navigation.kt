package com.momin.bluetoothracerapp.core.navigation

import GameMainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.momin.bluetoothracerapp.feature.roleselection.presentation.RoleSelectionScreen
import com.momin.bluetoothracerapp.feature.search.presentation.SearchMainScreen

sealed class Screen(val route: String) {
    object Search : Screen("search_screen")
    object RoleSelection : Screen("role_selection_screen")
    object Game : Screen("game_screen/{isFirstPlayer}") {
        fun createRoute(isFirstPlayer: Boolean) = "game_screen/$isFirstPlayer"
    }

}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            SearchMainScreen(navController)
        }
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(navController)
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("isFirstPlayer") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isFirstPlayer = backStackEntry.arguments?.getBoolean("isFirstPlayer") ?: false
            GameMainScreen(isFirstPlayer = isFirstPlayer)
        }
    }
}
