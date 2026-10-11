package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.audio.AudioCacheManager
import com.example.audio.QuranAudioPlayer
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class QuranApplication : Application() {

  val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  lateinit var repository: QuranRepository
    private set
  lateinit var audioCacheManager: AudioCacheManager
    private set
  lateinit var audioPlayer: QuranAudioPlayer
    private set

  override fun onCreate() {
    super.onCreate()
    createNotificationChannels()

    audioCacheManager = AudioCacheManager(this)
    audioPlayer = QuranAudioPlayer(this, audioCacheManager)
    repository = QuranRepository.getInstance(this)

    applicationScope.launch {
      repository.ensureInitialized()
    }
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      // Foreground service monitoring channel (low importance, no sound)
      val monitorChannel = NotificationChannel(
        CHANNEL_MONITOR,
        "Unlock Memorization Service",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Runs quietly in background to detect phone unlock and present current Ayah"
        setShowBadge(false)
      }

      // High-priority Full-Screen Intent channel (heads-up / prompt on unlock)
      val alertChannel = NotificationChannel(
        CHANNEL_FULLSCREEN,
        "Ayah Unlock Alerts",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Full-screen and heads-up notifications for Ayah memorization on unlock"
        enableVibration(true)
      }

      notificationManager.createNotificationChannel(monitorChannel)
      notificationManager.createNotificationChannel(alertChannel)
    }
  }

  companion object {
    const val CHANNEL_MONITOR = "quran_unlock_monitor"
    const val CHANNEL_FULLSCREEN = "quran_unlock_fullscreen"
  }
}
