package com.momin.bluetoothracerapp.feature.search.domain

import android.bluetooth.BluetoothDevice

data class BluetoothDeviceDomain(
    val name: String?,
    val address: String,
    val device: BluetoothDevice // platform type
)