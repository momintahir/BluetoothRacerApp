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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momin.bluetoothracerapp.R
import com.momin.bluetoothracerapp.feature.gameplay.presentation.GameViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameMainScreen(viewModel: GameViewModel = koinViewModel()) {
    GameScreen()
}

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    RacingTrack()
}

@Composable
fun RacingTrack() {
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

        Car(
            modifier = Modifier
                .offset(x = 50.dp)
                .align(Alignment.BottomStart),
            painter = painterResource(R.drawable.red_car),
            lane = 2
        )
        Car(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 150.dp)
                .rotate(70f),
            painter = painterResource(R.drawable.blue_car),
            lane = 1
        )
    }
}

@Composable
fun Car(modifier: Modifier = Modifier, painter: Painter, lane: Int, yOffset: Float = 0f) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer {
                    translationY = yOffset
                }
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

