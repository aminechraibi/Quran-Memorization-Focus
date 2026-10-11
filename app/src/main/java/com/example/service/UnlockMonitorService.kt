package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.QuranApplication
import com.example.UnlockAyahActivity
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class UnlockMonitorService : Service() {

  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private lateinit var repository: QuranRepository
  private var isReceiverRegistered = false

  private val unlockReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == Intent.ACTION_USER_PRESENT) {
        handleDeviceUnlock()
      }
    }
  }

  override fun onCreate() {
    super.onCreate()
    repository = (application as QuranApplication).repository

    val filter = IntentFilter().apply {
      addAction(Intent.ACTION_USER_PRESENT)
      addAction(Intent.ACTION_SCREEN_ON)
    }
    registerReceiver(unlockReceiver, filter)
    isReceiverRegistered = true

    startForeground(NOTIFICATION_ID_MONITOR, buildMonitorNotification("Ready for next unlock"))
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    serviceScope.launch {
      val settings = repository.settingsFlow.firstOrNull()
      if (settings?.unlockDisplayEnabled == false) {
        stopSelf()
      }
    }
    return START_STICKY
  }

  private fun handleDeviceUnlock() {
    serviceScope.launch {
      if (repository.isCooldownActive()) {
        return@launch
      }

      repository.recordUnlockTriggered()
      val settings = repository.settingsFlow.firstOrNull() ?: return@launch
      val currentAyah = repository.getAyahById(settings.currentAyahId).firstOrNull()
        ?: repository.getAyahOrFallback(settings.currentAyahId)

      val surahLabel = currentAyah?.let { "Surah ${it.surahNameEnglish} : Ayah ${it.ayahNumberInSurah}" }
        ?: "Current Ayah"

      // Check overlay permission
      val canDrawOverlays = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this@UnlockMonitorService)

      if (canDrawOverlays) {
        val unlockIntent = Intent(this@UnlockMonitorService, UnlockAyahActivity::class.java).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(unlockIntent)
      } else {
        // Fallback: Post high-priority full-screen intent notification
        postFullScreenNotification(surahLabel)
      }
    }
  }

  private fun postFullScreenNotification(ayahLabel: String) {
    val fullScreenIntent = Intent(this, UnlockAyahActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val fullScreenPendingIntent = PendingIntent.getActivity(
      this,
      200,
      fullScreenIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(this, QuranApplication.CHANNEL_FULLSCREEN)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle("Quran Memorization")
      .setContentText("$ayahLabel — Tap to review & memorize")
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setFullScreenIntent(fullScreenPendingIntent, true)
      .setContentIntent(fullScreenPendingIntent)
      .setAutoCancel(true)
      .addAction(
        android.R.drawable.ic_media_play,
        "Open Ayah",
        fullScreenPendingIntent
      )
      .build()

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    notificationManager.notify(NOTIFICATION_ID_ALERT, notification)
  }

  private fun buildMonitorNotification(statusText: String): Notification {
    val openMainIntent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      openMainIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(this, QuranApplication.CHANNEL_MONITOR)
      .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
      .setContentTitle("Quran Memorizer Active")
      .setContentText(statusText)
      .setOngoing(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setContentIntent(pendingIntent)
      .build()
  }

  override fun onDestroy() {
    super.onDestroy()
    if (isReceiverRegistered) {
      try {
        unregisterReceiver(unlockReceiver)
      } catch (_: Exception) {
      }
      isReceiverRegistered = false
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null

  companion object {
    const val NOTIFICATION_ID_MONITOR = 1001
    const val NOTIFICATION_ID_ALERT = 1002

    fun startService(context: Context) {
      val intent = Intent(context, UnlockMonitorService::class.java)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stopService(context: Context) {
      val intent = Intent(context, UnlockMonitorService::class.java)
      context.stopService(intent)
    }
  }
}
