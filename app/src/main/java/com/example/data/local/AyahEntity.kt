package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ayahs")
data class AyahEntity(
  @PrimaryKey val id: Int,
  val surahNumber: Int,
  val surahNameArabic: String,
  val surahNameEnglish: String,
  val ayahNumberInSurah: Int,
  val juzNumber: Int,
  val textArabic: String,
  val textEnglish: String,
  val textTransliteration: String,
  val audioUrl: String,
  val isCompleted: Boolean = false,
  val repetitionCount: Int = 0,
  val isCached: Boolean = true,
  val lastReviewedTimestamp: Long = 0L
)
