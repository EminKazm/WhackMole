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
    var isSettingsVisible by mutableStateOf(false) //  Settings visibility
    var isGameOver by mutableStateOf(false) // Track game over state
    var isLeaderboardVisible by mutableStateOf(false) // Leaderboard visibility
    var isGuideVisible by mutableStateOf(false) //  Guide visibility

    var isSoundEnableds by mutableStateOf(true) // Sound toggle
    var hammerSkins by mutableStateOf(HammerSkin.DEFAULT) // Hammer skin selection
    var topScores by mutableStateOf(listOf<Int>()) //  Top 5 scores

    var rewardedAdWatches by mutableStateOf(0) // Track rewarded ad watches

    private var moleUpdateJob: Job? = null

    private var gameJob: Job? = null
    private var tickPlayer: MediaPlayer? = null
    private var whackPlayer: MediaPlayer? = null
    private lateinit var sharedPrefs: android.content.SharedPreferences

    // New: Ad watching
    companion object {
        const val MAX_AD_WATCHES = 3
    }
    fun initializeSounds(context: android.content.Context) {
        sharedPrefs = context.getSharedPreferences("WhackAMolePrefs", Context.MODE_PRIVATE)
        highScore = sharedPrefs.getInt("highScore", 0) // Load high score on init
        isSoundEnableds = sharedPrefs.getBoolean("isSoundEnabled", true)
        hammerSkins = HammerSkin.values()[sharedPrefs.getInt("hammerSkin", 0)]
        loadTopScores() // Load top scores on initialization
        // Check if first launch
        val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)
        isGuideVisible = isFirstLaunch
        if (isFirstLaunch) {
            sharedPrefs.edit().putBoolean("isFirstLaunch", false).apply()
        }
        tickPlayer = MediaPlayer.create(context, R.raw.tick).apply {
            isLooping = true
        }
        whackPlayer = MediaPlayer.create(context, R.raw.mole_whack)
    }
    private fun loadTopScores() {
        val scores = (0 until 5).map { index ->
            sharedPrefs.getInt("topScore$index", 0)
        }.filter { it > 0 }.sortedDescending()
        topScores = scores.take(5)
    }
    private fun saveTopScores() {
        val updatedScores = (topScores + score).sortedDescending().take(5)
        topScores = updatedScores
        updatedScores.forEachIndexed { index, value ->
            sharedPrefs.edit().putInt("topScore$index", value).apply()
        }
    }
    fun incrementAdWatches() {
        rewardedAdWatches++
        sharedPrefs.edit().putInt("rewardedAdWatches", rewardedAdWatches).apply()
    }

    fun canWatchRewardedAd(): Boolean {
        return rewardedAdWatches < MAX_AD_WATCHES
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
        isSettingsVisible = false
        isGameRunning = false
        isGameOver = false
        isLeaderboardVisible = false

        score = 0
        timeLeft = 30
        moleStates = List(25) { MoleType.NONE }
    }
    fun showSettings() {
        isSettingsVisible = true
        isGameScreenVisible = false
        isLeaderboardVisible = false

    }
    fun showLeaderboard() {
        isLeaderboardVisible = true
        isGameScreenVisible = false
        isSettingsVisible = false
    }
    fun showSplash(){
        isGameScreenVisible = false
        isSettingsVisible = false
        isLeaderboardVisible = false
    }
    fun hideLeaderboard() {
        isLeaderboardVisible = false
    }
    fun hideSettings() {
        isSettingsVisible = false
    }
    fun hideGuide() { isGuideVisible = false }

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
        isGameOver = false // Reset game over state

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
            saveTopScores() // Save scores when game ends

            isGameRunning = false // Game ends, stay on game screen
            isGameOver = true // Trigger game over dialog


        }
        // Mole update coroutine (uses difficulty-specific interval)
        moleUpdateJob = viewModelScope.launch {
            while (timeLeft > 0) {
                delay(difficultyy.updateInterval)
                updateMoles()
            }
        }
    }
    // New method to resume game with extra time
    fun resumeGameWithExtraTime(extraTime: Int) {
        gameJob?.cancel()
        moleUpdateJob?.cancel()
        timeLeft += extraTime // Add extra time to current timeLeft
        isGameRunning = true
        isGameOver = false
        if (isSoundEnableds) tickPlayer?.start()
        gameJob = viewModelScope.launch {
            while (timeLeft > 0) {
                delay(1000L)
                updateMoles()
                timeLeft--
            }
            tickPlayer?.pause()
            updateHighScore()
            saveTopScores()
            isGameRunning = false
            isGameOver = true
        }
        moleUpdateJob = viewModelScope.launch {
            while (timeLeft > 0) {
                delay(difficultyy.updateInterval)
                updateMoles()
            }
        }
    }

    private fun updateMoles() {
        if (!isGameRunning) return
        moleStates = moleStates.map {
            if (Random.nextInt(difficultyy.spawnChance) == 0) { // 20% chance of a mole appearing
                when (Random.nextInt(100)) {
                    in 0..70 -> MoleType.REGULAR  // 70% chance
                    in 71..83 -> MoleType.GOLDEN
                    in 84..86 -> MoleType.TIME  // 20% chance
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

    fun whackMole(index: Int) {
        val moleType = moleStates[index]
        if (moleType != MoleType.NONE) {
            score += moleType.points
            timeLeft += moleType.time // Add time bonus

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