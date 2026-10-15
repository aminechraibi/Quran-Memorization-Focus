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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlayerState
import com.example.data.local.AyahEntity
import com.example.data.local.UserSettingsEntity
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.RepeatAmber
import com.example.ui.theme.SuccessGreen

/**
 * Islamic circular medallion with the Ayah number centered inside.
 */
@Composable
fun AyahNumberCircle(
  number: Int,
  modifier: Modifier = Modifier,
  size: Dp = 36.dp
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF14382B))
      .border(1.6.dp, GoldPrimary, CircleShape),
    contentAlignment = Alignment.Center
  ) {
    // Inner decorative gold ring
    Box(
      modifier = Modifier
        .size((size.value * 0.78f).dp)
        .border(0.9.dp, GoldLight.copy(alpha = 0.7f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr
      ) {
        Text(
          text = "$number",
          color = GoldLight,
          fontSize = (size.value * 0.40f).sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

@Composable
fun MemorizeUnlockScreen(
  ayah: AyahEntity?,
  ayahs: List<AyahEntity> = emptyList(),
  settings: UserSettingsEntity,
  audioState: AudioPlayerState,
  isTestMode: Boolean = false,
  onFontSizeChanged: (Int) -> Unit = {},
  onCycleAudioSpeed: () -> Unit = {},
  onIncrementRepetition: () -> Unit = {},
  onDecrementRepetition: () -> Unit = {},
  onDoneClicked: () -> Unit,
  onRepeatClicked: () -> Unit,
  onPreviousClicked: () -> Unit,
  onNextClicked: () -> Unit,
  onToggleAudio: () -> Unit,
  onReplayAudio: () -> Unit,
  onSeekAudio: (Long) -> Unit,
  onDismiss: () -> Unit
) {
  val displayAyahs = androidx.compose.runtime.remember(ayah, ayahs) {
    if (ayahs.isNotEmpty()) ayahs else listOfNotNull(ayah)
  }
  val primaryAyah = displayAyahs.firstOrNull() ?: ayah

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF071B14),
            Color(0xFF0D281E),
            Color(0xFF081611)
          )
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. FIXED TOP SECTION: Header with Font Sizer, Surah badge, Interactive Repetition Stepper
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        TopUnlockBar(
          ayah = primaryAyah,
          isTestMode = isTestMode,
          fontSize = settings.arabicFontSizeSp,
          onFontSizeChanged = onFontSizeChanged,
          onClose = onDismiss
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (displayAyahs.isNotEmpty()) {
          val first = displayAyahs.first()
          val last = displayAyahs.last()
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Surah Pill
            val surahLabel = if (displayAyahs.size > 1) {
              if (first.surahNumber == last.surahNumber) {
                "Surah ${first.surahNameEnglish} (${first.surahNameArabic}) • Ayat ${first.ayahNumberInSurah}–${last.ayahNumberInSurah}"
              } else {
                "Surah ${first.surahNameEnglish}:${first.ayahNumberInSurah} → ${last.surahNameEnglish}:${last.ayahNumberInSurah}"
              }
            } else {
              "Surah ${first.surahNameEnglish} (${first.surahNameArabic}) • Ayah ${first.ayahNumberInSurah}"
            }

            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(EmeraldDark)
                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = surahLabel,
                color = GoldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            // Interactive Repetition Stepper Badge
            RepetitionStepperBadge(
              currentCount = first.repetitionCount,
              goal = settings.repeatCountGoal,
              onIncrement = onIncrementRepetition,
              onDecrement = onDecrementRepetition
            )
          }

          // Bismillah header
          if (first.surahNumber != 9 && first.ayahNumberInSurah != 1) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
              color = GoldLight.copy(alpha = 0.85f),
              fontSize = 17.sp,
              textAlign = TextAlign.Center
            )
          }
        }
      }

      // 2. MIDDLE SECTION: Fits screen and ONLY the Ayah Arabic text scrolls
      // All verses are rendered consecutively, each followed immediately by its circular medallion at the end on left!
      if (displayAyahs.isNotEmpty()) {
        Card(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("ayah_arabic_card"),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color(0xFF102820).copy(alpha = 0.95f)
          ),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.35f))
        ) {
          val ayahScrollState = rememberScrollState()
          Box(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(ayahScrollState)
              .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
          ) {
            val annotatedString = buildAnnotatedString {
              displayAyahs.forEachIndexed { index, itemAyah ->
                val cleanArabicText = itemAyah.textArabic.replace("۝", "").trim()
                append(cleanArabicText)
                append("\u200F\u00A0")
                appendInlineContent("inline_ayah_number_${itemAyah.id}", "\u200F[${itemAyah.ayahNumberInSurah}]\u200F")
                if (index < displayAyahs.size - 1) {
                  append(" \u200F")
                }
              }
            }

            val badgeSize = (settings.arabicFontSizeSp * 1.30f).dp
            val inlineContentMap = displayAyahs.associate { itemAyah ->
              "inline_ayah_number_${itemAyah.id}" to InlineTextContent(
                Placeholder(
                  width = (settings.arabicFontSizeSp * 1.35f).sp,
                  height = (settings.arabicFontSizeSp * 1.35f).sp,
                  placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
              ) {
                AyahNumberCircle(
                  number = itemAyah.ayahNumberInSurah,
                  size = badgeSize,
                  modifier = Modifier.testTag("ayah_number_circle")
                )
              }
            }

            androidx.compose.runtime.CompositionLocalProvider(
              androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
            ) {
              Text(
                text = annotatedString,
                inlineContent = inlineContentMap,
                color = Color(0xFFFFF7E6),
                fontSize = settings.arabicFontSizeSp.sp,
                lineHeight = (settings.arabicFontSizeSp * 1.8f).sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                style = androidx.compose.ui.text.TextStyle(
                  textDirection = androidx.compose.ui.text.style.TextDirection.Rtl
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("ayah_arabic_text")
              )
            }
          }
        }
      } else {
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = GoldPrimary)
        }
      }

      // 3. FIXED BOTTOM SECTION: Audio Player with Speed Control + Navigation + Repeat/Done
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Audio Player Controls (Compact with Speed Selector)
        AudioPlayerSection(
          audioState = audioState,
          onToggleAudio = onToggleAudio,
          onReplayAudio = onReplayAudio,
          onSeekAudio = onSeekAudio,
          onCycleSpeed = onCycleAudioSpeed
        )

        // Previous / Next Nav
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilledTonalButton(
            onClick = onPreviousClicked,
            modifier = Modifier.testTag("nav_previous_button"),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = Color(0xFF142B24),
              contentColor = GoldLight
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Ayah", modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (settings.verseJumpStep > 1) "Prev (-${settings.verseJumpStep})" else "Previous", fontSize = 12.sp)
          }

          Text(
            text = "Hifz • ${settings.versesCount} ${if (settings.versesCount == 1) "Ayah" else "Ayat"}",
            color = GoldLight.copy(alpha = 0.65f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )

          FilledTonalButton(
            onClick = onNextClicked,
            modifier = Modifier.testTag("nav_next_button"),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = Color(0xFF142B24),
              contentColor = GoldLight
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Text(if (settings.verseJumpStep > 1) "Next (+${settings.verseJumpStep})" else "Next", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Ayah", modifier = Modifier.size(14.dp))
          }
        }

        // Primary Done & Repeat Actions
        BottomActionButtons(
          onDoneClicked = onDoneClicked,
          onRepeatClicked = onRepeatClicked
        )
      }
    }
  }
}

@Composable
private fun TopUnlockBar(
  ayah: AyahEntity?,
  isTestMode: Boolean,
  fontSize: Int,
  onFontSizeChanged: (Int) -> Unit,
  onClose: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(GoldPrimary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = ayah?.surahNameArabic?.take(2) ?: "قر",
          color = GoldLight,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = if (isTestMode) "Test Unlock Mode" else "Unlock Memorization",
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = ayah?.let { "${it.surahNameEnglish} : Ayah ${it.ayahNumberInSurah}" } ?: "Loading...",
          color = GoldLight,
          fontSize = 11.sp
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      // Font Size Controls (A- / A+) right on the unlock screen
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color.White.copy(alpha = 0.1f))
          .border(0.8.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
          .padding(horizontal = 2.dp, vertical = 1.dp)
      ) {
        IconButton(
          onClick = { onFontSizeChanged((fontSize - 2).coerceAtLeast(20)) },
          modifier = Modifier
            .size(30.dp)
            .testTag("decrease_font_size_button")
        ) {
          Text("A-", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Text(
          text = "${fontSize}sp",
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(horizontal = 2.dp)
        )

        IconButton(
          onClick = { onFontSizeChanged((fontSize + 2).coerceAtMost(48)) },
          modifier = Modifier
            .size(30.dp)
            .testTag("increase_font_size_button")
        ) {
          Text("A+", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = onClose,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.1f))
          .testTag("close_unlock_screen_button")
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close",
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
private fun RepetitionStepperBadge(
  currentCount: Int,
  goal: Int,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit
) {
  val isGoalReached = currentCount >= goal

  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(14.dp))
      .background(if (isGoalReached) SuccessGreen.copy(alpha = 0.2f) else RepeatAmber.copy(alpha = 0.2f))
      .border(
        1.dp,
        if (isGoalReached) SuccessGreen else RepeatAmber.copy(alpha = 0.6f),
        RoundedCornerShape(14.dp)
      )
      .padding(horizontal = 6.dp, vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = if (isGoalReached) Icons.Default.CheckCircle else Icons.Default.Repeat,
      contentDescription = null,
      tint = if (isGoalReached) Color(0xFF66BB6A) else Color(0xFFFFB74D),
      modifier = Modifier.size(13.dp)
    )

    Spacer(modifier = Modifier.width(4.dp))

    // Minus button
    IconButton(
      onClick = onDecrement,
      modifier = Modifier.size(24.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Remove,
        contentDescription = "Decrease repetition",
        tint = Color.White,
        modifier = Modifier.size(12.dp)
      )
    }

    Text(
      text = "$currentCount / $goal",
      color = if (isGoalReached) Color(0xFFC8E6C9) else Color(0xFFFFE0B2),
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(horizontal = 2.dp)
    )

    // Plus button
    IconButton(
      onClick = onIncrement,
      modifier = Modifier.size(24.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Increase repetition",
        tint = Color.White,
        modifier = Modifier.size(12.dp)
      )
    }
  }
}

@Composable
private fun AudioPlayerSection(
  audioState: AudioPlayerState,
  onToggleAudio: () -> Unit,
  onReplayAudio: () -> Unit,
  onSeekAudio: (Long) -> Unit,
  onCycleSpeed: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = Color(0xFF0F261E)
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Recitation (Al-Hussary)",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = if (audioState.isPlaying) "Playing..." else "Online stream & offline cache",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Speed Up Button (1.0x, 1.25x, 1.5x, 2.0x, 3.0x)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF1E4637))
              .border(1.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
              .clickable { onCycleSpeed() }
              .padding(horizontal = 8.dp, vertical = 5.dp)
              .testTag("audio_speed_chip"),
            contentAlignment = Alignment.Center
          ) {
            val speedLabel = if (audioState.playbackSpeed == 1.0f) "1x"
            else if (audioState.playbackSpeed == 1.25f) "1.25x"
            else if (audioState.playbackSpeed == 1.5f) "1.5x"
            else if (audioState.playbackSpeed == 2.0f) "2x"
            else if (audioState.playbackSpeed == 3.0f) "3x"
            else "${audioState.playbackSpeed}x"

            Text(
              text = speedLabel,
              color = GoldLight,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = onReplayAudio,
            modifier = Modifier
              .size(34.dp)
              .testTag("audio_replay_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Replay Audio",
              tint = GoldLight,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = onToggleAudio,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(GoldPrimary)
              .testTag("audio_play_pause_button")
          ) {
            if (audioState.isLoading) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color(0xFF281800),
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (audioState.isPlaying) "Pause" else "Play",
                tint = Color(0xFF281800),
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      // Slider & Duration
      if (audioState.durationMs > 0) {
        Slider(
          value = audioState.currentPositionMs.toFloat().coerceIn(0f, audioState.durationMs.toFloat()),
          onValueChange = { onSeekAudio(it.toLong()) },
          valueRange = 0f..audioState.durationMs.toFloat(),
          colors = SliderDefaults.colors(
            thumbColor = GoldLight,
            activeTrackColor = GoldPrimary,
            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .testTag("audio_seek_slider")
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = formatTime(audioState.currentPositionMs),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp
          )
          Text(
            text = formatTime(audioState.durationMs),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp
          )
        }
      }

      AnimatedVisibility(visible = audioState.errorMessage != null) {
        Text(
          text = audioState.errorMessage ?: "",
          color = Color(0xFFFF8A80),
          fontSize = 10.sp,
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    }
  }
}

@Composable
private fun BottomActionButtons(
  onDoneClicked: () -> Unit,
  onRepeatClicked: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Repeat Button: Keeps same ayah for next unlock, dismisses screen
    Button(
      onClick = onRepeatClicked,
      modifier = Modifier
        .weight(1f)
        .height(48.dp)
        .testTag("repeat_ayah_button"),
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF332008),
        contentColor = Color(0xFFFFB74D)
      ),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE65100))
    ) {
      Icon(
        imageVector = Icons.Default.Repeat,
        contentDescription = null,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Repeat",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
    }

    // Done Button: Marks completed, advances to next ayah for next unlock, dismisses screen
    Button(
      onClick = onDoneClicked,
      modifier = Modifier
        .weight(1f)
        .height(48.dp)
        .testTag("done_ayah_button"),
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = EmeraldPrimary,
        contentColor = Color.White
      )
    ) {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = null,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Done",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
    }
  }
}

private fun formatTime(millis: Long): String {
  val totalSec = millis / 1000
  val min = totalSec / 60
  val sec = totalSec % 60
  return String.format("%02d:%02d", min, sec)
}
