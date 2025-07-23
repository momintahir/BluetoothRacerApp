package com.momin.bluetoothracerapp.core.di

import com.momin.bluetoothracerapp.feature.lobby.domain.usecase.LobbyUseCases
import com.momin.bluetoothracerapp.feature.roleselection.presentation.RoleSelectionViewModel
import com.momin.bluetoothracerapp.feature.roleselection.usecase.RoleSelectionUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val roleSelectionModule = module {

    // Provide LobbyUseCases with BluetoothController dependency
    factory { RoleSelectionUseCase(bluetoothController = get()) }

    // Provide LobbyViewModel with LobbyUseCases dependency
    viewModel { RoleSelectionViewModel(useCase = get()) }
}