package com.momin.bluetoothracerapp.core.di

import com.momin.bluetoothracerapp.feature.lobby.domain.usecase.LobbyUseCases
import com.momin.bluetoothracerapp.feature.lobby.presentation.LobbyViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val lobbyModule = module {

    // Provide LobbyUseCases with BluetoothController dependency
    factory { LobbyUseCases(bluetoothController = get()) }

    // Provide LobbyViewModel with LobbyUseCases dependency
    viewModel { LobbyViewModel(useCases = get()) }
}