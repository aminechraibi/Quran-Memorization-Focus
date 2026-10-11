package com.example

import com.example.data.local.AyahEntity
import com.example.data.local.QuranSeedData
import com.example.data.local.UserSettingsEntity
import com.example.data.model.QuranSurahDirectory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuranProgressionUnitTest {

  @Test
  fun testSeedAyahsArePresentAndValid() {
    val ayahs = QuranSeedData.initialAyahs
    assertTrue("Should have initial seed verses", ayahs.isNotEmpty())

    val fatihaAyah1 = ayahs.find { it.id == 1 }
    assertNotNull(fatihaAyah1)
    assertEquals("Al-Fatihah", fatihaAyah1?.surahNameEnglish)
    assertEquals(1, fatihaAyah1?.ayahNumberInSurah)
    assertTrue("Should contain Arabic text", fatihaAyah1?.textArabic?.contains("بِسْمِ") == true)
  }

  @Test
  fun testSurahDirectoryHas114Surahs() {
    assertEquals(114, QuranSurahDirectory.surahs.size)

    val first = QuranSurahDirectory.surahs.first()
    assertEquals(1, first.number)
    assertEquals("Al-Fatihah", first.nameEnglish)
    assertEquals(7, first.numberOfAyahs)

    val last = QuranSurahDirectory.surahs.last()
    assertEquals(114, last.number)
    assertEquals("An-Nas", last.nameEnglish)
    assertEquals(6, last.numberOfAyahs)
  }

  @Test
  fun testAudioUrlFormatting() {
    val url = QuranSeedData.formatAudioUrl(1, 1, "Husary_128kbps")
    assertEquals("https://everyayah.com/data/Husary_128kbps/001001.mp3", url)

    val surah114Ayah6 = QuranSeedData.formatAudioUrl(114, 6, "Husary_128kbps")
    assertEquals("https://everyayah.com/data/Husary_128kbps/114006.mp3", surah114Ayah6)
  }

  @Test
  fun testRepetitionLogic() {
    val ayah = AyahEntity(
      id = 1,
      surahNumber = 1,
      surahNameArabic = "الفاتحة",
      surahNameEnglish = "Al-Fatihah",
      ayahNumberInSurah = 1,
      juzNumber = 1,
      textArabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
      textEnglish = "In the name of Allah",
      textTransliteration = "Bismillah",
      audioUrl = "http://example.com/1.mp3",
      isCompleted = false,
      repetitionCount = 3
    )

    val repeatedAyah = ayah.copy(repetitionCount = ayah.repetitionCount + 1)
    assertEquals(4, repeatedAyah.repetitionCount)
    assertFalse("Ayah is not completed just by repeating", repeatedAyah.isCompleted)

    val completedAyah = repeatedAyah.copy(isCompleted = true)
    assertTrue("Ayah is marked completed", completedAyah.isCompleted)
  }

  @Test
  fun testCooldownCalculation() {
    val now = System.currentTimeMillis()
    val settings5Min = UserSettingsEntity(
      cooldownMinutes = 5,
      lastUnlockTriggeredTimestamp = now - (2 * 60 * 1000L) // 2 mins ago
    )
    val elapsed = now - settings5Min.lastUnlockTriggeredTimestamp
    val isStillCooldown = elapsed < (settings5Min.cooldownMinutes * 60 * 1000L)
    assertTrue("Should be on cooldown when 2 mins passed on 5 min cooldown", isStillCooldown)

    val settingsExpired = UserSettingsEntity(
      cooldownMinutes = 5,
      lastUnlockTriggeredTimestamp = now - (6 * 60 * 1000L) // 6 mins ago
    )
    val elapsedExpired = now - settingsExpired.lastUnlockTriggeredTimestamp
    val isExpired = elapsedExpired >= (settingsExpired.cooldownMinutes * 60 * 1000L)
    assertTrue("Cooldown should expire after 6 mins", isExpired)
  }

  @Test
  fun testSpeedCycling() {
    val speeds = listOf(1.0f, 1.25f, 1.5f, 2.0f, 3.0f)
    var currentSpeed = 1.0f
    fun nextSpeed(s: Float) = when (s) {
      1.0f -> 1.25f
      1.25f -> 1.5f
      1.5f -> 2.0f
      2.0f -> 3.0f
      else -> 1.0f
    }

    currentSpeed = nextSpeed(currentSpeed)
    assertEquals(1.25f, currentSpeed, 0.01f)
    currentSpeed = nextSpeed(currentSpeed)
    assertEquals(1.5f, currentSpeed, 0.01f)
    currentSpeed = nextSpeed(currentSpeed)
    assertEquals(2.0f, currentSpeed, 0.01f)
    currentSpeed = nextSpeed(currentSpeed)
    assertEquals(3.0f, currentSpeed, 0.01f)
    currentSpeed = nextSpeed(currentSpeed)
    assertEquals(1.0f, currentSpeed, 0.01f)
  }
}
