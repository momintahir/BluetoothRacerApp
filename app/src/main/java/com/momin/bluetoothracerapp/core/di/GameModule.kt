package com.momin.bluetoothracerapp.core.di

import com.momin.bluetoothracerapp.feature.gameplay.domain.usecase.GameUseCase
import com.momin.bluetoothracerapp.feature.gameplay.presentation.GameViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gameModule = module {

    // Provide LobbyUseCases with BluetoothController dependency
    factory { GameUseCase(bluetoothController = get()) }

    // Provide LobbyViewModel with LobbyUseCases dependency
    viewModel { GameViewModel(get()) }
}