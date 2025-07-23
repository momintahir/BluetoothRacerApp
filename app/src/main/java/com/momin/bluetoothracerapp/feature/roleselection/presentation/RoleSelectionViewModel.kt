package com.momin.bluetoothracerapp.feature.roleselection.presentation

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momin.bluetoothracerapp.feature.lobby.domain.BluetoothDeviceDomain
import com.momin.bluetoothracerapp.feature.lobby.domain.usecase.LobbyUseCases
import com.momin.bluetoothracerapp.feature.lobby.presentation.LobbyUiState
import com.momin.bluetoothracerapp.feature.lobby.presentation.UiEvent
import com.momin.bluetoothracerapp.feature.roleselection.usecase.RoleSelectionUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoleSelectionViewModel(private val useCase: RoleSelectionUseCase) : ViewModel() {


}