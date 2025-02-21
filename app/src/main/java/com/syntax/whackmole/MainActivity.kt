package com.syntax.whackmole

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntax.whackmole.ui.theme.WhackMoleTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhackMoleTheme {
                Surface {
                    WhackAMoleGame()
                }
            }
        }
    }
}
@Stable
// Define Mole types
enum class MoleType(val points: Int, val color: Color, val label: String) {
    NONE(0, Color.Gray, ""),              // No mole
    REGULAR(1000, Color.Green, "MOLE"),     // Regular mole
    GOLDEN(250, Color.Yellow, "GOLD"),     // Golden mole (higher points)
    BOMB(-15, Color.Red, "BOMB")          // Bomb mole (lose points)
}
enum class Difficulty(val spawnChance: Int, val updateInterval: Long) {
    EASY(6, 5000L),    // 1/6 chance (~16%), updates every 1.5s
    MEDIUM(4, 1000L),  // 1/4 chance (25%), updates every 1s
    HARD(3, 700L)      // 1/3 chance (~33%), updates every 0.7s
}


@Composable
fun WhackAMoleGame(viewModel: GameViewModel = viewModel()) {
    val context = LocalContext.current

    // Initialize sounds when composable is first composed
    LaunchedEffect(Unit) {
        viewModel.initializeSounds(context)
    }
    when {
        viewModel.isSettingsVisible -> SettingsScreen(viewModel)
        viewModel.isGameScreenVisible -> GameScreen(viewModel)
        else -> SplashScreen(viewModel)
    }

}

@Composable
fun GameGrid(viewModel: GameViewModel, hammerPosition: Offset, isHammerWhacking: Boolean) {
    val density = LocalDensity.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {

        items(25) { index ->
            MoleHole(
                moleType = viewModel.moleStates[index],
                difficulty = viewModel.difficultyy, // Fixed typo: difficultyy -> difficulty
                position = with(density) {
                    Offset(
                        (index % 5 * 76 + 38).dp.toPx(), // Adjusted for 60dp + 16dp total padding
                        (index / 5 * 76 + 38 + 120).dp.toPx() // Adjusted offset for UI
                    )
                },
                hammerPosition = hammerPosition,
                isHammerWhacking = isHammerWhacking,
                onWhack = { viewModel.whackMole(index) }
            )
        }
    }
}
@Composable
fun MoleHole(
    moleType: MoleType,
    difficulty: Difficulty,
    position: Offset,
    hammerPosition: Offset,
    isHammerWhacking: Boolean,
    onWhack: () -> Unit
) {
    val isMoleVisible = moleType != MoleType.NONE
    val density = LocalDensity.current

    val scale by animateFloatAsState(
        targetValue = if (isMoleVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "moleScale"
    )

    LaunchedEffect(isHammerWhacking) {
        if (isHammerWhacking && isMoleVisible) {
            val distance = kotlin.math.sqrt(
                (hammerPosition.x - position.x).pow(2) + (hammerPosition.y - position.y).pow(2)
            )
            with(density) {
                if (distance < 50.dp.toPx()) {
                    onWhack()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.hole2), // Replace with your hole image
            contentDescription = "Hole",
            modifier = Modifier.fillMaxSize()
        )
        if (isMoleVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .clip(CircleShape)
                    .background(moleType.color)
            ) {
                if (scale > 0f) {
                    Image(
                        painter = painterResource(id = R.drawable.mole),
                        contentDescription = "Mole",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}