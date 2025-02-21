package com.syntax.whackmole

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(viewModel: GameViewModel) {
    var hammerPosition by remember { mutableStateOf(Offset.Zero) }
    var isHammerWhacking by remember { mutableStateOf(false) }
    val hammerRotation by animateFloatAsState(
        targetValue = if (isHammerWhacking) 45f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "hammerRotation"
    )
    val hammerScale by animateFloatAsState(
        targetValue = if (isHammerWhacking) 1.5f else 1f, // Scale up on whack
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "hammerScale",
    )
    val density = LocalDensity.current
    // Handle back button press
    BackHandler(enabled = viewModel.isGameScreenVisible) {
        viewModel.stopGameAndReturnToSplash()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    hammerPosition = offset
                    isHammerWhacking = true
                    viewModel.viewModelScope.launch {
                        delay(200L)
                        isHammerWhacking = false
                    }
                }
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Score: ${viewModel.score}", fontSize = 20.sp)
                Text("High Score: ${viewModel.highScore}", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Difficulty: ${viewModel.difficultyy.name}", fontSize = 20.sp) // Fixed typo
                Text("Time: ${viewModel.timeLeft}s", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            GameGrid(viewModel, hammerPosition, isHammerWhacking)

            Spacer(modifier = Modifier.height(32.dp))

            if (!viewModel.isGameRunning) {
                Button(
                    onClick = { viewModel.startGame() },
                    modifier = Modifier.width(200.dp).height(60.dp)
                ) {
                    Text("Start", fontSize = 24.sp)
                }
            }
        }

        // Hammer cursor - Always visible during game, follows tap
        if (viewModel.isGameRunning) {
            Image(
                painter = painterResource(id = viewModel.hammerSkins.resourceId),
                contentDescription = "Hammer",
                modifier = Modifier
                    .size(45.dp)
                    .offset {
                        with(density) {
                            IntOffset(
                                (hammerPosition.x - 60.dp.toPx()).toInt(), // Center hammer on X
                                (hammerPosition.y - 45.dp.toPx()).toInt() // Hammer head at tap point
                            )
                        }
                    }
                    .rotate(hammerRotation)
                    .scale(hammerScale) // Apply slam effect

            )
        }
    }
}
