package com.momin.bluetoothracerapp.feature.search.presentation

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momin.bluetoothracerapp.feature.search.domain.usecase.SearchUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchScreenViewModel(private val useCases: SearchUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    init {
        useCases.listenConnections()
        //        listening to incoming connections
        viewModelScope.launch {
            useCases.onDeviceConnectedFlow.collect {
                _eventFlow.emit(UiEvent.NavigateToRoleSelection)
            }
        }


        getBluetoothDevices()
        viewModelScope.launch {
            useCases.onConnectionSuccess.collect {
                _eventFlow.emit(UiEvent.NavigateToRoleSelection)
            }
        }
    }

    private fun getBluetoothDevices() {
        viewModelScope.launch {
            useCases.getBluetoothDevices().collect { devices ->
                _uiState.update { it.copy(devices = devices) }
            }
        }
    }

    fun startScan() = useCases.startScan()
    fun registerBondReceiver() = useCases.registerBondReceiver()
    fun unRegisterBondReceiver() = useCases.unRegisterBondReceiver()
    fun stopScan() = useCases.stopScan()
    fun pairAndRegisterDevice(device: BluetoothDevice) = useCases.pairAndRegisterDevice(device)
}