package com.momin.bluetoothracerapp.feature.search.domain.usecase

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.momin.bluetoothracerapp.core.bluetooth.BluetoothController
import com.momin.bluetoothracerapp.feature.search.domain.BluetoothDeviceDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchUseCase(private val bluetoothController: BluetoothController) {
    fun startScan() = bluetoothController.startScan()
    fun stopScan() = bluetoothController.stopScan()

    val onConnectionSuccess = bluetoothController.connectionSuccessFlow
    val onDeviceConnectedFlow = bluetoothController.onDeviceConnectedFlow

    fun listenConnections() = bluetoothController.listenConnections()
    fun registerBondReceiver() = bluetoothController.registerBondReceiver()
    fun unRegisterBondReceiver() = bluetoothController.unRegisterBondReceiver()
    fun pairAndRegisterDevice(device: BluetoothDevice) = bluetoothController.pairAndRegisterDevice(device)

    @SuppressLint("MissingPermission")
    fun getBluetoothDevices(): Flow<List<BluetoothDeviceDomain>> =
        bluetoothController.getBluetoothDevices()
            .map { list ->
                list.map {
                    BluetoothDeviceDomain(it.name, it.address, it)
                }
            }
}