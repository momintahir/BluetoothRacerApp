package com.momin.bluetoothracerapp.feature.lobby.presentation

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.momin.bluetoothracerapp.feature.lobby.domain.BluetoothDeviceDomain
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: LobbyViewModel = koinViewModel()
) {
    RequestBluetoothPermissions {
        LobbyScreen(viewModel)
    }
}

@Composable
fun LobbyScreen(viewModel: LobbyViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Discovered Devices", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(uiState.devices) { device ->
                DeviceItem(device) {
                    viewModel.connect(device)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { viewModel.startScan() }) {
                Text("Start Scan")
            }
            Button(onClick = { viewModel.stopScan() }) {
                Text("Stop Scan")
            }
        }
    }
}

@Composable
fun DeviceItem(device: BluetoothDeviceDomain, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(device.name ?: "Unknown Device", style = MaterialTheme.typography.bodyLarge)
            Text(device.address, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun RequestBluetoothPermissions(
    onPermissionsGranted: @Composable () -> Unit
) {
    val context = LocalContext.current
    val permissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            listOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    var allGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        allGranted = result.all { it.value }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissions.toTypedArray())
    }

    if (allGranted) {
        onPermissionsGranted()
    } else {
        Text("Requesting permissions...")
    }
}



