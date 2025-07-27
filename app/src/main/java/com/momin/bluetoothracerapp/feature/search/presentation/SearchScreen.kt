package com.momin.bluetoothracerapp.feature.search.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momin.bluetoothracerapp.R
import com.momin.bluetoothracerapp.core.navigation.Screen
import com.momin.bluetoothracerapp.core.permissions.RequestPermissions
import com.momin.bluetoothracerapp.feature.search.domain.BluetoothDeviceDomain
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
                        Item(device = device) {
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

@Composable
fun Item(device: BluetoothDeviceDomain, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .height(70.dp)
                .clickable { onClick() }
                .fillMaxWidth()
                .border(1.dp, Color.DarkGray)
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(R.drawable.ic_mobile), contentDescription = "", modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = device.name?.uppercase() ?: "Unnamed Device",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = device.address,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                )
            }
        }
}

@Composable
fun Button(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
        modifier = Modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}



