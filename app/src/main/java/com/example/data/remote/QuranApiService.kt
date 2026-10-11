package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class QuranApiResponse(
  @Json(name = "code") val code: Int,
  @Json(name = "status") val status: String,
  @Json(name = "data") val data: List<SurahEditionData>?
)

data class SurahEditionData(
  @Json(name = "number") val number: Int,
  @Json(name = "name") val name: String,
  @Json(name = "englishName") val englishName: String,
  @Json(name = "numberOfAyahs") val numberOfAyahs: Int,
  @Json(name = "ayahs") val ayahs: List<RemoteAyahItem>
)

data class RemoteAyahItem(
  @Json(name = "number") val number: Int, // Global ID (1..6236)
  @Json(name = "text") val text: String,
  @Json(name = "numberInSurah") val numberInSurah: Int,
  @Json(name = "juz") val juz: Int
)

interface QuranApiService {
  @GET("surah/{surahNumber}/editions/quran-uthmani,en.sahih")
  suspend fun getSurahEditions(
    @Path("surahNumber") surahNumber: Int
  ): Response<QuranApiResponse>

  companion object {
    private const val BASE_URL = "https://api.alquran.cloud/v1/"

    fun create(): QuranApiService {
      val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

      val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

      return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(QuranApiService::class.java)
    }
  }
}
