package com.momin.bluetoothracerapp.feature.lobby.presentation

import com.momin.bluetoothracerapp.feature.lobby.domain.BluetoothDeviceDomain

data class LobbyUiState(
    val devices: List<BluetoothDeviceDomain> = emptyList()
)