package com.momin.bluetoothracerapp.feature.lobby.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

data class BluetoothDeviceDomain(
    val name: String?,
    val address: String,
    val device: BluetoothDevice // platform type
)

@SuppressLint("MissingPermission")
fun BluetoothDevice.toDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = this.name,
        address = this.address,
        device = this
    )
}