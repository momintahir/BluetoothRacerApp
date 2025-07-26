package com.momin.bluetoothracerapp.feature.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momin.bluetoothracerapp.core.navigation.Screen
import com.momin.bluetoothracerapp.core.permissions.RequestPermissions
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchMainScreen(navController: NavController) {
    RequestPermissions {
        SearchScreen(navController = navController)
    }
}


@Composable
fun SearchScreen(navController: NavController, viewModel: SearchScreenViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                UiEvent.NavigateToRoleSelection -> {
                    navController.navigate(Screen.RoleSelection.route)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        viewModel.registerBondReceiver()
        onDispose {
            viewModel.unRegisterBondReceiver()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Discovered Devices",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.devices) { device ->
                DeviceItem(device) {
                    viewModel.pairDevice(device.device)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.startScan() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Scan")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { viewModel.stopScan() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Stop Scan")
            }
        }
    }
}



