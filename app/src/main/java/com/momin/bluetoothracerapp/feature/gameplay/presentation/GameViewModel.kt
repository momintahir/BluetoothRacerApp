package com.momin.bluetoothracerapp.feature.gameplay.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momin.bluetoothracerapp.feature.gameplay.domain.usecase.GameUseCase
import kotlinx.coroutines.launch

class GameViewModel(private val gameUseCase: GameUseCase) : ViewModel() {

    private val _playerName = mutableStateOf("")
    val playerName: State<String> = _playerName

    private val _opponentName = mutableStateOf("")
    val opponentName: State<String> = _opponentName

    private val _isMyTurn = mutableStateOf(false)
    val isMyTurn: State<Boolean> = _isMyTurn

    private val _myCarPosition = mutableIntStateOf(0)
    val myCarPosition: State<Int> = _myCarPosition

    private val _opponentCarPosition = mutableIntStateOf(0)
    val opponentCarPosition: State<Int> = _opponentCarPosition

    init {
        assignRandomNamesAndTurn()
        gameUseCase.listenForMessages()

        viewModelScope.launch {
            gameUseCase.onMessageReceived.collect { message ->
                if (message.startsWith("DICE_ROLL:")) {
                    val diceResult = message.substringAfter(":").toInt()
                    _opponentCarPosition.intValue += diceResult
                    _isMyTurn.value = true
                }
            }
        }
    }

    fun onDiceRolled(result: Int) {
        _myCarPosition.intValue += result

        // Send result to opponent via Bluetooth
        sendDiceRollToOpponent(result)

        // Switch turn
        _isMyTurn.value = false
    }

    fun sendDiceRollToOpponent(result: Int) {
        val message = "DICE_ROLL:$result"
        gameUseCase.sendMessage(message)
    }

    private fun assignRandomNamesAndTurn() {
        val names = listOf("Player1", "Player2")
        _playerName.value = names[0]
        _opponentName.value = names[1]
        _isMyTurn.value = listOf(true, false).random()
    }

    fun toggleTurn() {
        _isMyTurn.value = !_isMyTurn.value
    }
}
