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

import android.app.NotificationManager
import androidx.activity.compose.BackHandler
import com.example.service.UnlockMonitorService

class UnlockAyahActivity : ComponentActivity() {

  private val viewModel: QuranViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Clear alert notification if it was shown
    try {
      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      notificationManager?.cancel(UnlockMonitorService.NOTIFICATION_ID_ALERT)
    } catch (_: Exception) {}

    configureShowOnLockScreen()

    setContent {
      BackHandler {
        dismissUnlockActivity()
      }

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
    window.addFlags(
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
      WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      setShowWhenLocked(true)
      setTurnScreenOn(true)
      val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
      keyguardManager?.requestDismissKeyguard(this, null)
    } else {
      @Suppress("DEPRECATION")
      window.addFlags(
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
          WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
          WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
      )
    }
  }

  private fun dismissUnlockActivity() {
    viewModel.pauseAudio()
    moveTaskToBack(true)
    finishAndRemoveTask()
    overridePendingTransition(0, android.R.anim.fade_out)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.pauseAudio()
  }
}
