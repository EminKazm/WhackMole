package com.syntax.whackmole

enum class Difficulty(val spawnChance: Int, val updateInterval: Long) {
    EASY(6, 5000L),    // 1/6 chance (~16%), updates every 1.5s
    MEDIUM(4, 1000L),  // 1/4 chance (25%), updates every 1s
    HARD(3, 700L)      // 1/3 chance (~33%), updates every 0.7s
}
