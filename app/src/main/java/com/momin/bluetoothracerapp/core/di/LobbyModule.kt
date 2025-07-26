package com.momin.bluetoothracerapp.core.di

import com.momin.bluetoothracerapp.feature.search.domain.usecase.SearchUseCase
import com.momin.bluetoothracerapp.feature.search.presentation.SearchScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val lobbyModule = module {

    // Provide LobbyUseCases with BluetoothController dependency
    factory { SearchUseCase(bluetoothController = get()) }

    // Provide LobbyViewModel with LobbyUseCases dependency
    viewModel { SearchScreenViewModel(useCases = get()) }
}