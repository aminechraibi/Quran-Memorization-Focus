package com.example.audio

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class AudioCacheManager(private val context: Context) {

  private val audioCacheDir: File by lazy {
    File(context.cacheDir, "quran_audio").apply {
      if (!exists()) mkdirs()
    }
  }

  private val client = OkHttpClient()

  private fun getFileNameForAyah(surah: Int, ayah: Int, reciter: String): String {
    return "${reciter}_${String.format("%03d", surah)}_${String.format("%03d", ayah)}.mp3"
  }

  fun getCachedAudioFile(surah: Int, ayah: Int, reciter: String): File? {
    val file = File(audioCacheDir, getFileNameForAyah(surah, ayah, reciter))
    return if (file.exists() && file.length() > 0) file else null
  }

  fun isAudioCached(surah: Int, ayah: Int, reciter: String): Boolean {
    return getCachedAudioFile(surah, ayah, reciter) != null
  }

  suspend fun cacheAudioForAyah(surah: Int, ayah: Int, reciter: String, url: String): Boolean =
    withContext(Dispatchers.IO) {
      try {
        val targetFile = File(audioCacheDir, getFileNameForAyah(surah, ayah, reciter))
        if (targetFile.exists() && targetFile.length() > 0) {
          return@withContext true
        }

        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return@withContext false

        response.body?.byteStream()?.use { input ->
          FileOutputStream(targetFile).use { output ->
            input.copyTo(output)
          }
        }
        true
      } catch (e: Exception) {
        e.printStackTrace()
        false
      }
    }

  fun getCacheSizeBytes(): Long {
    var size = 0L
    audioCacheDir.listFiles()?.forEach { file ->
      size += file.length()
    }
    return size
  }

  fun getCacheSizeFormatted(): String {
    val bytes = getCacheSizeBytes()
    val mb = bytes / (1024.0 * 1024.0)
    return String.format("%.2f MB", mb)
  }

  fun getCachedFilesCount(): Int {
    return audioCacheDir.listFiles()?.size ?: 0
  }

  fun clearAudioCache(): Boolean {
    return try {
      audioCacheDir.listFiles()?.forEach { it.delete() }
      true
    } catch (e: Exception) {
      false
    }
  }
}
