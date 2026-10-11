package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.QuranApplication
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    if (context == null || intent == null) return

    val action = intent.action
    if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
      val app = context.applicationContext as? QuranApplication ?: return
      app.applicationScope.launch {
        val settings = app.repository.settingsFlow.firstOrNull()
        if (settings?.unlockDisplayEnabled != false) {
          UnlockMonitorService.startService(context)
        }
      }
    }
  }
}
