import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.momin.bluetoothracerapp.R
import com.momin.bluetoothracerapp.core.speechrecognizer.SpeechRecognizerManager
import com.momin.bluetoothracerapp.feature.gameplay.presentation.GameViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

@Composable
fun GameMainScreen(isHost:Boolean, navController: NavController) {
    val viewModel: GameViewModel = koinViewModel { parametersOf(isHost) }

    val isMyTurn by viewModel.isMyTurn
    val myName by viewModel.playerName
    val opponentName by viewModel.opponentName
    var showDiceDialog by remember { mutableStateOf(false) }
    val myCarPositionY = viewModel.myCarPositionY
    val opponentCarPositionY = viewModel.opponentCarPositionY
    LaunchedEffect(isMyTurn) {
        if (isMyTurn && !viewModel.isGameOver) {
            delay(3000)
            showDiceDialog = true
        }
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

    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        viewModel.initGame(density)
    }

    if (showDiceDialog) {
        DiceRollDialog(
            onDiceRolled = { result ->
                showDiceDialog = false
                viewModel.onDiceRolled(result,density)
            }
        )
    }

    if (isHost) {
        GameScreen(myCarPositionY, opponentCarPositionY,viewModel) // Host: Red = me, Blue = opponent
    } else {
        GameScreen(opponentCarPositionY, myCarPositionY,viewModel) // Guest: Red = opponent, Blue = me
    }
}
@Composable
fun DiceRollDialog(
    onDiceRolled: (Int) -> Unit
) {
    val context = LocalContext.current
    var currentDice by remember { mutableIntStateOf(R.drawable.dice_1) }
    var isRolling by remember { mutableStateOf(false) }

    val diceImages = listOf(
        R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3,
        R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6
    )

    val speechRecognizerManager = remember {
        SpeechRecognizerManager(context) { spokenText ->
            if ("roll" in spokenText || "start" in spokenText) {
                isRolling = true
            }
        }
    }

    DisposableEffect(Unit) {
        speechRecognizerManager.startListening()
        onDispose {
            speechRecognizerManager.stopListening()
            speechRecognizerManager.destroy()
        }
    }

    val transition = rememberInfiniteTransition(label = "diceBounce")
    val offsetY by transition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )
    val offsetX by transition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )

    LaunchedEffect(isRolling) {
        if (isRolling) {
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 3000) {
                currentDice = diceImages.random()
                delay(300)
            }
            isRolling = false
            val result = (1..6).random()
            currentDice = diceImages[result - 1]
            onDiceRolled(result)
        }
    }

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Your Turn!") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(currentDice),
                    contentDescription = "Dice",
                    modifier = Modifier
                        .offset(y = if (isRolling) offsetY.dp else 0.dp, x = if (isRolling) offsetX.dp else 0.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Say 'roll' or tap the button below!")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { isRolling = true }) {
                    Text("🎲 Roll Dice")
                }
            }
        },
        confirmButton = {}
    )
}


@Composable
fun GameScreen(myCarPosition: Int, opponentsCarPosition: Int, viewModel: GameViewModel, modifier: Modifier = Modifier) {
    RacingTrack(myCarPosition, opponentsCarPosition, viewModel)
}

@Composable
fun RacingTrack(myCarPosition: Int, opponentsCarPosition: Int, viewModel: GameViewModel) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .onGloballyPositioned {
            }
    ) {
        // Side Grass
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
        val topPadding = LocalDensity.current.run { 10.dp.roundToPx() }
        val spacingHeight = LocalDensity.current.run { 6.dp.roundToPx() }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = topPadding.dp)
                .onGloballyPositioned { coordinates ->
                }, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FINISH",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(spacingHeight.dp))
            CheckerboardLine()
            Box(Modifier.height(1.dp).fillMaxWidth().background(Color.Yellow).onGloballyPositioned{ coordinates ->
                viewModel.finishLinePos = IntOffset(x = coordinates.positionInWindow().x.roundToInt(), y = coordinates.positionInWindow().y.roundToInt())
            })
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

        val myCarAnim by animateIntAsState(targetValue = myCarPosition, animationSpec = tween(300))
        val opponentCarAnim by animateIntAsState(targetValue = opponentsCarPosition, animationSpec = tween(300))

        Car(
            modifier = Modifier
                .offset(x = 50.dp)
                .offset { IntOffset(x = 0, y = myCarAnim) }
                .size(60.dp),
            painter = painterResource(R.drawable.red_car),
        )
        Car(
            modifier = Modifier
                .offset(x = 150.dp)
                .offset { IntOffset(x = 0, y = opponentCarAnim) }
                .size(60.dp),
            painter = painterResource(R.drawable.yellow_car),
        )

        val boxSize = LocalDensity.current.run { 30.dp.roundToPx() }

        Box(Modifier
            .align(Alignment.BottomStart)
            .size(boxSize.dp)
            .onGloballyPositioned { coordinates ->
                if (viewModel.opponentCarPositionY == 0 && viewModel.myCarPositionY == 0) {
                    viewModel.opponentCarPositionY = coordinates.positionInWindow().y.roundToInt()
                    viewModel.myCarPositionY = coordinates.positionInWindow().y.roundToInt()
                }
            })
    }
}

@Composable
fun Car(modifier: Modifier = Modifier, painter: Painter) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun CheckerboardLine(){
    val boxSize = LocalDensity.current.run { 8.dp.roundToPx() }
    Column {
        Row {
            repeat(19) {
                Box(
                    modifier = Modifier
                        .size(boxSize.dp, boxSize.dp)
                        .background(if (it % 2 == 0) Color.White else Color.Black)
                )
            }
        }

        Row {
            repeat(19) {
                Box(
                    modifier = Modifier
                        .size(boxSize.dp, boxSize.dp)
                        .background(if (it % 2 == 0) Color.Black else Color.White)
                )
            }
        }
    }
}

