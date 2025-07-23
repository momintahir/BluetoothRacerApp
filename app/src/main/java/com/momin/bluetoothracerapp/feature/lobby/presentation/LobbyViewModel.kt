package com.momin.bluetoothracerapp.feature.lobby.presentation

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momin.bluetoothracerapp.feature.lobby.domain.BluetoothDeviceDomain
import com.momin.bluetoothracerapp.feature.lobby.domain.usecase.LobbyUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LobbyViewModel(private val useCases: LobbyUseCases) : ViewModel() {
    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    init {
        useCases.startServer()
        observeDevices()
        viewModelScope.launch {
            useCases.onConnectionSuccess.collect {
                _eventFlow.emit(UiEvent.NavigateToRoleSelection)
            }
        }

        viewModelScope.launch {
            useCases.onDeviceConnectedFlow.collect {
                _eventFlow.emit(UiEvent.NavigateToRoleSelection)
            }
        }
    }

    private fun observeDevices() {
        viewModelScope.launch {
            useCases.getDiscoveredDevices().collect { devices ->
                _uiState.update { it.copy(devices = devices) }
            }
        }
    }

    fun startScan() = useCases.startScan()
    fun registerBondReceiver() = useCases.registerBondReceiver()
    fun unRegisterBondReceiver() = useCases.unRegisterBondReceiver()
    fun stopScan() = useCases.stopScan()
    fun connect(device: BluetoothDeviceDomain) = useCases.connectTo(device)
    fun pairDevice(device: BluetoothDevice) = useCases.pairDevice(device)

}