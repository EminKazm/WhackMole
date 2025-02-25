package com.syntax.whackmole

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
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
        val bgRes = when (viewModel.difficultyy) {
            Difficulty.EASY -> R.drawable.bg_easy
            Difficulty.MEDIUM -> R.drawable.bg_medium
            Difficulty.HARD -> R.drawable.bg_hard
        }
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Score: ${viewModel.score}", fontSize = 20.sp, color = Color.DarkGray)
                Text("High Score: ${viewModel.highScore}", fontSize = 20.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Difficulty: ${viewModel.difficultyy.name}", fontSize = 20.sp, color = Color.DarkGray) // Fixed typo
                Text("Time: ${viewModel.timeLeft}s", fontSize = 20.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(48.dp))

            GameGrid(viewModel, hammerPosition, isHammerWhacking)

            Spacer(modifier = Modifier.height(32.dp))

            if (!viewModel.isGameRunning && !viewModel.isGameOver) {
                Button(
                    onClick = { viewModel.startGame() },
                    modifier = Modifier.width(200.dp).height(60.dp)
                        .border(width = 2.dp, color = Color.LightGray, shape = CircleShape),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White)
                ) {
                    Text("Start", fontSize = 24.sp)
                }
            }
            // Add Banner Ad at the bottom
            BannerAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally)
            )
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
        // Game Over Dialog
        if (viewModel.isGameOver) {
            GameOverDialog(viewModel)
        }
    }
}
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                // Use your Ad Unit ID here; test ID used for now
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
@Composable
fun GameOverDialog(viewModel: GameViewModel) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val canWatchAd = viewModel.canWatchRewardedAd()
    val infiniteTransition = rememberInfiniteTransition()

    // Animate scale continuously between 0.9f and 1.1f
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "PulsingAnimation"
    )
    Dialog(onDismissRequest = { /* Do nothing to prevent dismissal by clicking outside */ }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            modifier = Modifier
                .padding(26.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Over",
                    fontSize = 28.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Score: ${viewModel.score}",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Text(
                    text = "High Score: ${viewModel.highScore}",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.replay),
                        contentDescription = "Share",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { viewModel.startGame() }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = "Menu",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { viewModel.stopGameAndReturnToSplash() }
                    )
                    if (canWatchAd) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, // Align items in the center vertically
                            modifier = Modifier.clickable {
                                activity?.showRewardedAd {
                                    viewModel.resumeGameWithExtraTime(10)
                                    viewModel.incrementAdWatches()
                                }
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ad),
                                contentDescription = "Watch Ad",
                                modifier = Modifier.size(50.dp).graphicsLayer(scaleX = scale, scaleY = scale)
                            )

                            Spacer(modifier = Modifier.width(2.dp)) // Add spacing between image and text

                            Text(
                                text = "+10",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

            }
        }
    }
}