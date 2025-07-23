package com.momin.bluetoothracerapp.feature.lobby.presentation

sealed class UiEvent {
    object NavigateToGame : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
    // Add more if needed
}