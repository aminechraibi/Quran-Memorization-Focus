package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.QuranSurahDirectory
import com.example.data.model.SurahInfo
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.RepeatAmber
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.QuranUiState

@Composable
fun HomeScreen(
  uiState: QuranUiState,
  onLaunchTestUnlock: () -> Unit,
  onSelectSurahAndAyah: (surahNumber: Int, ayahNumber: Int) -> Unit,
  onUnlockEnabledChanged: (Boolean) -> Unit,
  onCooldownChanged: (Int) -> Unit,
  onRepeatGoalChanged: (Int) -> Unit,
  onFontSizeChanged: (Int) -> Unit,
  onReciterChanged: (String) -> Unit,
  onAudioSpeedChanged: (Float) -> Unit,
  onAutoPlayChanged: (Boolean) -> Unit,
  onVersesCountChanged: (Int) -> Unit = {},
  onVerseJumpStepChanged: (Int) -> Unit = {},
  onNavigateToSurahs: () -> Unit = {}
) {
  val currentAyah = uiState.currentAyah
  val settings = uiState.settings
  val activeSurahNumber = currentAyah?.surahNumber ?: 1
  val activeAyahNumber = currentAyah?.ayahNumberInSurah ?: 1

  var showSurahDialog by remember { mutableStateOf(false) }
  var showAyahJumpDialog by remember { mutableStateOf(false) }

  val currentSurahInfo = remember(activeSurahNumber) {
    QuranSurahDirectory.getSurahByNumber(activeSurahNumber) ?: QuranSurahDirectory.surahs.first()
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. HERO BANNER WITH THE PRIMARY "SHOW SCREEN" BUTTON
    item {
      HeroLaunchCard(
        settings = settings,
        onLaunchTestUnlock = onLaunchTestUnlock
      )
    }

    // 2. SURAH & AYAH SELECTOR (Configurable Target)
    item {
      SurahAndAyahSelectorCard(
        surahInfo = currentSurahInfo,
        ayahNumber = activeAyahNumber,
        settings = settings,
        totalRepetitions = currentAyah?.repetitionCount ?: 0,
        repeatGoal = settings.repeatCountGoal,
        onOpenSurahDialog = { showSurahDialog = true },
        onOpenAyahJumpDialog = { showAyahJumpDialog = true },
        onPreviousAyah = {
          val jump = settings.verseJumpStep.coerceAtLeast(1)
          if (activeAyahNumber > jump) {
            onSelectSurahAndAyah(activeSurahNumber, activeAyahNumber - jump)
          } else if (activeSurahNumber > 1) {
            val prevSurah = QuranSurahDirectory.getSurahByNumber(activeSurahNumber - 1)
            if (prevSurah != null) {
              val targetAyah = (prevSurah.numberOfAyahs - (jump - activeAyahNumber)).coerceIn(1, prevSurah.numberOfAyahs)
              onSelectSurahAndAyah(prevSurah.number, targetAyah)
            }
          } else {
            onSelectSurahAndAyah(1, 1)
          }
        },
        onNextAyah = {
          val jump = settings.verseJumpStep.coerceAtLeast(1)
          if (activeAyahNumber + jump <= currentSurahInfo.numberOfAyahs) {
            onSelectSurahAndAyah(activeSurahNumber, activeAyahNumber + jump)
          } else if (activeSurahNumber < 114) {
            val nextSurah = QuranSurahDirectory.getSurahByNumber(activeSurahNumber + 1)
            if (nextSurah != null) {
              val overflow = (activeAyahNumber + jump) - currentSurahInfo.numberOfAyahs
              val targetAyah = overflow.coerceIn(1, nextSurah.numberOfAyahs)
              onSelectSurahAndAyah(nextSurah.number, targetAyah)
            }
          }
        }
      )
    }

    // 3. ESSENTIAL CONFIGURATIONS ONLY
    item {
      EssentialConfigsCard(
        settings = settings,
        onUnlockEnabledChanged = onUnlockEnabledChanged,
        onCooldownChanged = onCooldownChanged,
        onRepeatGoalChanged = onRepeatGoalChanged,
        onFontSizeChanged = onFontSizeChanged,
        onReciterChanged = onReciterChanged,
        onAudioSpeedChanged = onAudioSpeedChanged,
        onAutoPlayChanged = onAutoPlayChanged,
        onVersesCountChanged = onVersesCountChanged,
        onVerseJumpStepChanged = onVerseJumpStepChanged
      )
    }

    // 4. BRIEF PROGRESS STATS
    item {
      MemorizationQuickStats(
        completedCount = uiState.completedCount,
        totalRepetitions = uiState.totalRepetitions,
        cachedAyahs = uiState.cachedAyahsCount,
        onViewDirectory = onNavigateToSurahs
      )
    }
  }

  // DIALOG: SELECT SURAH (Searchable list of all 114 Surahs)
  if (showSurahDialog) {
    SurahSelectionDialog(
      currentSurahNumber = activeSurahNumber,
      onSelectSurah = { selectedSurah ->
        showSurahDialog = false
        onSelectSurahAndAyah(selectedSurah.number, 1)
      },
      onDismiss = { showSurahDialog = false }
    )
  }

  // DIALOG: JUMP TO AYAH
  if (showAyahJumpDialog) {
    AyahJumpDialog(
      surahInfo = currentSurahInfo,
      currentAyahNumber = activeAyahNumber,
      onSelectAyah = { targetAyah ->
        showAyahJumpDialog = false
        onSelectSurahAndAyah(activeSurahNumber, targetAyah)
      },
      onDismiss = { showAyahJumpDialog = false }
    )
  }
}

/**
 * Top Hero Card featuring Islamic branding and the primary button to immediately open the memorization screen.
 */
@Composable
private fun HeroLaunchCard(
  settings: com.example.data.local.UserSettingsEntity,
  onLaunchTestUnlock: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = EmeraldDark),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color(0xFF092018),
              Color(0xFF134E3C),
              Color(0xFF0A231C)
            )
          )
        )
        .padding(20.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "حِفْظُ القُرْآن الكَرِيم",
              color = GoldLight,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Quran Memorization on Unlock",
              color = Color.White.copy(alpha = 0.9f),
              fontSize = 14.sp
            )
          }

          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(GoldPrimary.copy(alpha = 0.2f))
              .border(1.2.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.ic_quran_logo),
              contentDescription = "Quran Logo",
              modifier = Modifier.size(32.dp),
              contentScale = ContentScale.Fit
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status indicator
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.25f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(if (settings.unlockDisplayEnabled) SuccessGreen else Color.Gray)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (settings.unlockDisplayEnabled) {
              "Unlock trigger: ACTIVE (${if (settings.cooldownMinutes == 0) "Every unlock" else "${settings.cooldownMinutes} min cooldown"})"
            } else {
              "Unlock trigger: PAUSED (Enable in settings below)"
            },
            color = Color(0xFFD4E6DF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // THE PRIMARY BUTTON TO SHOW THE MEMORIZATION SCREEN
        Button(
          onClick = onLaunchTestUnlock,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("test_unlock_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = GoldPrimary,
            contentColor = Color(0xFF281800)
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LockOpen,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Show Memorization Screen • إظهار شاشة الحفظ",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }
      }
    }
  }
}

/**
 * Surah and Ayah selection card allowing users to configure which verse to memorize.
 * Avoids rendering the entire Ayah text/audio, keeping the main screen concise and focused.
 */
@Composable
private fun SurahAndAyahSelectorCard(
  surahInfo: SurahInfo,
  ayahNumber: Int,
  settings: com.example.data.local.UserSettingsEntity,
  totalRepetitions: Int,
  repeatGoal: Int,
  onOpenSurahDialog: () -> Unit,
  onOpenAyahJumpDialog: () -> Unit,
  onPreviousAyah: () -> Unit,
  onNextAyah: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Memorization Target",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        // Repetitions badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (totalRepetitions >= repeatGoal) SuccessGreen.copy(alpha = 0.15f) else RepeatAmber.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (totalRepetitions >= repeatGoal) SuccessGreen.copy(alpha = 0.4f) else RepeatAmber.copy(alpha = 0.4f)
          )
        ) {
          Text(
            text = "$totalRepetitions / $repeatGoal repeats",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (totalRepetitions >= repeatGoal) SuccessGreen else RepeatAmber,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Surah Selector Button
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .clickable { onOpenSurahDialog() }
          .testTag("select_surah_button"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "SURAH (TAP TO CHANGE)",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${surahInfo.number}. ${surahInfo.nameEnglish} • ${surahInfo.nameArabic}",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${surahInfo.numberOfAyahs} Ayahs • ${surahInfo.revelationType}",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Select Surah",
            tint = EmeraldPrimary,
            modifier = Modifier.size(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Ayah Selector Controls (Stepper + Jump Button)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Previous Ayah button
        IconButton(
          onClick = onPreviousAyah,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .testTag("prev_ayah_stepper_button")
        ) {
          Icon(Icons.Default.Remove, contentDescription = "Previous Ayah", tint = EmeraldPrimary)
        }

        // Center: Current Ayah Badge with Jump Dialog on click
        Surface(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onOpenAyahJumpDialog() }
            .testTag("jump_ayah_button"),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f))
        ) {
          Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            val verseText = if (settings.versesCount > 1) {
              val endAyah = minOf(surahInfo.numberOfAyahs, ayahNumber + settings.versesCount - 1)
              "Ayat $ayahNumber–$endAyah (${settings.versesCount} verses shown)"
            } else {
              "Ayah $ayahNumber of ${surahInfo.numberOfAyahs}"
            }
            Text(
              text = verseText,
              fontSize = if (settings.versesCount > 1) 13.sp else 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )
            Text(
              text = if (settings.verseJumpStep > 1) "Jump step: +${settings.verseJumpStep} • Tap to jump" else "Tap to jump",
              fontSize = 11.sp,
              color = GoldDark,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Next Ayah button
        IconButton(
          onClick = onNextAyah,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .testTag("next_ayah_stepper_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Next Ayah", tint = EmeraldPrimary)
        }
      }
    }
  }
}

/**
 * Essential configurations only card:
 * Unlock toggle, repetition goal, font size, reciter speed, and cooldown.
 */
@Composable
private fun EssentialConfigsCard(
  settings: com.example.data.local.UserSettingsEntity,
  onUnlockEnabledChanged: (Boolean) -> Unit,
  onCooldownChanged: (Int) -> Unit,
  onRepeatGoalChanged: (Int) -> Unit,
  onFontSizeChanged: (Int) -> Unit,
  onReciterChanged: (String) -> Unit,
  onAudioSpeedChanged: (Float) -> Unit,
  onAutoPlayChanged: (Boolean) -> Unit,
  onVersesCountChanged: (Int) -> Unit = {},
  onVerseJumpStepChanged: (Int) -> Unit = {}
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = null,
          tint = EmeraldPrimary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Memorization Settings",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 1. UNLOCK DISPLAY TOGGLE
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Show on Phone Unlock",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Presents full-screen Ayah automatically upon unlocking phone",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Switch(
          checked = settings.unlockDisplayEnabled,
          onCheckedChange = onUnlockEnabledChanged,
          modifier = Modifier.testTag("unlock_enabled_switch"),
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = EmeraldPrimary
          )
        )
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 14.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
      )

      // 2. VERSES DISPLAYED AT ONCE (Default 1, Min 1, Max 10)
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Verses Displayed at Once",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Consecutive verses shown together (1 to 10)",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            IconButton(
              onClick = { if (settings.versesCount > 1) onVersesCountChanged(settings.versesCount - 1) },
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Decrease verses", modifier = Modifier.size(16.dp))
            }
            Text(
              text = "${settings.versesCount}",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary,
              modifier = Modifier.padding(horizontal = 6.dp)
            )
            IconButton(
              onClick = { if (settings.versesCount < 10) onVersesCountChanged(settings.versesCount + 1) },
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Increase verses", modifier = Modifier.size(16.dp))
            }
          }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(1, 2, 3, 5, 7, 10).forEach { count ->
            val isSelected = settings.versesCount == count
            FilterChip(
              selected = isSelected,
              onClick = { onVersesCountChanged(count) },
              label = { Text("${count}v", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 14.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
      )

      // 3. PROGRESSION JUMP / SKIP STEP (1 to versesCount)
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Progression Jump Step",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (settings.versesCount > 1) {
                "Advance by up to ${settings.versesCount} verses on Next/Prev or unlock"
              } else {
                "Advances by 1 verse on each progression"
              },
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          if (settings.versesCount > 1) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              IconButton(
                onClick = { if (settings.verseJumpStep > 1) onVerseJumpStepChanged(settings.verseJumpStep - 1) },
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceVariant)
              ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease jump", modifier = Modifier.size(16.dp))
              }
              Text(
                text = "+${settings.verseJumpStep}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GoldDark,
                modifier = Modifier.padding(horizontal = 6.dp)
              )
              IconButton(
                onClick = { if (settings.verseJumpStep < settings.versesCount) onVerseJumpStepChanged(settings.verseJumpStep + 1) },
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceVariant)
              ) {
                Icon(Icons.Default.Add, contentDescription = "Increase jump", modifier = Modifier.size(16.dp))
              }
            }
          }
        }

        if (settings.versesCount > 1) {
          Spacer(modifier = Modifier.height(8.dp))
          val maxChips = minOf(settings.versesCount, 5)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            (1..maxChips).forEach { step ->
              val isSelected = settings.verseJumpStep == step
              val label = when (step) {
                1 -> "1 (Rolling)"
                settings.versesCount -> "$step (Next $step)"
                else -> "Skip $step"
              }
              FilterChip(
                selected = isSelected,
                onClick = { onVerseJumpStepChanged(step) },
                label = { Text(label, fontSize = 10.sp) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = GoldDark,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 14.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
      )

      // 2. REPETITION GOAL
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Repetition Goal per Ayah",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${settings.repeatCountGoal} times",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = GoldDark
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(3, 5, 7, 10, 20).forEach { goal ->
            val isSelected = settings.repeatCountGoal == goal
            FilterChip(
              selected = isSelected,
              onClick = { onRepeatGoalChanged(goal) },
              label = { Text("${goal}x", fontSize = 12.sp) },
              modifier = Modifier.weight(1f),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 14.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
      )

      // 3. UNLOCK SCREEN FONT SIZE
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Unlock Screen Font Size",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${settings.arabicFontSizeSp} sp",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("22sp", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Slider(
            value = settings.arabicFontSizeSp.toFloat(),
            onValueChange = { onFontSizeChanged(it.toInt()) },
            valueRange = 22f..44f,
            steps = 10,
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 8.dp)
              .testTag("font_size_slider"),
            colors = SliderDefaults.colors(
              thumbColor = EmeraldPrimary,
              activeTrackColor = EmeraldPrimary
            )
          )
          Text("44sp", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 14.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
      )

      // 4. UNLOCK COOLDOWN
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Unlock Cooldown",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Avoids showing repeatedly if phone is unlocked multiple times in minutes",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val cooldowns = listOf(
            0 to "Every unlock",
            1 to "1 min",
            5 to "5 min",
            15 to "15 min"
          )
          cooldowns.forEach { (mins, label) ->
            val isSelected = settings.cooldownMinutes == mins
            FilterChip(
              selected = isSelected,
              onClick = { onCooldownChanged(mins) },
              label = { Text(label, fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 14.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
      )

      // 5. RECITER & AUDIO PLAYBACK SPEED
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Reciter Recitation",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val reciters = listOf(
            "Husary_128kbps" to "Al-Hussary",
            "Alafasy_128kbps" to "Al-Afasy",
            "Abdul_Basit_Murattal_192kbps" to "Abdul Basit"
          )
          reciters.forEach { (key, name) ->
            val isSelected = settings.selectedReciter == key
            FilterChip(
              selected = isSelected,
              onClick = { onReciterChanged(key) },
              label = { Text(name, fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Audio Playback Speed",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(1.0f, 1.25f, 1.5f, 2.0f, 3.0f).forEach { spd ->
            FilterChip(
              selected = false,
              onClick = { onAudioSpeedChanged(spd) },
              label = { Text("${spd}x", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Autoplay Audio",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Stream recitation automatically when unlock screen appears",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Switch(
            checked = settings.autoPlayAudioOnUnlock,
            onCheckedChange = onAutoPlayChanged,
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = EmeraldPrimary
            )
          )
        }
      }
    }
  }
}

/**
 * Clean quick stats summary.
 */
@Composable
private fun MemorizationQuickStats(
  completedCount: Int,
  totalRepetitions: Int,
  cachedAyahs: Int,
  onViewDirectory: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Card(
      modifier = Modifier.weight(1f),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      )
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "Completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "$completedCount",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = SuccessGreen
        )
        Text(text = "Ayahs", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }

    Card(
      modifier = Modifier.weight(1f),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      )
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "Repetitions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "$totalRepetitions",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = GoldDark
        )
        Text(text = "Total done", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }

    Card(
      modifier = Modifier
        .weight(1f)
        .clickable { onViewDirectory() },
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      )
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "Offline Cached", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "$cachedAyahs",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = EmeraldPrimary
        )
        Text(text = "Browse Surahs →", fontSize = 10.sp, color = EmeraldPrimary)
      }
    }
  }
}

/**
 * Searchable selection dialog for all 114 Surahs.
 */
@Composable
private fun SurahSelectionDialog(
  currentSurahNumber: Int,
  onSelectSurah: (SurahInfo) -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  val allSurahs = QuranSurahDirectory.surahs
  val filteredSurahs = remember(searchQuery) {
    if (searchQuery.isBlank()) {
      allSurahs
    } else {
      allSurahs.filter { surah ->
        surah.nameEnglish.contains(searchQuery, ignoreCase = true) ||
          surah.nameArabic.contains(searchQuery) ||
          surah.number.toString() == searchQuery.trim()
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Text(
          text = "Select Surah",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by name or number...", fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear")
              }
            }
          },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    text = {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(350.dp)
      ) {
        items(filteredSurahs, key = { it.number }) { surah ->
          val isSelected = surah.number == currentSurahNumber
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else Color.Transparent)
              .clickable { onSelectSurah(surah) }
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(30.dp)
                  .clip(CircleShape)
                  .background(if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${surah.number}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = surah.nameEnglish,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "${surah.numberOfAyahs} verses • ${surah.revelationType}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Text(
              text = surah.nameArabic,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary,
              fontFamily = FontFamily.Serif
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

/**
 * Direct Ayah Jump dialog allowing instant jumping to any Ayah in the Surah.
 */
@Composable
private fun AyahJumpDialog(
  surahInfo: SurahInfo,
  currentAyahNumber: Int,
  onSelectAyah: (Int) -> Unit,
  onDismiss: () -> Unit
) {
  var inputAyahText by remember { mutableStateOf(currentAyahNumber.toString()) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Jump to Ayah in Surah ${surahInfo.nameEnglish}",
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Enter an Ayah number between 1 and ${surahInfo.numberOfAyahs}:",
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = inputAyahText,
          onValueChange = {
            inputAyahText = it
            errorMessage = null
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          label = { Text("Ayah Number (1 - ${surahInfo.numberOfAyahs})") },
          isError = errorMessage != null,
          supportingText = {
            errorMessage?.let {
              Text(it, color = MaterialTheme.colorScheme.error)
            }
          },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick jump shortcuts for long surahs
        if (surahInfo.numberOfAyahs >= 10) {
          Text(text = "Quick shortcuts:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            val shortcuts = listOf(1, surahInfo.numberOfAyahs / 4, surahInfo.numberOfAyahs / 2, surahInfo.numberOfAyahs)
              .distinct()
              .filter { it in 1..surahInfo.numberOfAyahs }

            shortcuts.forEach { num ->
              OutlinedButton(
                onClick = { inputAyahText = num.toString() },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
              ) {
                Text("$num", fontSize = 11.sp)
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val parsed = inputAyahText.toIntOrNull()
          if (parsed != null && parsed in 1..surahInfo.numberOfAyahs) {
            onSelectAyah(parsed)
          } else {
            errorMessage = "Please enter a valid number between 1 and ${surahInfo.numberOfAyahs}"
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
      ) {
        Text("Go")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
