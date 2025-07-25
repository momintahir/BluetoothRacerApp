package com.momin.bluetoothracerapp.feature.gameplay.domain.usecase

import com.momin.bluetoothracerapp.core.bluetooth.BluetoothController
import kotlinx.coroutines.flow.SharedFlow

class GameUseCase(private val bluetoothController: BluetoothController) {

    fun sendMessage(message: Int) = bluetoothController.sendMessage(message)
    fun listenForMessages() = bluetoothController.listenForMessages()
    val onMessageReceived: SharedFlow<String> = bluetoothController.onMessageReceivedFlow
}