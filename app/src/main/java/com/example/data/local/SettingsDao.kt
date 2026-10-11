package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

  @Query("SELECT * FROM user_settings WHERE id = 1")
  fun getSettings(): Flow<UserSettingsEntity?>

  @Query("SELECT * FROM user_settings WHERE id = 1")
  suspend fun getSettingsSync(): UserSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveSettings(settings: UserSettingsEntity)

  @Query("UPDATE user_settings SET currentAyahId = :ayahId WHERE id = 1")
  suspend fun updateCurrentAyahId(ayahId: Int)

  @Query("UPDATE user_settings SET lastUnlockTriggeredTimestamp = :timestamp WHERE id = 1")
  suspend fun updateLastUnlockTimestamp(timestamp: Long)

  @Query("UPDATE user_settings SET unlockDisplayEnabled = :enabled WHERE id = 1")
  suspend fun setUnlockDisplayEnabled(enabled: Boolean)

  @Query("UPDATE user_settings SET cooldownMinutes = :cooldown WHERE id = 1")
  suspend fun setCooldownMinutes(cooldown: Int)

  @Query("UPDATE user_settings SET selectedReciter = :reciter WHERE id = 1")
  suspend fun setSelectedReciter(reciter: String)

  @Query("UPDATE user_settings SET autoPlayAudioOnUnlock = :autoPlay WHERE id = 1")
  suspend fun setAutoPlayAudio(autoPlay: Boolean)

  @Query("UPDATE user_settings SET arabicFontSizeSp = :fontSize WHERE id = 1")
  suspend fun setArabicFontSize(fontSize: Int)

  @Query("UPDATE user_settings SET showTranslation = :show WHERE id = 1")
  suspend fun setShowTranslation(show: Boolean)

  @Query("UPDATE user_settings SET showTransliteration = :show WHERE id = 1")
  suspend fun setShowTransliteration(show: Boolean)

  @Query("UPDATE user_settings SET repeatCountGoal = :goal WHERE id = 1")
  suspend fun setRepeatCountGoal(goal: Int)
}
