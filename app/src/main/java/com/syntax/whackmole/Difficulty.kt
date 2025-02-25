package com.syntax.whackmole

enum class Difficulty(val spawnChance: Int, val updateInterval: Long) {
    EASY(5, 3000L),   // 1/5 chance (~20%), updates every 3s
    MEDIUM(3, 800L),  // 1/3 chance (~33%), updates every 0.8s
    HARD(2, 400L)     // 1/3 chance (~33%), updates every 0.7s
}
