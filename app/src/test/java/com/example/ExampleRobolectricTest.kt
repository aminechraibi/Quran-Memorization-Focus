package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AyahEntity
import com.example.data.local.QuranDatabase
import com.example.data.local.QuranSeedData
import com.example.data.local.UserSettingsEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: QuranDatabase

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, QuranDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Quran Memorizer", appName)
  }

  @Test
  fun `test room insert and read ayahs`() = runBlocking {
    val quranDao = database.quranDao()
    quranDao.insertAyahs(QuranSeedData.initialAyahs)

    val count = quranDao.getAyahsCount()
    assertTrue("Should insert seed verses", count > 0)

    val ayah1 = quranDao.getAyahByIdSync(1)
    assertNotNull(ayah1)
    assertEquals(1, ayah1?.surahNumber)
    assertEquals("Al-Fatihah", ayah1?.surahNameEnglish)

    // Test Repetition
    quranDao.incrementRepetition(1)
    val repeated = quranDao.getAyahByIdSync(1)
    assertEquals(1, repeated?.repetitionCount)

    quranDao.setAyahRepetitionCount(1, 7)
    val customRepeated = quranDao.getAyahByIdSync(1)
    assertEquals(7, customRepeated?.repetitionCount)

    // Test Done
    quranDao.setAyahCompleted(1, true)
    val completed = quranDao.getAyahByIdSync(1)
    assertTrue(completed?.isCompleted == true)

    val completedCount = quranDao.getCompletedAyahsCount().first()
    assertEquals(1, completedCount)

    // Test Reset
    quranDao.resetAllProgress()
    val afterReset = quranDao.getAyahByIdSync(1)
    assertEquals(0, afterReset?.repetitionCount)
    assertEquals(false, afterReset?.isCompleted)
  }

  @Test
  fun `test room settings persistence`() = runBlocking {
    val settingsDao = database.settingsDao()
    settingsDao.saveSettings(UserSettingsEntity(currentAyahId = 5, cooldownMinutes = 15))

    val settings = settingsDao.getSettingsSync()
    assertNotNull(settings)
    assertEquals(5, settings?.currentAyahId)
    assertEquals(15, settings?.cooldownMinutes)

    settingsDao.updateCurrentAyahId(6)
    val updated = settingsDao.getSettingsSync()
    assertEquals(6, updated?.currentAyahId)

    // Test versesCount and verseJumpStep
    assertEquals(1, updated?.versesCount)
    assertEquals(1, updated?.verseJumpStep)

    settingsDao.setVersesCount(3)
    settingsDao.setVerseJumpStep(2)
    val multiSettings = settingsDao.getSettingsSync()
    assertEquals(3, multiSettings?.versesCount)
    assertEquals(2, multiSettings?.verseJumpStep)
  }

  @Test
  fun `test corrupt dummy bismillah cleanup`() = runBlocking {
    val quranDao = database.quranDao()
    // Insert valid ayah 1:1
    quranDao.insertAyah(
      AyahEntity(
        id = 1,
        surahNumber = 1,
        surahNameArabic = "الفاتحة",
        surahNameEnglish = "Al-Fatihah",
        ayahNumberInSurah = 1,
        juzNumber = 1,
        textArabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
        textEnglish = "In the name of Allah",
        textTransliteration = "Bismillah",
        audioUrl = ""
      )
    )

    // Insert corrupt dummy ayah at 2:6
    quranDao.insertAyah(
      AyahEntity(
        id = 13,
        surahNumber = 2,
        surahNameArabic = "البقرة",
        surahNameEnglish = "Al-Baqarah",
        ayahNumberInSurah = 6,
        juzNumber = 1,
        textArabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
        textEnglish = "Corrupted dummy text",
        textTransliteration = "Bismillah",
        audioUrl = ""
      )
    )

    assertEquals(2, quranDao.getAyahsCount())

    // Run deleteCorruptedAyahs
    quranDao.deleteCorruptedAyahs()

    // 1:1 should still be present, while 2:6 should be purged
    val ayah1 = quranDao.getAyahByIdSync(1)
    val ayah13 = quranDao.getAyahByIdSync(13)
    assertNotNull(ayah1)
    assertTrue("Corrupt ayah at 2:6 should be purged", ayah13 == null)
  }

  @Test
  fun `test multi-verse distinct text without duplicate bismillah`() = runBlocking {
    val quranDao = database.quranDao()
    quranDao.insertAyahs(QuranSeedData.initialAyahs)

    // Range of 5 verses in Al-Baqarah (2:1 to 2:5)
    val verses = quranDao.getAyahsRangeSync(8, 12)
    assertEquals(5, verses.size)

    // Ensure all 5 verses have distinct Arabic text and none are empty
    val distinctTexts = verses.map { it.textArabic.trim() }.distinct()
    assertEquals("All 5 verses must have unique authentic text", 5, distinctTexts.size)
  }
}
