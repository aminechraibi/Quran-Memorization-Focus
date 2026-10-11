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
      selectedSurahAyahs = QuranSeedData.initialAyahs.filter { it.surahNumber == 1 }
    )
  )
  val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

  val audioState: StateFlow<AudioPlayerState> = audioPlayer.playerState

  init {
    viewModelScope.launch {
      repository.settingsFlow.collect { settings ->
        val safeSettings = settings ?: UserSettingsEntity()
        val currAyah = repository.getAyahByIdSync(safeSettings.currentAyahId)
          ?: QuranSeedData.initialAyahs.firstOrNull { it.id == safeSettings.currentAyahId }
          ?: QuranSeedData.initialAyahs.first()

        _uiState.update { it.copy(settings = safeSettings, currentAyah = currAyah) }
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
      val nextAyah = repository.getAyahByIdSync(nextId)
      _uiState.update { it.copy(currentAyah = nextAyah) }
      audioPlayer.pause()
      onDismiss()
    }
  }

  fun onRepeatClicked(onDismiss: () -> Unit) {
    viewModelScope.launch {
      val state = uiState.value
      val currentId = state.settings.currentAyahId
      repository.repeatAyah(currentId)
      val updatedAyah = repository.getAyahByIdSync(currentId)
      _uiState.update { it.copy(currentAyah = updatedAyah) }
      audioPlayer.pause()
      onDismiss()
    }
  }

  fun nextAyah() {
    viewModelScope.launch {
      val state = uiState.value
      val nextId = (state.settings.currentAyahId + 1).coerceAtMost(6236)
      repository.setCurrentAyah(nextId)
      val ayah = repository.getAyahByIdSync(nextId)
      _uiState.update { it.copy(currentAyah = ayah) }
      audioPlayer.pause()
    }
  }

  fun previousAyah() {
    viewModelScope.launch {
      val state = uiState.value
      val prevId = (state.settings.currentAyahId - 1).coerceAtLeast(1)
      repository.setCurrentAyah(prevId)
      val ayah = repository.getAyahByIdSync(prevId)
      _uiState.update { it.copy(currentAyah = ayah) }
      audioPlayer.pause()
    }
  }

  fun selectAyahToMemorize(ayahId: Int) {
    viewModelScope.launch {
      repository.setCurrentAyah(ayahId)
      val ayah = repository.getAyahByIdSync(ayahId) ?: repository.getAyahOrFallback(ayahId)
      _uiState.update {
        it.copy(
          currentAyah = ayah,
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
      val ayah = repository.getAyahByIdSync(globalId) ?: repository.getAyahOrFallback(globalId)
      _uiState.update {
        it.copy(
          currentAyah = ayah,
          userMessage = "Selected Surah ${surahInfo.nameEnglish} (${surahInfo.nameArabic}) : Ayah $clampedAyah"
        )
      }
      if (ayah?.textArabic == "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ" && clampedAyah > 1) {
        downloadSurah(surahNumber)
      }
    }
  }

  fun toggleAudioPlayback(ayah: AyahEntity? = null) {
    val targetAyah = ayah ?: uiState.value.currentAyah ?: return
    val reciter = uiState.value.settings.selectedReciter
    audioPlayer.togglePlayPause(targetAyah.surahNumber, targetAyah.ayahNumberInSurah, reciter)
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
