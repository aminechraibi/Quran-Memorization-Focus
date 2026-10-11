package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {

  @Query("SELECT * FROM ayahs WHERE id = :id")
  fun getAyahById(id: Int): Flow<AyahEntity?>

  @Query("SELECT * FROM ayahs WHERE id = :id")
  suspend fun getAyahByIdSync(id: Int): AyahEntity?

  @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumberInSurah ASC")
  fun getAyahsBySurah(surahNumber: Int): Flow<List<AyahEntity>>

  @Query("SELECT * FROM ayahs WHERE id BETWEEN :startId AND :endId ORDER BY id ASC")
  fun getAyahsRange(startId: Int, endId: Int): Flow<List<AyahEntity>>

  @Query("SELECT * FROM ayahs WHERE id BETWEEN :startId AND :endId ORDER BY id ASC")
  suspend fun getAyahsRangeSync(startId: Int, endId: Int): List<AyahEntity>

  @Query("SELECT * FROM ayahs ORDER BY id ASC")
  fun getAllAyahs(): Flow<List<AyahEntity>>

  @Query("SELECT COUNT(*) FROM ayahs")
  suspend fun getAyahsCount(): Int

  @Query("SELECT COUNT(*) FROM ayahs WHERE isCompleted = 1")
  fun getCompletedAyahsCount(): Flow<Int>

  @Query("SELECT COALESCE(SUM(repetitionCount), 0) FROM ayahs")
  fun getTotalRepetitionsCount(): Flow<Int>

  @Query("SELECT * FROM ayahs WHERE isCompleted = 1 ORDER BY lastReviewedTimestamp DESC")
  fun getCompletedAyahs(): Flow<List<AyahEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAyahs(ayahs: List<AyahEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAyah(ayah: AyahEntity)

  @Update
  suspend fun updateAyah(ayah: AyahEntity)

  @Query("UPDATE ayahs SET isCompleted = :completed, lastReviewedTimestamp = :timestamp WHERE id = :id")
  suspend fun setAyahCompleted(id: Int, completed: Boolean, timestamp: Long = System.currentTimeMillis())

  @Query("UPDATE ayahs SET repetitionCount = repetitionCount + 1, lastReviewedTimestamp = :timestamp WHERE id = :id")
  suspend fun incrementRepetition(id: Int, timestamp: Long = System.currentTimeMillis())

  @Query("UPDATE ayahs SET repetitionCount = :count, lastReviewedTimestamp = :timestamp WHERE id = :id")
  suspend fun setAyahRepetitionCount(id: Int, count: Int, timestamp: Long = System.currentTimeMillis())

  @Query("UPDATE ayahs SET isCompleted = 0, repetitionCount = 0, lastReviewedTimestamp = 0")
  suspend fun resetAllProgress()
}
