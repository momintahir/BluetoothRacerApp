package com.momin.bluetoothracerapp.core.di

import com.momin.bluetoothracerapp.core.bluetooth.BluetoothController
import com.momin.bluetoothracerapp.core.bluetooth.BluetoothControllerImpl
import org.koin.dsl.module

val bluetoothModule = module {
    single<BluetoothController> { BluetoothControllerImpl(get()) }
}