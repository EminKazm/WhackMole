package com.syntax.whackmole

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(viewModel: GameViewModel) {
    BackHandler(enabled = viewModel.isSettingsVisible) {
        viewModel.hideSettings()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Settings",
                fontSize = 36.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Sound Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sound", fontSize = 20.sp)
                Switch(
                    checked = viewModel.isSoundEnableds,
                    onCheckedChange = { viewModel.setSoundEnabled(it) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Difficulty Selection
            Text("Difficulty", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Difficulty.values().forEach { diff ->
                    Button(
                        onClick = { viewModel.setDifficulty(diff) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.difficultyy == diff) Color.Green else Color.Gray
                        )
                    ) {
                        Text(diff.name)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Hammer Skin Selection
            Text("Hammer Skin", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HammerSkin.values().forEach { skin ->
                    Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    if (viewModel.hammerSkins == skin) Color.Green.copy(alpha = 0.3f) else Color.Transparent
                                )
                                .padding(4.dp)
                                .clickable { viewModel.setHammerSkin(skin) }
                            ) {
                        Image(
                            painter = painterResource(id = skin.resourceId),
                            contentDescription = skin.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.hideSettings() },
                modifier = Modifier.width(200.dp).height(60.dp)
            ) {
                Text("Back", fontSize = 24.sp)
            }
        }
    }
}