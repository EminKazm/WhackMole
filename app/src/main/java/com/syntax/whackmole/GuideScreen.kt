package com.syntax.whackmole

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GuideScreen(viewModel: GameViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally, // Aligns the Column contents center
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.mole_guide),
                contentDescription = "Mole Guide",
                modifier = Modifier.size(200.dp)
            )
            Text(
                text = "Welcome to Mole Smash!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "How to Play:",
                fontSize = 24.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Left-aligned Column for text & images
            Column(
                modifier = Modifier.fillMaxWidth(), // Makes sure everything is aligned left
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Image(painterResource(R.drawable.hammer), "Hammer", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tap moles with the hammer to score points.", fontSize = 18.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Image(painterResource(R.drawable.mole), "Regular Mole", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Regular moles give 1000 points.", fontSize = 18.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Image(painterResource(R.drawable.mole2), "Golden Mole", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Golden moles give 1000 points!", fontSize = 18.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Image(painterResource(R.drawable.mole_with_bomb), "Bomb Mole", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bomb reduces 15 points!", fontSize = 18.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Image(painterResource(R.drawable.time), "Time Bonus", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Time adds 5 seconds!", fontSize = 18.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.hideGuide() },
                modifier = Modifier.width(200.dp).height(60.dp)
                    .border(width = 4.dp, color = Color.LightGray, shape = CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White)
            ) {
                Text("Start", fontSize = 24.sp)
            }
        }
    }
}
