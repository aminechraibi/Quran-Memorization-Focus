package com.example

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.MemorizeUnlockScreen
import com.example.ui.theme.QuranMemorizerTheme
import com.example.viewmodel.QuranViewModel

class UnlockAyahActivity : ComponentActivity() {

  private val viewModel: QuranViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    configureShowOnLockScreen()

    setContent {
      QuranMemorizerTheme(darkTheme = true) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        MemorizeUnlockScreen(
          ayah = uiState.currentAyah,
          ayahs = uiState.currentAyahs,
          settings = uiState.settings,
          audioState = uiState.audioState,
          isTestMode = false,
          onFontSizeChanged = { viewModel.setArabicFontSize(it) },
          onCycleAudioSpeed = { viewModel.cycleAudioSpeed() },
          onIncrementRepetition = { viewModel.incrementCurrentAyahRepetition() },
          onDecrementRepetition = { viewModel.decrementCurrentAyahRepetition() },
          onDoneClicked = {
            viewModel.onDoneClicked {
              dismissUnlockActivity()
            }
          },
          onRepeatClicked = {
            viewModel.onRepeatClicked {
              dismissUnlockActivity()
            }
          },
          onPreviousClicked = { viewModel.previousAyah() },
          onNextClicked = { viewModel.nextAyah() },
          onToggleAudio = { viewModel.toggleAudioPlayback() },
          onReplayAudio = { viewModel.replayAudio() },
          onSeekAudio = { viewModel.seekAudio(it) },
          onDismiss = { dismissUnlockActivity() }
        )
      }
    }
  }

  private fun configureShowOnLockScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      setShowWhenLocked(true)
      setTurnScreenOn(true)
      val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
      keyguardManager.requestDismissKeyguard(this, null)
    } else {
      @Suppress("DEPRECATION")
      window.addFlags(
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
          WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
          WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
          WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
      )
    }
  }

  private fun dismissUnlockActivity() {
    finishAndRemoveTask()
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
  }
}
