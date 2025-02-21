package com.syntax.whackmole

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var score by mutableStateOf(0)
    var highScore by mutableStateOf(0)

    var timeLeft by mutableStateOf(30)
    var moleStates by mutableStateOf(List(25) { MoleType.NONE })
    var difficultyy by mutableStateOf(Difficulty.EASY) // Default difficulty
    var isGameRunning by mutableStateOf(false) // Track game state
    var isGameScreenVisible by mutableStateOf(false) // Tracks if game screen is shown
    var isSettingsVisible by mutableStateOf(false) // New: Settings visibility
    var isSoundEnableds by mutableStateOf(true) // New: Sound toggle
    var hammerSkins by mutableStateOf(HammerSkin.DEFAULT) // New: Hammer skin selection
    private var moleUpdateJob: Job? = null

    private var gameJob: Job? = null
    private var tickPlayer: MediaPlayer? = null
    private var whackPlayer: MediaPlayer? = null
    private lateinit var sharedPrefs: android.content.SharedPreferences

    fun initializeSounds(context: android.content.Context) {
        sharedPrefs = context.getSharedPreferences("WhackAMolePrefs", Context.MODE_PRIVATE)
        highScore = sharedPrefs.getInt("highScore", 0) // Load high score on init
        isSoundEnableds = sharedPrefs.getBoolean("isSoundEnabled", true)
        hammerSkins = HammerSkin.values()[sharedPrefs.getInt("hammerSkin", 0)]
        tickPlayer = MediaPlayer.create(context, R.raw.tick).apply {
            isLooping = true
        }
        whackPlayer = MediaPlayer.create(context, R.raw.mole_whack)
    }

    fun setDifficulty(newDifficulty: Difficulty) {
        difficultyy = newDifficulty
    }
    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnableds = enabled
        sharedPrefs.edit().putBoolean("isSoundEnabled", enabled).apply()
    }

    fun setHammerSkin(skin: HammerSkin) {
        hammerSkins = skin
        sharedPrefs.edit().putInt("hammerSkin", skin.ordinal).apply()
    }
    fun showGameScreen() {
        isGameScreenVisible = true
        isGameRunning = false // Ensure game isn’t running yet
        score = 0
        timeLeft = 30
        moleStates = List(25) { MoleType.NONE }
    }
    fun showSettings() {
        isSettingsVisible = true
        isGameScreenVisible = false
    }

    fun hideSettings() {
        isSettingsVisible = false
    }
    fun stopGameAndReturnToSplash() {
        gameJob?.cancel()
        moleUpdateJob?.cancel()
        tickPlayer?.pause()
        isGameRunning = false
        isGameScreenVisible = false
    }


    fun startGame() {
        gameJob?.cancel()
        moleUpdateJob?.cancel()
        score = 0
        timeLeft = 30
        moleStates = List(25) { MoleType.NONE }
        isGameRunning = true

        if (isSoundEnableds) tickPlayer?.start()

        gameJob = viewModelScope.launch {
            while (timeLeft > 0) {
                delay(1000L) // Fixed 1-second interval
                timeLeft--
                updateMoles()
                if (timeLeft == 0) {
                    moleStates = List(25) { MoleType.NONE }
                    break
                }
            }
            tickPlayer?.pause()
            updateHighScore()
            isGameRunning = false // Game ends, stay on game screen


        }
        // Mole update coroutine (uses difficulty-specific interval)
        moleUpdateJob = viewModelScope.launch {
            while (timeLeft > 0) {
                delay(difficultyy.updateInterval)
                updateMoles()
            }
        }
    }

    private fun updateMoles() {
        moleStates = moleStates.map {
            if (Random.nextInt(difficultyy.spawnChance) == 0) { // 20% chance of a mole appearing
                when (Random.nextInt(100)) {
                    in 0..69 -> MoleType.REGULAR  // 70% chance
                    in 70..89 -> MoleType.GOLDEN  // 20% chance
                    else -> MoleType.BOMB         // 10% chance
                }
            } else {
                MoleType.NONE
            }
        }
    }
    private fun updateHighScore() {
        if (score > highScore) {
            highScore = score
            sharedPrefs.edit().putInt("highScore", highScore).apply()
        }
    }
//    fun whackMole(index: Int) {
//        val moleType = moleStates[index]
//        if (moleType != MoleType.NONE) {
//            score += moleType.points
//            moleStates = moleStates.toMutableList().apply { this[index] = MoleType.NONE }
//            whackPlayer?.let {
//                it.seekTo(0)
//                it.start()
//            }
//        }
//        updateHighScore() // Update high score immediately after scoring
//
//    }
    fun whackMole(index: Int) {
        val moleType = moleStates[index]
        if (moleType != MoleType.NONE) {
            score += moleType.points
            moleStates = moleStates.toMutableList().apply { this[index] = MoleType.NONE }
            if (isSoundEnableds) whackPlayer?.let { it.seekTo(0); it.start() }
            updateHighScore()
        }
    }
    override fun onCleared() {
        super.onCleared()
        tickPlayer?.release()
        whackPlayer?.release()
        tickPlayer = null
        whackPlayer = null
    }
}