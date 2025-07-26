package com.momin.bluetoothracerapp.core.di

import android.content.Context
import com.momin.bluetoothracerapp.core.speechrecognizer.SpeechRecognizerManager
import org.koin.dsl.module

val speechRecognizerModule = module {
    single { (context: Context, onResult: (String) -> Unit) ->
        SpeechRecognizerManager(context, onResult)
    }
}

