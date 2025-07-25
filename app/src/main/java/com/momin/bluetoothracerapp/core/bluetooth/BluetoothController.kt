package com.momin.bluetoothracerapp.core.bluetooth

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface BluetoothController {
    fun startScan()
    fun stopScan()
    fun connectToDevice(device: BluetoothDevice)
    fun pairDevice(device: BluetoothDevice)
    fun registerBondReceiver()
    fun startServer()
    fun unRegisterBondReceiver()
    val connectionSuccessFlow: SharedFlow<Boolean>
    val onDeviceConnectedFlow: SharedFlow<Boolean>
    val onMessageReceivedFlow: SharedFlow<String>
    fun disconnect()
    fun sendMessage(message: Int)
    fun listenForMessages()
    fun observeMessages(): Flow<String>
    fun observeConnectedDevices(): Flow<List<BluetoothDevice>>
}