package com.syntax.whackmole

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SplashScreen(viewModel: GameViewModel) {

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
            Spacer(modifier = Modifier.height(32.dp)) // Moves content down

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
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )



            Spacer(modifier = Modifier.height(64.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.showGameScreen() },
                    modifier = Modifier.width(150.dp).height(60.dp)
                        .border(width = 2.dp, color = Color.LightGray, shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White)
                ) {
                    Text("Play", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.showLeaderboard() },
                    modifier = Modifier.width(150.dp).height(60.dp)
                        .border(width = 2.dp, color = Color.LightGray, shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White)
                ) {
                    Text("Leaderboard", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.showSettings() },
                    modifier = Modifier.width(150.dp).height(60.dp)
                        .border(width = 2.dp, color = Color.LightGray, shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White)
                ) {
                    Text("Settings", fontSize = 24.sp)
                }
            }
        }
    }

}