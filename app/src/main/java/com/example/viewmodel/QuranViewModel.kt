package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.QuranApplication
import com.example.audio.AudioPlayerState
import com.example.audio.QuranAudioPlayer
import com.example.data.local.AyahEntity
import com.example.data.local.QuranSeedData
import com.example.data.local.UserSettingsEntity
import com.example.data.model.QuranSurahDirectory
import com.example.data.repository.QuranRepository
import com.example.service.UnlockMonitorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuranUiState(
  val currentAyah: AyahEntity? = null,
  val currentAyahs: List<AyahEntity> = emptyList(),
  val settings: UserSettingsEntity = UserSettingsEntity(),
  val completedCount: Int = 0,
  val totalRepetitions: Int = 0,
  val selectedSurahAyahs: List<AyahEntity> = emptyList(),
  val selectedSurahNumber: Int = 1,
  val audioState: AudioPlayerState = AudioPlayerState(),
  val cacheSizeFormatted: String = "0.00 MB",
  val cachedAyahsCount: Int = 0,
  val isSurahDownloading: Boolean = false,
  val userMessage: String? = null
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

  private val app = application as QuranApplication
  private val repository: QuranRepository = app.repository
  private val audioPlayer: QuranAudioPlayer = app.audioPlayer
  private val audioCacheManager = app.audioCacheManager

  private val _uiState = MutableStateFlow(
    QuranUiState(
      currentAyah = QuranSeedData.initialAyahs.first(),
      currentAyahs = listOf(QuranSeedData.initialAyahs.first()),
      selectedSurahAyahs = QuranSeedData.initialAyahs.filter { it.surahNumber == 1 }
    )
  )
  val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

  val audioState: StateFlow<AudioPlayerState> = audioPlayer.playerState

  init {
    viewModelScope.launch {
      repository.settingsFlow.collect { settings ->
        val safeSettings = settings ?: UserSettingsEntity()
        val count = safeSettings.versesCount.coerceIn(1, 10)
        val ayahsList = repository.getAyahsBlock(safeSettings.currentAyahId, count)
        val currAyah = ayahsList.firstOrNull()
          ?: repository.getAyahByIdSync(safeSettings.currentAyahId)
          ?: QuranSeedData.initialAyahs.first()

        _uiState.update {
          it.copy(
            settings = safeSettings,
            currentAyah = currAyah,
            currentAyahs = ayahsList
          )
        }
      }
    }

    viewModelScope.launch {
      repository.completedAyahsCount.collect { completed ->
        _uiState.update { it.copy(completedCount = completed) }
      }
    }

    viewModelScope.launch {
      repository.totalRepetitionsCount.collect { repetitions ->
        _uiState.update { it.copy(totalRepetitions = repetitions) }
      }
    }

    viewModelScope.launch {
      audioPlayer.playerState.collect { aState ->
        _uiState.update { it.copy(audioState = aState) }
      }
    }

    loadSelectedSurah(1)
    refreshCacheInfo()
  }

  fun loadSelectedSurah(surahNumber: Int) {
    _uiState.update { it.copy(selectedSurahNumber = surahNumber) }
    viewModelScope.launch {
      repository.getAyahsBySurah(surahNumber).collect { ayahs ->
        _uiState.update { it.copy(selectedSurahAyahs = ayahs) }
      }
    }
  }

  fun onDoneClicked(onDismiss: () -> Unit) {
    viewModelScope.launch {
      val state = uiState.value
      val currentId = state.settings.currentAyahId
      val nextId = repository.markDoneAndAdvance(currentId)
      val count = state.settings.versesCount.coerceIn(1, 10)
      val nextAyahs = repository.getAyahsBlock(nextId, count)
      _uiState.update {
        it.copy(
          currentAyah = nextAyahs.firstOrNull(),
          currentAyahs = nextAyahs
        )
      }
      audioPlayer.pause()
      onDismiss()
    }
  }

  fun onRepeatClicked(onDismiss: () -> Unit) {
    viewModelScope.launch {
      val state = uiState.value
      val ayahs = if (state.currentAyahs.isNotEmpty()) state.currentAyahs else listOfNotNull(state.currentAyah)
      for (a in ayahs) {
        repository.repeatAyah(a.id)
      }
      val count = state.settings.versesCount.coerceIn(1, 10)
      val updatedAyahs = repository.getAyahsBlock(state.settings.currentAyahId, count)
      _uiState.update {
        it.copy(
          currentAyah = updatedAyahs.firstOrNull(),
          currentAyahs = updatedAyahs
        )
      }
      audioPlayer.pause()
      onDismiss()
    }
  }

  fun nextAyah() {
    viewModelScope.launch {
      val state = uiState.value
      val jump = state.settings.verseJumpStep.coerceAtLeast(1)
      val nextId = (state.settings.currentAyahId + jump).coerceAtMost(6236)
      repository.setCurrentAyah(nextId)
      val count = state.settings.versesCount.coerceIn(1, 10)
      val ayahs = repository.getAyahsBlock(nextId, count)
      _uiState.update {
        it.copy(
          currentAyah = ayahs.firstOrNull(),
          currentAyahs = ayahs
        )
      }
      audioPlayer.pause()
    }
  }

  fun previousAyah() {
    viewModelScope.launch {
      val state = uiState.value
      val jump = state.settings.verseJumpStep.coerceAtLeast(1)
      val prevId = (state.settings.currentAyahId - jump).coerceAtLeast(1)
      repository.setCurrentAyah(prevId)
      val count = state.settings.versesCount.coerceIn(1, 10)
      val ayahs = repository.getAyahsBlock(prevId, count)
      _uiState.update {
        it.copy(
          currentAyah = ayahs.firstOrNull(),
          currentAyahs = ayahs
        )
      }
      audioPlayer.pause()
    }
  }

  fun selectAyahToMemorize(ayahId: Int) {
    viewModelScope.launch {
      repository.setCurrentAyah(ayahId)
      val count = uiState.value.settings.versesCount.coerceIn(1, 10)
      val ayahs = repository.getAyahsBlock(ayahId, count)
      val ayah = ayahs.firstOrNull() ?: repository.getAyahOrFallback(ayahId)
      _uiState.update {
        it.copy(
          currentAyah = ayah,
          currentAyahs = ayahs,
          userMessage = "Active Ayah set to Ayah $ayahId"
        )
      }
    }
  }

  fun selectSurahAndAyah(surahNumber: Int, ayahNumberInSurah: Int) {
    val surahInfo = QuranSurahDirectory.getSurahByNumber(surahNumber) ?: return
    val clampedAyah = ayahNumberInSurah.coerceIn(1, surahInfo.numberOfAyahs)
    val globalId = QuranSurahDirectory.getGlobalAyahId(surahNumber, clampedAyah)
    viewModelScope.launch {
      repository.setCurrentAyah(globalId)
      val count = uiState.value.settings.versesCount.coerceIn(1, 10)
      val ayahs = repository.getAyahsBlock(globalId, count)
      val ayah = ayahs.firstOrNull() ?: repository.getAyahOrFallback(globalId)
      _uiState.update {
        it.copy(
          currentAyah = ayah,
          currentAyahs = ayahs,
          userMessage = "Selected Surah ${surahInfo.nameEnglish} (${surahInfo.nameArabic}) : Ayah $clampedAyah"
        )
      }
      if (ayah?.textArabic == "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ" && clampedAyah > 1) {
        downloadSurah(surahNumber)
      }
    }
  }

  fun toggleAudioPlayback(ayah: AyahEntity? = null) {
    val reciter = uiState.value.settings.selectedReciter
    val currentAyahs = uiState.value.currentAyahs
    if (ayah == null && currentAyahs.size > 1) {
      audioPlayer.togglePlaylist(currentAyahs, reciter)
    } else {
      val targetAyah = ayah ?: uiState.value.currentAyah ?: return
      audioPlayer.togglePlayPause(targetAyah.surahNumber, targetAyah.ayahNumberInSurah, reciter)
    }
  }

  fun replayAudio() {
    audioPlayer.replay()
  }

  fun seekAudio(positionMs: Long) {
    audioPlayer.seekTo(positionMs)
  }

  fun cycleAudioSpeed() {
    audioPlayer.cyclePlaybackSpeed()
  }

  fun setAudioSpeed(speed: Float) {
    audioPlayer.setPlaybackSpeed(speed)
  }

  fun updateCurrentAyahRepetition(count: Int) {
    viewModelScope.launch {
      val state = uiState.value
      val currentId = state.settings.currentAyahId
      repository.setAyahRepetitionCount(currentId, count)
      val updatedAyah = repository.getAyahByIdSync(currentId)
      _uiState.update { it.copy(currentAyah = updatedAyah) }
    }
  }

  fun incrementCurrentAyahRepetition() {
    viewModelScope.launch {
      val currentAyah = uiState.value.currentAyah ?: return@launch
      val newCount = currentAyah.repetitionCount + 1
      updateCurrentAyahRepetition(newCount)
    }
  }

  fun decrementCurrentAyahRepetition() {
    viewModelScope.launch {
      val currentAyah = uiState.value.currentAyah ?: return@launch
      val newCount = (currentAyah.repetitionCount - 1).coerceAtLeast(0)
      updateCurrentAyahRepetition(newCount)
    }
  }

  fun setUnlockEnabled(enabled: Boolean) {
    viewModelScope.launch {
      repository.setUnlockDisplayEnabled(enabled)
      if (enabled) {
        UnlockMonitorService.startService(app)
      } else {
        UnlockMonitorService.stopService(app)
      }
    }
  }

  fun setCooldownMinutes(minutes: Int) {
    viewModelScope.launch {
      repository.setCooldownMinutes(minutes)
    }
  }

  fun setSelectedReciter(reciter: String) {
    viewModelScope.launch {
      repository.setSelectedReciter(reciter)
      audioPlayer.release()
      refreshCacheInfo()
    }
  }

  fun setArabicFontSize(sizeSp: Int) {
    viewModelScope.launch {
      repository.setArabicFontSize(sizeSp)
    }
  }

  fun setShowTranslation(show: Boolean) {
    viewModelScope.launch {
      repository.setShowTranslation(show)
    }
  }

  fun setShowTransliteration(show: Boolean) {
    viewModelScope.launch {
      repository.setShowTransliteration(show)
    }
  }

  fun setAutoPlay(autoPlay: Boolean) {
    viewModelScope.launch {
      repository.setAutoPlayAudio(autoPlay)
    }
  }

  fun setRepeatCountGoal(goal: Int) {
    viewModelScope.launch {
      repository.setRepeatCountGoal(goal)
    }
  }

  fun setVersesCount(count: Int) {
    viewModelScope.launch {
      val clamped = count.coerceIn(1, 10)
      repository.setVersesCount(clamped)
      val currentId = uiState.value.settings.currentAyahId
      val ayahs = repository.getAyahsBlock(currentId, clamped)
      _uiState.update {
        it.copy(
          currentAyahs = ayahs,
          currentAyah = ayahs.firstOrNull(),
          userMessage = "Displaying $clamped ${if (clamped == 1) "verse" else "verses"} at once"
        )
      }
    }
  }

  fun setVerseJumpStep(step: Int) {
    viewModelScope.launch {
      val maxStep = uiState.value.settings.versesCount.coerceIn(1, 10)
      val clamped = step.coerceIn(1, maxStep)
      repository.setVerseJumpStep(clamped)
      _uiState.update {
        it.copy(userMessage = "Jump progression step set to $clamped ${if (clamped == 1) "verse" else "verses"}")
      }
    }
  }

  fun resetAllProgress() {
    viewModelScope.launch {
      repository.resetAllProgress()
      val resetAyah = repository.getAyahByIdSync(1)
      _uiState.update {
        it.copy(
          currentAyah = resetAyah,
          userMessage = "Memorization progress has been reset"
        )
      }
    }
  }

  fun clearUserMessage() {
    _uiState.update { it.copy(userMessage = null) }
  }

  fun refreshCacheInfo() {
    _uiState.update {
      it.copy(
        cacheSizeFormatted = audioCacheManager.getCacheSizeFormatted(),
        cachedAyahsCount = audioCacheManager.getCachedFilesCount()
      )
    }
  }

  fun clearAudioCache() {
    audioCacheManager.clearAudioCache()
    refreshCacheInfo()
    _uiState.update { it.copy(userMessage = "Audio cache cleared") }
  }

  fun downloadSurah(surahNumber: Int) {
    viewModelScope.launch {
      _uiState.update { it.copy(isSurahDownloading = true) }
      val success = repository.downloadSurahOnline(surahNumber)
      _uiState.update { it.copy(isSurahDownloading = false) }
      if (success) {
        loadSelectedSurah(surahNumber)
        _uiState.update {
          it.copy(userMessage = "Surah ${QuranSurahDirectory.getSurahByNumber(surahNumber)?.nameEnglish} downloaded for offline use")
        }
      } else {
        _uiState.update {
          it.copy(userMessage = "Could not download Surah. Please check internet connection.")
        }
      }
      refreshCacheInfo()
    }
  }

  override fun onCleared() {
    super.onCleared()
    audioPlayer.release()
  }
}
