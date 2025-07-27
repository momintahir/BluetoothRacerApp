import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.momin.bluetoothracerapp.R
import com.momin.bluetoothracerapp.feature.gameplay.presentation.BottomScreenPositionReference
import com.momin.bluetoothracerapp.feature.gameplay.presentation.Car
import com.momin.bluetoothracerapp.feature.gameplay.presentation.DiceRollDialog
import com.momin.bluetoothracerapp.feature.gameplay.presentation.FinishLine
import com.momin.bluetoothracerapp.feature.gameplay.presentation.GameViewModel
import com.momin.bluetoothracerapp.feature.gameplay.presentation.RedAndWhiteLines
import com.momin.bluetoothracerapp.feature.gameplay.presentation.RoadAndLanes
import com.momin.bluetoothracerapp.feature.gameplay.presentation.SideGrass
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

@Composable
fun GameMainScreen(isHost: Boolean) {
    val viewModel: GameViewModel = koinViewModel { parametersOf(isHost) }
    val isHostTurn by viewModel.isHostTurn
    var showDiceDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isHostTurn) {
        if (isHostTurn && !viewModel.isGameOver) {
            delay(3000)
            showDiceDialog = true
        }
    }

    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        viewModel.initGame(density)
    }

    val gameResult = viewModel.gameResult
    if (gameResult != null) {
        AlertDialog(
            onDismissRequest = { /* Optional: Restart or exit */ },
            title = { Text("Game Over") },
            text = { Text(gameResult) },
            confirmButton = {
                TextButton(onClick = { /* Restart logic here */ }) {
                    Text("OK")
                }
            }
        )
    }

    if (showDiceDialog) {
        DiceRollDialog(
            onDiceRolled = { result ->
                showDiceDialog = false
                viewModel.onDiceRolled(result,density)
            }
        )
    }
        GameScreen(viewModel)
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.8f))
    ) {
        // Side Grass
        SideGrass(Modifier.align(Alignment.CenterStart))
        SideGrass(Modifier.align(Alignment.CenterEnd))


        // Red and white lines on the roadside
        RedAndWhiteLines(modifier = Modifier.padding(start = 20.dp).align(Alignment.CenterStart))
        RedAndWhiteLines(modifier = Modifier.padding(end = 20.dp).align(Alignment.CenterEnd))

        // Finish Line
        FinishLine(modifier = Modifier.align(Alignment.TopCenter), finishLinePos = { coordinates ->
            viewModel.finishLinePos = IntOffset(x = coordinates.positionInWindow().x.roundToInt(), y = coordinates.positionInWindow().y.roundToInt())
        })

        // Road and Lanes
        RoadAndLanes()

        val firstCarAnim by animateIntAsState(targetValue = viewModel.firstCarPositionY, animationSpec = tween(300))
        val secondCarAnim by animateIntAsState(targetValue = viewModel.secondCarPositionY, animationSpec = tween(300))

        Car(
            modifier = Modifier
                .offset(x = 50.dp)
                .offset { IntOffset(x = 0, y = firstCarAnim) }
                .size(60.dp),
            painter = painterResource(R.drawable.red_car),
        )
        Car(
            modifier = Modifier
                .offset(x = 150.dp)
                .offset { IntOffset(x = 0, y = secondCarAnim) }
                .size(60.dp),
            painter = painterResource(R.drawable.yellow_car),
        )

        BottomScreenPositionReference(Modifier.align(Alignment.BottomStart)) { coordinates ->
            if (viewModel.secondCarPositionY == 0 && viewModel.firstCarPositionY == 0) {
                viewModel.secondCarPositionY = coordinates.positionInWindow().y.roundToInt()
                viewModel.firstCarPositionY = coordinates.positionInWindow().y.roundToInt()
            }
        }
    }
}
