package com.syntax.whackmole

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LeaderboardScreen(viewModel: GameViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Leaderboard",
                fontSize = 36.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                viewModel.topScores.forEachIndexed { index, score ->
                    Text(
                        text = "${index + 1}. $score",
                        fontSize = 24.sp,
                        color = when (index) {
                            0 -> Color(0xFFFFD700) // Gold
                            1 -> Color(0xFFC0C0C0) // Silver
                            2 -> Color(0xFFCD7F32) // Bronze
                            else -> Color.Black
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (viewModel.topScores.isEmpty()) {
                    Text(
                        text = "No scores yet!",
                        fontSize = 24.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.hideLeaderboard() },
                modifier = Modifier.width(200.dp).height(60.dp)
            ) {
                Text("Back", fontSize = 24.sp)
            }
        }
    }
}