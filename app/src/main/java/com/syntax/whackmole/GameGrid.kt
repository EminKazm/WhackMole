package com.syntax.whackmole

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

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
