package com.momin.bluetoothracerapp.feature.search.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momin.bluetoothracerapp.R
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
    val animatedDevices = remember(uiState.devices) { uiState.devices }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            if (event == UiEvent.NavigateToRoleSelection) {
                navController.navigate(Screen.RoleSelection.route)
            }
        }
    }

    DisposableEffect(Unit) {
        viewModel.registerBondReceiver()
        onDispose { viewModel.unRegisterBondReceiver() }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Discovered Devices",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Image(painter = painterResource(R.drawable.ic_mobile), contentDescription = "", modifier = Modifier.size(30.dp))
            }

            Spacer(Modifier.height(50.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                itemsIndexed(animatedDevices) { index, device ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = index * 100)) +
                                slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(500, delayMillis = index * 100))
                    ) {
                        DeviceItem(device = device) {
                            viewModel.pairAndRegisterDevice(device.device)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(text = "Start Scan", onClick = { viewModel.startScan() })
                Button(text = "Stop Scan", onClick = { viewModel.stopScan() })
            }
        }
    }
}




