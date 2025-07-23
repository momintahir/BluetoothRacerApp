package com.momin.bluetoothracerapp.feature.gameplay.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {

    private val _isMyTurn = MutableStateFlow(true)
    val isMyTurn: StateFlow<Boolean> = _isMyTurn

    private val _myCarY = MutableStateFlow(0f)
    val myCarY: StateFlow<Float> = _myCarY

    private val _opponentCarY = MutableStateFlow(0f)
    val opponentCarY: StateFlow<Float> = _opponentCarY

    private val _diceValue = MutableStateFlow(1)
    val diceValue: StateFlow<Int> = _diceValue

    fun onRollDiceClicked() {
        if (!_isMyTurn.value) return

        val rolled = (1..6).random()
        _diceValue.value = rolled

        // Move car
        val newY = (_myCarY.value - rolled * 50f).coerceAtLeast(0f)
        _myCarY.value = newY

        // TODO: Send rolled + newY via Bluetooth to opponent

        _isMyTurn.value = false
    }

    fun onOpponentDataReceived(rolled: Int, newY: Float) {
        _diceValue.value = rolled
        _opponentCarY.value = newY
        _isMyTurn.value = true
    }
}
