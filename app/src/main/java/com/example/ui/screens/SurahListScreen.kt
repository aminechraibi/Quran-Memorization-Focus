package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AyahEntity
import com.example.data.model.QuranSurahDirectory
import com.example.data.model.SurahInfo
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SuccessGreen

@Composable
fun SurahListScreen(
  selectedSurahNumber: Int,
  surahAyahs: List<AyahEntity>,
  currentAyahId: Int,
  isDownloading: Boolean,
  onSelectSurah: (Int) -> Unit,
  onSelectAyahToMemorize: (Int) -> Unit,
  onToggleAudioForAyah: (AyahEntity) -> Unit,
  onDownloadSurah: (Int) -> Unit,
  playingAyahNumber: Int?
) {
  var searchQuery by remember { mutableStateOf("") }
  var viewModeSurahNumber by remember { mutableStateOf<Int?>(null) }

  val filteredSurahs = remember(searchQuery) {
    if (searchQuery.isBlank()) {
      QuranSurahDirectory.surahs
    } else {
      QuranSurahDirectory.surahs.filter {
        it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
          it.nameArabic.contains(searchQuery) ||
          it.englishMeaning.contains(searchQuery, ignoreCase = true) ||
          it.number.toString() == searchQuery.trim()
      }
    }
  }

  if (viewModeSurahNumber != null) {
    val surahInfo = QuranSurahDirectory.getSurahByNumber(viewModeSurahNumber!!)
    SurahDetailView(
      surah = surahInfo,
      ayahs = surahAyahs,
      currentAyahId = currentAyahId,
      isDownloading = isDownloading,
      onBack = { viewModeSurahNumber = null },
      onSelectAyahToMemorize = onSelectAyahToMemorize,
      onToggleAudioForAyah = onToggleAudioForAyah,
      onDownloadSurah = { onDownloadSurah(viewModeSurahNumber!!) },
      playingAyahNumber = playingAyahNumber
    )
  } else {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Quran Surahs (114)",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Select any Surah or Ayah to set as your active memorization target",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("surah_search_input"),
        placeholder = { Text("Search by name, Arabic, or number...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filteredSurahs, key = { it.number }) { surah ->
          SurahListItem(
            surah = surah,
            isSelected = surah.number == selectedSurahNumber,
            onClick = {
              onSelectSurah(surah.number)
              viewModeSurahNumber = surah.number
            }
          )
        }
      }
    }
  }
}

@Composable
private fun SurahListItem(
  surah: SurahInfo,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Card(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("surah_item_${surah.number}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${surah.number}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = surah.nameEnglish,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${surah.englishMeaning} • ${surah.numberOfAyahs} ayahs",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
          )
        }
      }

      Text(
        text = surah.nameArabic,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}

@Composable
private fun SurahDetailView(
  surah: SurahInfo?,
  ayahs: List<AyahEntity>,
  currentAyahId: Int,
  isDownloading: Boolean,
  onBack: () -> Unit,
  onSelectAyahToMemorize: (Int) -> Unit,
  onToggleAudioForAyah: (AyahEntity) -> Unit,
  onDownloadSurah: () -> Unit,
  playingAyahNumber: Int?
) {
  if (surah == null) return

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 2.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Surah ${surah.nameEnglish} (${surah.nameArabic})",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Text(
            text = "${surah.numberOfAyahs} verses • ${surah.revelationType}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
        }

        if (ayahs.isEmpty()) {
          FilledTonalButton(
            onClick = onDownloadSurah,
            enabled = !isDownloading
          ) {
            if (isDownloading) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
              Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Download", fontSize = 12.sp)
            }
          }
        }
      }
    }

    if (ayahs.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Ayahs for Surah ${surah.nameEnglish} are not yet cached offline.",
            textAlign = TextAlign.Center,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onDownloadSurah,
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
          ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download Surah Text for Offline")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(ayahs, key = { it.id }) { ayah ->
          val isCurrent = ayah.id == currentAyahId
          val isPlaying = playingAyahNumber == ayah.ayahNumberInSurah

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(28.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = "${ayah.ayahNumberInSurah}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }

                  if (ayah.isCompleted) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = "Completed",
                      tint = SuccessGreen,
                      modifier = Modifier.size(18.dp)
                    )
                  }

                  if (ayah.repetitionCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "${ayah.repetitionCount}x",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.secondary,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                Row {
                  IconButton(
                    onClick = { onToggleAudioForAyah(ayah) },
                    modifier = Modifier.size(34.dp)
                  ) {
                    Icon(
                      imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                      contentDescription = "Play Ayah Audio",
                      tint = MaterialTheme.colorScheme.primary
                    )
                  }

                  if (!isCurrent) {
                    Button(
                      onClick = { onSelectAyahToMemorize(ayah.id) },
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                      modifier = Modifier.height(32.dp)
                    ) {
                      Text("Memorize", fontSize = 11.sp)
                    }
                  } else {
                    Surface(
                      color = SuccessGreen.copy(alpha = 0.2f),
                      shape = RoundedCornerShape(8.dp),
                      border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                    ) {
                      Text(
                        text = "Active Target",
                        color = SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              val cleanText = ayah.textArabic.replace("۝", "").trim()
              val inlineId = "inline_surah_num_${ayah.id}"

              val annotatedString = buildAnnotatedString {
                append(cleanText)
                append(" ")
                appendInlineContent(inlineId, "[${ayah.ayahNumberInSurah}]")
              }

              val inlineContentMap = mapOf(
                inlineId to InlineTextContent(
                  Placeholder(
                    width = 28.sp,
                    height = 28.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                  )
                ) {
                  AyahNumberCircle(number = ayah.ayahNumberInSurah, size = 26.dp)
                }
              )

              Text(
                text = annotatedString,
                inlineContent = inlineContentMap,
                fontSize = 20.sp,
                lineHeight = 36.sp,
                textAlign = TextAlign.Right,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
        }
      }
    }
  }
}
