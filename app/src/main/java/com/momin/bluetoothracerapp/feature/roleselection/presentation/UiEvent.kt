package com.momin.bluetoothracerapp.feature.roleselection.presentation

sealed class UiEvent {
    object NavigateToGame : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
    // Add more if needed
}