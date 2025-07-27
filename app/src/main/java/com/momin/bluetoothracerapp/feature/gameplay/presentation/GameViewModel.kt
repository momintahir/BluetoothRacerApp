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

    private val _isHostTurn = mutableStateOf(false)
    val isHostTurn: State<Boolean> = _isHostTurn

    var firstCarPositionY by mutableIntStateOf(0)
    var secondCarPositionY by mutableIntStateOf(0)
    var isGameOver by mutableStateOf(false)
    var gameResult by mutableStateOf<String?>(null)

    init {
        _isHostTurn.value = isHost
        gameUseCase.listenForMessages()
    }

    private var stepSizePx = 0f
    var finishLinePos by mutableStateOf(IntOffset.Zero)

    fun initGame(density: Density) {
        stepSizePx = with(density) { 50.dp.toPx() }

        viewModelScope.launch {
            gameUseCase.onMessageReceived.collect { message ->
                val result = message.toIntOrNull() ?: return@collect
                handleOpponentMove(result)
            }
        }
    }

    fun handleOpponentMove(result: Int){
        val moveDistance = (result * stepSizePx).toInt()
        if (isHost) {
            secondCarPositionY -= moveDistance
            checkIfOpponentWon(secondCarPositionY)
        } else {
            firstCarPositionY -= moveDistance
            checkIfOpponentWon(firstCarPositionY)
        }
        _isHostTurn.value = true
    }
    private fun checkIfOpponentWon(positionY: Int) {
        if (positionY <= finishLinePos.y) {
            isGameOver = true
            gameResult = "Opponent Win!"
        }
    }

    private fun checkIfYouWon(positionY: Int) {
        if (positionY <= finishLinePos.y) {
            isGameOver = true
            gameResult = "You Win!"
        }
    }


    fun onDiceRolled(result: Int, density: Density) {
        val stepSizeDp = 50.dp
        val stepSizePx = with(density) { stepSizeDp.toPx() }
        val moveDistance = (result * stepSizePx).toInt()
        if (isHost) {
            firstCarPositionY -= moveDistance
            checkIfYouWon(firstCarPositionY)
        } else {
            secondCarPositionY -= moveDistance
            checkIfYouWon(secondCarPositionY)
        }
        sendDiceRollToOpponent(result)
        _isHostTurn.value = false
    }

    fun sendDiceRollToOpponent(result: Int) {
        val message = result
        gameUseCase.sendMessage(message)
    }
}
