package com.syntax.whackmole

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
// Define Mole types
enum class MoleType(val points: Int, val color: Color, val label: String, val time:Int=0) {
    NONE(0, Color.Gray, ""),              // No mole
    REGULAR(10, Color.Transparent, "MOLE"),     // Regular mole
    GOLDEN(50, Color.Transparent, "GOLD"),     // Golden mole (higher points)
    BOMB(-15, Color.Transparent, "BOMB"),
    TIME(0, Color.Transparent, "TIME",5)// Bomb mole (lose points)
}