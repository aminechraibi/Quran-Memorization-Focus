package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.data.local.QuranSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AudioPlayerState(
  val isPlaying: Boolean = false,
  val isLoading: Boolean = false,
  val currentPositionMs: Long = 0L,
  val durationMs: Long = 0L,
  val playbackSpeed: Float = 1.0f,
  val activeSurah: Int = 0,
  val activeAyah: Int = 0,
  val errorMessage: String? = null
)

class QuranAudioPlayer(
  private val context: Context,
  private val cacheManager: AudioCacheManager
) {

  private var mediaPlayer: MediaPlayer? = null
  private val scope = CoroutineScope(Dispatchers.Main + Job())
  private var progressTrackerJob: Job? = null

  private val _playerState = MutableStateFlow(AudioPlayerState())
  val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

  fun playAyah(surah: Int, ayah: Int, reciter: String = "Husary_128kbps", directUrl: String? = null) {
    try {
      release()
      _playerState.update {
        it.copy(
          isLoading = true,
          errorMessage = null,
          activeSurah = surah,
          activeAyah = ayah
        )
      }

      val cachedFile = cacheManager.getCachedAudioFile(surah, ayah, reciter)
      val audioSourceUri = if (cachedFile != null) {
        Uri.fromFile(cachedFile)
      } else {
        val url = directUrl ?: QuranSeedData.formatAudioUrl(surah, ayah, reciter)
        Uri.parse(url)
      }

      mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
          AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        )
        setDataSource(context, audioSourceUri)
        setOnPreparedListener { mp ->
          _playerState.update {
            it.copy(
              isLoading = false,
              isPlaying = true,
              durationMs = mp.duration.toLong(),
              currentPositionMs = 0L
            )
          }
          mp.start()
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
              val currentSpeed = _playerState.value.playbackSpeed
              if (currentSpeed != 1.0f) {
                val params = mp.playbackParams
                params.speed = currentSpeed
                mp.playbackParams = params
              }
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }
          startProgressTracking()
        }
        setOnCompletionListener {
          _playerState.update { it.copy(isPlaying = false, currentPositionMs = it.durationMs) }
          stopProgressTracking()
        }
        setOnErrorListener { _, what, extra ->
          _playerState.update {
            it.copy(
              isLoading = false,
              isPlaying = false,
              errorMessage = "Audio playback error ($what, $extra). Please check your internet connection."
            )
          }
          stopProgressTracking()
          true
        }
        prepareAsync()
      }
    } catch (e: Exception) {
      _playerState.update {
        it.copy(
          isLoading = false,
          isPlaying = false,
          errorMessage = "Could not initialize audio: ${e.localizedMessage}"
        )
      }
    }
  }

  fun pause() {
    mediaPlayer?.let {
      if (it.isPlaying) {
        it.pause()
        _playerState.update { s -> s.copy(isPlaying = false) }
        stopProgressTracking()
      }
    }
  }

  fun resume() {
    mediaPlayer?.let {
      if (!it.isPlaying) {
        it.start()
        _playerState.update { s -> s.copy(isPlaying = true) }
        startProgressTracking()
      }
    }
  }

  fun togglePlayPause(surah: Int, ayah: Int, reciter: String) {
    val state = _playerState.value
    if (state.activeSurah == surah && state.activeAyah == ayah && mediaPlayer != null) {
      if (state.isPlaying) {
        pause()
      } else {
        resume()
      }
    } else {
      playAyah(surah, ayah, reciter)
    }
  }

  fun seekTo(positionMs: Long) {
    mediaPlayer?.let {
      it.seekTo(positionMs.toInt())
      _playerState.update { s -> s.copy(currentPositionMs = positionMs) }
    }
  }

  fun setPlaybackSpeed(speed: Float) {
    _playerState.update { it.copy(playbackSpeed = speed) }
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      try {
        mediaPlayer?.let { mp ->
          val params = mp.playbackParams
          params.speed = speed
          mp.playbackParams = params
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun cyclePlaybackSpeed(): Float {
    val speeds = listOf(1.0f, 1.25f, 1.5f, 2.0f, 3.0f)
    val current = _playerState.value.playbackSpeed
    val next = when (current) {
      1.0f -> 1.25f
      1.25f -> 1.5f
      1.5f -> 2.0f
      2.0f -> 3.0f
      else -> 1.0f
    }
    setPlaybackSpeed(next)
    return next
  }

  fun replay() {
    val state = _playerState.value
    if (mediaPlayer != null) {
      seekTo(0)
      mediaPlayer?.start()
      _playerState.update { it.copy(isPlaying = true) }
      startProgressTracking()
    } else if (state.activeSurah > 0 && state.activeAyah > 0) {
      playAyah(state.activeSurah, state.activeAyah)
    }
  }

  private fun startProgressTracking() {
    stopProgressTracking()
    progressTrackerJob = scope.launch {
      while (isActive) {
        mediaPlayer?.let { mp ->
          if (mp.isPlaying) {
            val current = mp.currentPosition.toLong()
            val dur = mp.duration.toLong().coerceAtLeast(1L)
            _playerState.update { it.copy(currentPositionMs = current, durationMs = dur) }
          }
        }
        delay(250)
      }
    }
  }

  private fun stopProgressTracking() {
    progressTrackerJob?.cancel()
    progressTrackerJob = null
  }

  fun release() {
    stopProgressTracking()
    try {
      mediaPlayer?.stop()
      mediaPlayer?.release()
    } catch (_: Exception) {
    } finally {
      mediaPlayer = null
      _playerState.update { it.copy(isPlaying = false, isLoading = false) }
    }
  }
}
