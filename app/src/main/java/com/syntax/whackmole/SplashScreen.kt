package com.syntax.whackmole

import android.graphics.fonts.FontStyle
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syntax.whackmole.ui.theme.DarkerGreen


@Composable
fun SplashScreen(viewModel: GameViewModel) {
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
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Splash Screen",
            contentScale = ContentScale.Crop, // Adjust to fill the screen
            modifier = Modifier.matchParentSize() // Make it cover the full screen
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(64.dp)) // Moves content down

            Text(
                text = "Whack-a-Mole",
                fontSize = 36.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(64.dp)) // Moves content down

            Text(
                text = "High Score: ${viewModel.highScore}",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )



            Spacer(modifier = Modifier.height(64.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.play_button), // Replace with your play button image
                    contentDescription = "Play Button",
                    modifier = Modifier
                        .size(80.dp) // Set the size of the image
                        .graphicsLayer(scaleX = scale, scaleY = scale) // Apply pulsing effect
                        .clickable { viewModel.showGameScreen() } // Make it clickable
                )
                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    painter = painterResource(id= R.drawable.cup),
                    contentDescription = "Leadboard",
                    modifier = Modifier.size(80.dp)
                        .clickable { viewModel.showLeaderboard() }
                )
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id= R.drawable.settings),
                    contentDescription = "Share",
                    modifier = Modifier.size(80.dp)
                        .clickable { viewModel.showSettings() }
                )
            }
        }
    }

}