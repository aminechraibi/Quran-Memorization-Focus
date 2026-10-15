package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [AyahEntity::class, UserSettingsEntity::class], version = 2, exportSchema = false)
abstract class QuranDatabase : RoomDatabase() {

  abstract fun quranDao(): QuranDao
  abstract fun settingsDao(): SettingsDao

  companion object {
    @Volatile
    private var INSTANCE: QuranDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope? = null): QuranDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          QuranDatabase::class.java,
          "quran_memorizer_db"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              scope?.launch(Dispatchers.IO) {
                val database = getDatabase(context)
                database.quranDao().insertAyahs(QuranSeedData.initialAyahs)
                database.settingsDao().saveSettings(UserSettingsEntity())
              }
            }
          })
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
