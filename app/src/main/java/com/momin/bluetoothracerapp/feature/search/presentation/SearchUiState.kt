package com.momin.bluetoothracerapp.feature.search.presentation

import com.momin.bluetoothracerapp.feature.search.domain.BluetoothDeviceDomain

data class SearchUiState(
    val devices: List<BluetoothDeviceDomain> = emptyList()
)