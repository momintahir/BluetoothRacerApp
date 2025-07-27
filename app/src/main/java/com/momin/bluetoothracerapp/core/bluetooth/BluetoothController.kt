package com.momin.bluetoothracerapp.core.bluetooth

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface BluetoothController {
    fun startScan()
    fun stopScan()
    fun connectToDevice(device: BluetoothDevice)
    fun pairAndRegisterDevice(device: BluetoothDevice)
    fun registerBondReceiver()
    fun listenConnections()
    fun unRegisterBondReceiver()
    val connectionSuccessFlow: SharedFlow<Boolean>
    val onDeviceConnectedFlow: SharedFlow<Boolean>
    val onMessageReceivedFlow: SharedFlow<String>
    fun disconnect()
    fun sendMessage(message: Int)
    fun listenForMessages()
    fun getBluetoothDevices(): Flow<List<BluetoothDevice>>
}