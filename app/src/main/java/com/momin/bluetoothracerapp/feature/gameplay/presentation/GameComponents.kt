package com.momin.bluetoothracerapp.feature.gameplay.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momin.bluetoothracerapp.R
import com.momin.bluetoothracerapp.core.speechrecognizer.SpeechRecognizerManager
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

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
fun CheckerboardLine() {
    val boxSize = LocalDensity.current.run { 8.dp.roundToPx() }
    Column(modifier = Modifier.fillMaxWidth(0.8f)) {
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

@Composable
fun RoadAndLanes(modifier: Modifier = Modifier) {
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
}

@Composable
fun FinishLine(modifier: Modifier = Modifier, finishLinePos: (coordinates: LayoutCoordinates) -> Unit) {
    val topPadding = LocalDensity.current.run { 10.dp.roundToPx() }
    val spacingHeight = LocalDensity.current.run { 6.dp.roundToPx() }

    Column(
        modifier = modifier
            .padding(top = topPadding.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FINISH",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(spacingHeight.dp))
        CheckerboardLine()
        Box(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    finishLinePos(coordinates)
                })
    }
}

    @Composable
    fun RedAndWhiteLines(modifier: Modifier = Modifier) {
        Box(modifier = modifier
            .fillMaxHeight()
            .width(20.dp)) {
            Column {
                repeat(100) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(if (it % 2 == 0) Color.White else Color.Red)
                    )
                }
            }
        }
    }

@Composable
fun SideGrass(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(40.dp)
            .background(Color(0xFF3DBD3D))
    )
}

@Composable
fun BottomScreenPositionReference(modifier: Modifier = Modifier, onPositioned: (coordinates: LayoutCoordinates) -> Unit) {
    val boxSize = LocalDensity.current.run { 30.dp.roundToPx() }

    Box(modifier
        .size(boxSize.dp)
        .onGloballyPositioned { coordinates ->
           onPositioned(coordinates)
        })
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