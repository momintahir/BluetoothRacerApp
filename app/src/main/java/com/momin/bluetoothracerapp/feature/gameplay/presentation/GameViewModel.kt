package com.momin.bluetoothracerapp.feature.gameplay.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momin.bluetoothracerapp.feature.gameplay.domain.usecase.GameUseCase
import kotlinx.coroutines.launch

class GameViewModel(private val isHost: Boolean, private val gameUseCase: GameUseCase) : ViewModel() {

    private val _playerName = mutableStateOf("")
    val playerName: State<String> = _playerName

    private val _opponentName = mutableStateOf("")
    val opponentName: State<String> = _opponentName

    private val _isMyTurn = mutableStateOf(false)
    val isMyTurn: State<Boolean> = _isMyTurn

    var myCarPositionY by mutableIntStateOf(0) // 0.0 to 1.0
    var opponentCarPositionY by mutableIntStateOf(0)
    var isGameOver by mutableStateOf(false)
    var gameResult by mutableStateOf<String?>(null)

    init {
        setupPlayerState()
        gameUseCase.listenForMessages()
    }

    private var stepSizePx = 0f
    var finishLinePos by mutableStateOf(IntOffset.Zero)

    fun initGame(density: Density) {
        stepSizePx = with(density) { 20.dp.toPx() }
        val finishLine = with(density) { finishLinePos.y.dp.toPx() }

        viewModelScope.launch {
            gameUseCase.onMessageReceived.collect { message ->
                val moveDistance = (message.toInt() * stepSizePx).toInt()
                opponentCarPositionY -= moveDistance
                _isMyTurn.value = true

                println("myTag: opponentCarPositionY: $opponentCarPositionY, finishLinePos.y: ${finishLinePos.y}")
                if (opponentCarPositionY <= finishLinePos.y) {
                    isGameOver = true
                    gameResult = "Opponent Win!"
                }
            }
        }
    }


    fun onDiceRolled(result: Int, density: Density) {
        val stepSizeDp = 20.dp
        val stepSizePx = with(density) { stepSizeDp.toPx() }
        val moveDistance = (result * stepSizePx).toInt()
        myCarPositionY = myCarPositionY - moveDistance // move up, don't go past finish line

        if (myCarPositionY <= finishLinePos.y) {
            isGameOver = true
            gameResult = "You Win!"
        }
        sendDiceRollToOpponent(result)
        _isMyTurn.value = false
    }

    fun sendDiceRollToOpponent(result: Int) {
        val message = result
        gameUseCase.sendMessage(message)
    }

    private fun setupPlayerState() {
        if (isHost) {
            _playerName.value = "Host"
            _opponentName.value = "Guest"
            _isMyTurn.value = true // Host always starts
        } else {
            _playerName.value = "Guest"
            _opponentName.value = "Host"
            _isMyTurn.value = false
        }
    }

    fun toggleTurn() {
        _isMyTurn.value = !_isMyTurn.value
    }
}
