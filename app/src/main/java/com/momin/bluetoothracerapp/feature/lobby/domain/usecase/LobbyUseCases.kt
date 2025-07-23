package com.momin.bluetoothracerapp.feature.lobby.domain.usecase

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.momin.bluetoothracerapp.core.bluetooth.BluetoothController
import com.momin.bluetoothracerapp.feature.lobby.domain.BluetoothDeviceDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LobbyUseCases(private val bluetoothController: BluetoothController) {
    fun startScan() = bluetoothController.startScan()
    fun stopScan() = bluetoothController.stopScan()
    fun connectTo(device: BluetoothDeviceDomain) {
        val platformDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.address)
        bluetoothController.connectToDevice(platformDevice)
    }
    val onConnectionSuccess = bluetoothController.connectionSuccessFlow
    val onDeviceConnectedFlow = bluetoothController.onDeviceConnectedFlow

    fun startServer() = bluetoothController.startServer()
    fun registerBondReceiver() = bluetoothController.registerBondReceiver()
    fun unRegisterBondReceiver() = bluetoothController.unRegisterBondReceiver()
    fun pairDevice(device: BluetoothDevice) = bluetoothController.pairDevice(device)

    @SuppressLint("MissingPermission")
    fun getDiscoveredDevices(): Flow<List<BluetoothDeviceDomain>> =
        bluetoothController.observeConnectedDevices()
            .map { list ->
                list.map {
                    BluetoothDeviceDomain(it.name, it.address, it)
                }
            }
}