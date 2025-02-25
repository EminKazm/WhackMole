package com.syntax.whackmole

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.pow

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

    // Wiggle animation
    val wiggle by animateFloatAsState(
        targetValue = if (isMoleVisible) 5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "moleWiggle"
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
                    .rotate(wiggle) // Apply wiggle effect

                    .clip(CircleShape)
                    .background(moleType.color)
            ) {
                if (scale > 0f) {
                    if (moleType.label=="BOMB"){
                        Image(
                            painter = painterResource(id = R.drawable.mole_with_bomb),
                            contentDescription = "Bomb",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    else if (moleType.label=="TIME"){
                        Image(
                            painter = painterResource(id = R.drawable.clock2),
                            contentDescription = "Time",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else if (moleType.label=="GOLD"){
                        Image(
                            painter = painterResource(id = R.drawable.mole2),
                            contentDescription = "Golden",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else{
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
}