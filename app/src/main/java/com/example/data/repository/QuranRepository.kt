package com.example.data.repository

import android.content.Context
import com.example.audio.AudioCacheManager
import com.example.data.local.AyahEntity
import com.example.data.local.QuranDao
import com.example.data.local.QuranDatabase
import com.example.data.local.QuranSeedData
import com.example.data.local.SettingsDao
import com.example.data.local.UserSettingsEntity
import com.example.data.model.QuranSurahDirectory
import com.example.data.remote.QuranApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuranRepository(
  private val quranDao: QuranDao,
  private val settingsDao: SettingsDao,
  private val audioCacheManager: AudioCacheManager,
  private val apiService: QuranApiService = QuranApiService.create()
) {

  val settingsFlow: Flow<UserSettingsEntity?> = settingsDao.getSettings()
  val completedAyahsCount: Flow<Int> = quranDao.getCompletedAyahsCount()
  val totalRepetitionsCount: Flow<Int> = quranDao.getTotalRepetitionsCount()
  val allAyahs: Flow<List<AyahEntity>> = quranDao.getAllAyahs()

  suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
    try {
      // 1. Purge any corrupted dummy bismillah records that were mistakenly inserted previously
      quranDao.deleteCorruptedAyahs()
    } catch (e: Exception) {
      e.printStackTrace()
    }

    val count = quranDao.getAyahsCount()
    if (count < QuranSeedData.initialAyahs.size) {
      quranDao.insertAyahs(QuranSeedData.initialAyahs)
    }
    val settings = settingsDao.getSettingsSync()
    if (settings == null) {
      settingsDao.saveSettings(UserSettingsEntity())
    }

    // Prefetch Surah 1 & 2 in background so user has full text immediately
    val currentAyah = settings?.currentAyahId ?: 1
    val currentSurah = QuranSurahDirectory.surahs.lastOrNull { it.startAyahGlobalId <= currentAyah }?.number ?: 1
    prefetchSurahs(setOf(1, 2, currentSurah))
  }

  fun prefetchSurahs(surahNumbers: Set<Int>) {
    CoroutineScope(Dispatchers.IO).launch {
      for (num in surahNumbers) {
        val count = quranDao.getAyahsCountForSurah(num)
        val info = QuranSurahDirectory.getSurahByNumber(num)
        if (info != null && count < info.numberOfAyahs) {
          downloadSurahOnline(num)
        }
      }
    }
  }

  fun getAyahById(id: Int): Flow<AyahEntity?> = quranDao.getAyahById(id)

  suspend fun getAyahByIdSync(id: Int): AyahEntity? = quranDao.getAyahByIdSync(id)

  fun getAyahsBySurah(surahNumber: Int): Flow<List<AyahEntity>> =
    quranDao.getAyahsBySurah(surahNumber)

  fun getAyahsRange(startId: Int, endId: Int): Flow<List<AyahEntity>> =
    quranDao.getAyahsRange(startId, endId)

  suspend fun markDoneAndAdvance(currentAyahId: Int): Int = withContext(Dispatchers.IO) {
    val settings = settingsDao.getSettingsSync()
    val jumpStep = settings?.verseJumpStep?.coerceAtLeast(1) ?: 1
    val versesCount = settings?.versesCount?.coerceIn(1, 10) ?: 1

    // Mark current group of verses as completed
    for (i in 0 until versesCount) {
      val aId = (currentAyahId + i).coerceAtMost(6236)
      quranDao.setAyahCompleted(aId, true)
    }

    // Calculate next starting ayah advancing by jumpStep
    val nextAyahId = (currentAyahId + jumpStep).coerceAtMost(6236)
    settingsDao.updateCurrentAyahId(nextAyahId)

    // Automatically trigger cache for next window
    cacheSurroundingAyahs(nextAyahId)

    nextAyahId
  }

  suspend fun repeatAyah(currentAyahId: Int) = withContext(Dispatchers.IO) {
    quranDao.incrementRepetition(currentAyahId)
  }

  suspend fun setAyahRepetitionCount(ayahId: Int, count: Int) = withContext(Dispatchers.IO) {
    quranDao.setAyahRepetitionCount(ayahId, count.coerceAtLeast(0))
  }

  suspend fun setCurrentAyah(ayahId: Int) = withContext(Dispatchers.IO) {
    val ayah = getAyahOrFallback(ayahId)
    if (ayah != null) {
      settingsDao.updateCurrentAyahId(ayahId)
      cacheSurroundingAyahs(ayahId)
    }
  }

  suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
    quranDao.resetAllProgress()
    settingsDao.updateCurrentAyahId(1)
  }

  suspend fun updateSettings(settings: UserSettingsEntity) = withContext(Dispatchers.IO) {
    settingsDao.saveSettings(settings)
  }

  suspend fun setUnlockDisplayEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
    settingsDao.setUnlockDisplayEnabled(enabled)
  }

  suspend fun setCooldownMinutes(minutes: Int) = withContext(Dispatchers.IO) {
    settingsDao.setCooldownMinutes(minutes)
  }

  suspend fun setSelectedReciter(reciter: String) = withContext(Dispatchers.IO) {
    settingsDao.setSelectedReciter(reciter)
  }

  suspend fun setAutoPlayAudio(autoPlay: Boolean) = withContext(Dispatchers.IO) {
    settingsDao.setAutoPlayAudio(autoPlay)
  }

  suspend fun setArabicFontSize(fontSize: Int) = withContext(Dispatchers.IO) {
    settingsDao.setArabicFontSize(fontSize)
  }

  suspend fun setShowTranslation(show: Boolean) = withContext(Dispatchers.IO) {
    settingsDao.setShowTranslation(show)
  }

  suspend fun setShowTransliteration(show: Boolean) = withContext(Dispatchers.IO) {
    settingsDao.setShowTransliteration(show)
  }

  suspend fun setRepeatCountGoal(goal: Int) = withContext(Dispatchers.IO) {
    settingsDao.setRepeatCountGoal(goal)
  }

  suspend fun setVersesCount(count: Int) = withContext(Dispatchers.IO) {
    val clampedCount = count.coerceIn(1, 10)
    settingsDao.setVersesCount(clampedCount)
    val settings = settingsDao.getSettingsSync()
    if (settings != null && settings.verseJumpStep > clampedCount) {
      settingsDao.setVerseJumpStep(clampedCount)
    }
  }

  suspend fun setVerseJumpStep(step: Int) = withContext(Dispatchers.IO) {
    val settings = settingsDao.getSettingsSync()
    val maxStep = settings?.versesCount?.coerceIn(1, 10) ?: 10
    val clampedStep = step.coerceIn(1, maxStep)
    settingsDao.setVerseJumpStep(clampedStep)
  }

  private fun isValidAyah(ayah: AyahEntity): Boolean {
    // If it has corrupted dummy placeholder text that isn't true Bismillah
    if (ayah.textArabic.trim() == "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ") {
      return (ayah.surahNumber == 1 && ayah.ayahNumberInSurah == 1) ||
             (ayah.surahNumber == 27 && ayah.ayahNumberInSurah == 30)
    }
    return ayah.textArabic.isNotBlank()
  }

  suspend fun getAyahsBlock(startAyahId: Int, count: Int): List<AyahEntity> = withContext(Dispatchers.IO) {
    val safeCount = count.coerceIn(1, 10)
    val neededIds = (0 until safeCount).map { (startAyahId + it).coerceAtMost(6236) }.distinct()

    // Query existing ayahs from database
    val existing = quranDao.getAyahsRangeSync(neededIds.first(), neededIds.last())
      .filter { neededIds.contains(it.id) && isValidAyah(it) }

    // If any are missing or corrupted, identify the required surahs and download them online
    val missingIds = neededIds.filter { id -> existing.none { it.id == id } }
    if (missingIds.isNotEmpty()) {
      val surahsToFetch = missingIds.mapNotNull { id ->
        QuranSurahDirectory.surahs.lastOrNull { it.startAyahGlobalId <= id }?.number
      }.distinct()

      for (surahNum in surahsToFetch) {
        downloadSurahOnline(surahNum)
      }
    }

    // Re-query from database after fetch
    val updated = quranDao.getAyahsRangeSync(neededIds.first(), neededIds.last())
      .filter { neededIds.contains(it.id) && isValidAyah(it) }
      .sortedBy { it.id }

    if (updated.isNotEmpty()) {
      return@withContext updated
    }

    // Fallback if network was offline
    neededIds.mapNotNull { id ->
      QuranSeedData.initialAyahs.find { it.id == id } ?: getAyahOrFallback(id)
    }
  }

  suspend fun recordUnlockTriggered() = withContext(Dispatchers.IO) {
    settingsDao.updateLastUnlockTimestamp(System.currentTimeMillis())
  }

  suspend fun isCooldownActive(): Boolean = withContext(Dispatchers.IO) {
    val settings = settingsDao.getSettingsSync() ?: return@withContext false
    if (!settings.unlockDisplayEnabled) return@withContext true
    if (settings.cooldownMinutes <= 0) return@withContext false

    val lastTrigger = settings.lastUnlockTriggeredTimestamp
    val elapsed = System.currentTimeMillis() - lastTrigger
    val cooldownMillis = settings.cooldownMinutes * 60 * 1000L
    elapsed < cooldownMillis
  }

  private suspend fun calculateNextAyahId(currentId: Int): Int {
    val all = quranDao.getAllAyahs().firstOrNull() ?: emptyList()
    val sortedIds = all.map { it.id }.sorted()
    val nextInLocal = sortedIds.firstOrNull { it > currentId }
    return nextInLocal ?: sortedIds.firstOrNull() ?: 1
  }

  suspend fun getAyahOrFallback(id: Int): AyahEntity? = withContext(Dispatchers.IO) {
    val local = quranDao.getAyahByIdSync(id)
    if (local != null && isValidAyah(local)) {
      return@withContext local
    }

    val seed = QuranSeedData.initialAyahs.find { it.id == id }
    if (seed != null) {
      quranDao.insertAyah(seed)
      return@withContext seed
    }

    val surahInfo = QuranSurahDirectory.surahs.lastOrNull { it.startAyahGlobalId <= id }
      ?: QuranSurahDirectory.surahs.first()

    // Try downloading the surah
    val success = downloadSurahOnline(surahInfo.number)
    if (success) {
      val fetched = quranDao.getAyahByIdSync(id)
      if (fetched != null && isValidAyah(fetched)) {
        return@withContext fetched
      }
    }

    // Offline safe representation (do NOT pollute the database with duplicate dummy Bismillah)
    val offset = (id - surahInfo.startAyahGlobalId) + 1
    val reciter = settingsDao.getSettingsSync()?.selectedReciter ?: "Husary_128kbps"
    AyahEntity(
      id = id,
      surahNumber = surahInfo.number,
      surahNameArabic = surahInfo.nameArabic,
      surahNameEnglish = surahInfo.nameEnglish,
      ayahNumberInSurah = offset.coerceAtLeast(1),
      juzNumber = 1,
      textArabic = "﴿ سورة ${surahInfo.nameArabic} - آية $offset ﴾",
      textEnglish = "Ayah $offset of Surah ${surahInfo.nameEnglish}",
      textTransliteration = "Surah ${surahInfo.nameEnglish} : Ayah $offset",
      audioUrl = QuranSeedData.formatAudioUrl(surahInfo.number, offset.coerceAtLeast(1), reciter)
    )
  }

  /**
   * Permanently caches current Ayah and the previous/next 10 ayahs, including text and audio.
   */
  suspend fun cacheSurroundingAyahs(centerAyahId: Int) = withContext(Dispatchers.IO) {
    val startId = (centerAyahId - 10).coerceAtLeast(1)
    val endId = centerAyahId + 10
    val settings = settingsDao.getSettingsSync()
    val reciter = settings?.selectedReciter ?: "Husary_128kbps"
    val cacheAudio = settings?.offlineAudioCacheEnabled ?: true

    val ayahs = quranDao.getAyahsRangeSync(startId, endId)
    if (cacheAudio) {
      for (ayah in ayahs) {
        if (!audioCacheManager.isAudioCached(ayah.surahNumber, ayah.ayahNumberInSurah, reciter)) {
          audioCacheManager.cacheAudioForAyah(
            ayah.surahNumber,
            ayah.ayahNumberInSurah,
            reciter,
            ayah.audioUrl
          )
        }
      }
    }
  }

  suspend fun downloadSurahOnline(surahNumber: Int): Boolean = withContext(Dispatchers.IO) {
    try {
      val response = apiService.getSurahEditions(surahNumber)
      if (response.isSuccessful) {
        val data = response.body()?.data ?: return@withContext false
        val uthmaniEdition = data.find { it.ayahs.isNotEmpty() } ?: return@withContext false
        val englishEdition = data.getOrNull(1)

        val surahInfo = QuranSurahDirectory.getSurahByNumber(surahNumber)
        val entities = uthmaniEdition.ayahs.mapIndexed { index, remoteAyah ->
          val enText = englishEdition?.ayahs?.getOrNull(index)?.text ?: "Translation not available"
          AyahEntity(
            id = remoteAyah.number,
            surahNumber = surahNumber,
            surahNameArabic = surahInfo?.nameArabic ?: uthmaniEdition.name,
            surahNameEnglish = surahInfo?.nameEnglish ?: uthmaniEdition.englishName,
            ayahNumberInSurah = remoteAyah.numberInSurah,
            juzNumber = remoteAyah.juz,
            textArabic = remoteAyah.text,
            textEnglish = enText,
            textTransliteration = "Surah ${surahInfo?.nameEnglish} : Ayah ${remoteAyah.numberInSurah}",
            audioUrl = QuranSeedData.formatAudioUrl(surahNumber, remoteAyah.numberInSurah)
          )
        }
        quranDao.insertAyahs(entities)
        true
      } else {
        false
      }
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  companion object {
    @Volatile
    private var INSTANCE: QuranRepository? = null

    fun getInstance(context: Context): QuranRepository {
      return INSTANCE ?: synchronized(this) {
        val db = QuranDatabase.getDatabase(context)
        val cacheManager = AudioCacheManager(context)
        val instance = QuranRepository(db.quranDao(), db.settingsDao(), cacheManager)
        INSTANCE = instance
        instance
      }
    }
  }
}
