package com.momin.bluetoothracerapp.core.bluetooth

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface BluetoothController {
    fun startScan()
    fun stopScan()
    fun connectToDevice(device: BluetoothDevice)
    fun disconnect()
    fun sendMessage(message: String)
    fun observeMessages(): Flow<String>
    fun observeConnectedDevices(): Flow<List<BluetoothDevice>>
}