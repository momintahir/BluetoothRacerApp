package com.momin.bluetoothracerapp

import android.app.Application
import com.momin.bluetoothracerapp.core.di.bluetoothModule
import com.momin.bluetoothracerapp.core.di.gameModule
import com.momin.bluetoothracerapp.core.di.lobbyModule
import com.momin.bluetoothracerapp.core.di.speechRecognizerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class RaceGameApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RaceGameApp)
            modules(listOf(bluetoothModule, lobbyModule, gameModule, speechRecognizerModule))
        }
    }
}