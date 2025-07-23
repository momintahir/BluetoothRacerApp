import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.momin.bluetoothracerapp.R
import com.momin.bluetoothracerapp.feature.gameplay.presentation.GameViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GameMainScreen(isHost:Boolean, navController: NavController) {
    val viewModel: GameViewModel = koinViewModel { parametersOf(isHost) }

    val isMyTurn by viewModel.isMyTurn
    val myName by viewModel.playerName
    val opponentName by viewModel.opponentName
    var showDiceDialog by remember { mutableStateOf(false) }
    val myCarPosition = viewModel.myCarPosition.value
    val opponentsCarPosition = viewModel.opponentCarPosition.value
    LaunchedEffect(isMyTurn) {
        if (isMyTurn) {
            delay(3000)
            showDiceDialog = true
        }
    }

    if (showDiceDialog) {
        DiceRollDialog(
            onDiceRolled = { result ->
                showDiceDialog = false
                viewModel.onDiceRolled(result)
            }
        )
    }

    if (isHost) {
        GameScreen(myCarPosition, opponentsCarPosition) // Host: Red = me, Blue = opponent
    } else {
        GameScreen(opponentsCarPosition, myCarPosition) // Guest: Red = opponent, Blue = me
    }
}
@Composable
fun DiceRollDialog(onDiceRolled: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Your Turn!") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text("Tap to roll the dice!")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val diceResult = (1..6).random()
                    onDiceRolled(diceResult)
                }) {
                    Text("🎲 Roll Dice")
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun GameScreen(myCarPosition: Int, opponentsCarPosition: Int, modifier: Modifier = Modifier) {
    RacingTrack(myCarPosition,opponentsCarPosition)
}

@Composable
fun RacingTrack(myCarPosition: Int, opponentsCarPosition: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        // Side Grass and Crowd (Left)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp)
                .align(Alignment.CenterStart)
                .background(Color(0xFF3DBD3D))
        )

        // Side Grass and Crowd (Right)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp)
                .align(Alignment.CenterEnd)
                .background(Color(0xFF3DBD3D))
        )

        // Finish Line
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FINISH",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            CheckerboardLine()
        }

        // Road and Lanes
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(20) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(20.dp)
                                .background(Color.White)
                        )
                    }
                }
            }
        }

        val myCarOffset by animateDpAsState(
            targetValue = (myCarPosition * -20).dp, // Adjust based on track design
            animationSpec = tween(durationMillis = 300)
        )
        val opponentsCarOffset by animateDpAsState(
            targetValue = (opponentsCarPosition * (-20)).dp, // Adjust based on track design
            animationSpec = tween(durationMillis = 300)
        )

        Car(
            modifier = Modifier
                .offset(x = 50.dp)
                .align(Alignment.BottomStart),
            painter = painterResource(R.drawable.red_car),
            yOffset = myCarOffset.value
        )
        Car(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 150.dp),
            painter = painterResource(R.drawable.yellow_car),
            yOffset = opponentsCarOffset.value
        )
    }
}

@Composable
fun Car(modifier: Modifier = Modifier, painter: Painter, yOffset: Float = 0f) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
//                .graphicsLayer {
//                    translationY = yOffset
//                }
                .size(64.dp)
                .align(Alignment.Center)
                .offset(y = yOffset.dp)
        )
    }
}

@Composable
fun CheckerboardLine() {
    Column {
        Row {
            repeat(19) {
                Box(
                    modifier = Modifier
                        .size(15.dp, 15.dp)
                        .background(if (it % 2 == 0) Color.White else Color.Black)
                )
            }
        }

        Row {
            repeat(19) {
                Box(
                    modifier = Modifier
                        .size(15.dp, 15.dp)
                        .background(if (it % 2 == 0) Color.Black else Color.White)
                )
            }
        }
    }
}

