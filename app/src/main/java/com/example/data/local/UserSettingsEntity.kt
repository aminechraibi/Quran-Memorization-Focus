package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
  @PrimaryKey val id: Int = 1,
  val currentAyahId: Int = 1,
  val unlockDisplayEnabled: Boolean = true,
  val cooldownMinutes: Int = 5,
  val lastUnlockTriggeredTimestamp: Long = 0L,
  val selectedReciter: String = "Husary_128kbps", // Mahmoud Khalil Al-Hussary
  val autoPlayAudioOnUnlock: Boolean = false,
  val arabicFontSizeSp: Int = 30,
  val showTranslation: Boolean = true,
  val showTransliteration: Boolean = false,
  val repeatCountGoal: Int = 5,
  val offlineAudioCacheEnabled: Boolean = true,
  val themeDimMode: Boolean = false,
  val versesCount: Int = 1, // Number of verses shown: 1 to 10, default 1
  val verseJumpStep: Int = 1 // Progression jump stride: 1 to versesCount, default 1
)
