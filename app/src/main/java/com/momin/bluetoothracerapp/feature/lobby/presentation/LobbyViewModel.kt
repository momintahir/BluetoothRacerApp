package com.momin.bluetoothracerapp.feature.lobby.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momin.bluetoothracerapp.feature.lobby.domain.BluetoothDeviceDomain
import com.momin.bluetoothracerapp.feature.lobby.domain.usecase.LobbyUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LobbyViewModel(private val useCases: LobbyUseCases) : ViewModel() {
    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState = _uiState.asStateFlow()


    init {
        observeDevices()
    }

    private fun observeDevices() {
        viewModelScope.launch {
            useCases.getDiscoveredDevices().collect { devices ->
                _uiState.update { it.copy(devices = devices) }
            }
        }
    }

    fun startScan() = useCases.startScan()
    fun stopScan() = useCases.stopScan()
    fun connect(device: BluetoothDeviceDomain) = useCases.connectTo(device)

}