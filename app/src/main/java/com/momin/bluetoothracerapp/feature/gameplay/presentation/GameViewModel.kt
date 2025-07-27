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

    private val _isMyTurn = mutableStateOf(false)
    val isMyTurn: State<Boolean> = _isMyTurn

    var firstCarPositionY by mutableIntStateOf(0) // 0.0 to 1.0
    var secondCarPositionY by mutableIntStateOf(0)
    var isGameOver by mutableStateOf(false)
    var gameResult by mutableStateOf<String?>(null)

    init {
        _isMyTurn.value = isHost
        gameUseCase.listenForMessages()
    }

    private var stepSizePx = 0f
    var finishLinePos by mutableStateOf(IntOffset.Zero)

    fun initGame(density: Density) {
        stepSizePx = with(density) { 50.dp.toPx() }

        viewModelScope.launch {
            gameUseCase.onMessageReceived.collect { message ->
                val moveDistance = (message.toInt() * stepSizePx).toInt()
                secondCarPositionY -= moveDistance
                _isMyTurn.value = true

                if (secondCarPositionY <= finishLinePos.y) {
                    isGameOver = true
                    gameResult = "Opponent Win!"
                }
            }
        }
    }


    fun onDiceRolled(result: Int, density: Density) {
        val stepSizeDp = 50.dp
        val stepSizePx = with(density) { stepSizeDp.toPx() }
        val moveDistance = (result * stepSizePx).toInt()
        firstCarPositionY = firstCarPositionY - moveDistance // move up, don't go past finish line

        if (firstCarPositionY <= finishLinePos.y) {
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

    }
}
