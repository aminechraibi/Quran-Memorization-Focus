package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserSettingsEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  settings: UserSettingsEntity,
  cacheSizeFormatted: String,
  cachedCount: Int,
  onUnlockEnabledChanged: (Boolean) -> Unit,
  onCooldownChanged: (Int) -> Unit,
  onReciterChanged: (String) -> Unit,
  onFontSizeChanged: (Int) -> Unit,
  onShowTranslationChanged: (Boolean) -> Unit,
  onShowTransliterationChanged: (Boolean) -> Unit,
  onAutoPlayChanged: (Boolean) -> Unit,
  onRepeatGoalChanged: (Int) -> Unit,
  onClearCache: () -> Unit,
  onRefreshCache: () -> Unit,
  onLaunchTestUnlock: () -> Unit
) {
  val context = LocalContext.current

  val canDrawOverlays = remember {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Settings.canDrawOverlays(context)
    } else true
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "Settings & Behavior",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Customize unlock behavior, recitation, and offline caching",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
      )
    }

    // Test Mode Quick Access Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Test Full-Screen Unlock",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "Preview the interactive unlock overlay right now without locking your device.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Button(
            onClick = onLaunchTestUnlock,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
          ) {
            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Test")
          }
        }
      }
    }

    // 1. Unlock Behavior Section
    item {
      SettingsSectionHeader(title = "Unlock Behavior", icon = Icons.Default.Speed)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Toggle Automatic Display
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Show on Phone Unlock",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
              )
              Text(
                text = "Launches current Ayah when phone is unlocked",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
              )
            }
            Switch(
              checked = settings.unlockDisplayEnabled,
              onCheckedChange = onUnlockEnabledChanged,
              modifier = Modifier.testTag("toggle_unlock_display_switch"),
              colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

          // Cooldown Period Selector
          Text(
            text = "Cooldown Interval",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
          )
          Text(
            text = "Prevents repeated interruptions if unlocking rapidly",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
          )

          Spacer(modifier = Modifier.height(10.dp))

          val cooldownOptions = listOf(
            0 to "Every unlock",
            1 to "1 min",
            5 to "5 min",
            15 to "15 min",
            30 to "30 min",
            60 to "1 hour"
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            cooldownOptions.take(3).forEach { (minutes, label) ->
              FilterChip(
                selected = settings.cooldownMinutes == minutes,
                onClick = { onCooldownChanged(minutes) },
                label = { Text(label, fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = EmeraldPrimary,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            cooldownOptions.drop(3).forEach { (minutes, label) ->
              FilterChip(
                selected = settings.cooldownMinutes == minutes,
                onClick = { onCooldownChanged(minutes) },
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
      }
    }

    // 2. Android Reliability & Background Permissions Guidance
    item {
      SettingsSectionHeader(title = "Reliability & Permissions", icon = Icons.Default.Security)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Android 10+ Background Behavior",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Modern Android limits background activity launches. To allow the full-screen Ayah to pop up seamlessly upon unlock, please grant 'Display over other apps'. A high-priority full-screen notification fallback is also active.",
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Display Over Other Apps",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
              Text(
                text = if (canDrawOverlays) "Permission Granted ✓" else "Permission Required for overlay popup",
                fontSize = 11.sp,
                color = if (canDrawOverlays) SuccessGreen else MaterialTheme.colorScheme.error
              )
            }

            if (!canDrawOverlays && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              OutlinedButton(
                onClick = {
                  val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                  )
                  context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("Enable", fontSize = 12.sp)
              }
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Battery Optimization",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
              Text(
                text = "Set to 'Unrestricted' in App Info so unlock detection isn't terminated by OS battery saving.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
              )
            }
          }
        }
      }
    }

    // 3. Audio & Reciter Settings
    item {
      SettingsSectionHeader(title = "Audio Recitation", icon = Icons.Default.GraphicEq)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Reciter (Quran Foundation / EveryAyah)",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          ReciterDropdown(
            currentReciter = settings.selectedReciter,
            onReciterChanged = onReciterChanged
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Autoplay on unlock
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Autoplay Audio on Unlock",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
              )
              Text(
                text = "Immediately plays recitation when the unlock screen appears",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
              )
            }
            Switch(
              checked = settings.autoPlayAudioOnUnlock,
              onCheckedChange = onAutoPlayChanged,
              colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

          // Repetition Goal
          Text(
            text = "Target Repetitions per Ayah: ${settings.repeatCountGoal}x",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
          )
          Slider(
            value = settings.repeatCountGoal.toFloat(),
            onValueChange = { onRepeatGoalChanged(it.toInt()) },
            valueRange = 1f..15f,
            steps = 13,
            colors = SliderDefaults.colors(
              thumbColor = GoldPrimary,
              activeTrackColor = EmeraldPrimary
            )
          )
        }
      }
    }

    // 4. Display & Typography Settings
    item {
      SettingsSectionHeader(title = "Display & Reading", icon = Icons.Default.TextFields)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Arabic Font Size: ${settings.arabicFontSizeSp} sp",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
          )
          Slider(
            value = settings.arabicFontSizeSp.toFloat(),
            onValueChange = { onFontSizeChanged(it.toInt()) },
            valueRange = 22f..42f,
            steps = 9,
            colors = SliderDefaults.colors(
              thumbColor = GoldPrimary,
              activeTrackColor = EmeraldPrimary
            )
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

          Text(
            text = "Pure Arabic Hifz Mode",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Active for maximum focus during memorization. English translations are hidden across the unlock screen and main app.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
    }

    // 5. Offline Storage & Cache Section
    item {
      SettingsSectionHeader(title = "Offline & Cache", icon = Icons.Default.Cached)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Permanent Offline Window",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
          )
          Text(
            text = "The current Ayah and the surrounding 10 Ayahs are permanently kept in Room database and cached for 100% offline access.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Audio Cache Size",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "$cacheSizeFormatted ($cachedCount files)",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
              )
            }

            OutlinedButton(
              onClick = onClearCache,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
              Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Clear Cache", fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 4.dp)
  ) {
    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = title,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReciterDropdown(
  currentReciter: String,
  onReciterChanged: (String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }

  val reciters = listOf(
    "Husary_128kbps" to "Mahmoud Khalil Al-Hussary (Murattal)",
    "Husary_Muallim_128kbps" to "Mahmoud Khalil Al-Hussary (Mu'allim / Teaching)",
    "Alafasy_128kbps" to "Mishary Rashid Al-Afasy",
    "Minshawy_Murattal_128kbps" to "Mohamed Siddiq El-Minshawi"
  )

  val currentLabel = reciters.find { it.first == currentReciter }?.second ?: "Mahmoud Khalil Al-Hussary"

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = !expanded }
  ) {
    OutlinedTextField(
      value = currentLabel,
      onValueChange = {},
      readOnly = true,
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
      modifier = Modifier
        .menuAnchor()
        .fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    )

    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false }
    ) {
      reciters.forEach { (id, label) ->
        DropdownMenuItem(
          text = { Text(label, fontSize = 13.sp) },
          onClick = {
            onReciterChanged(id)
            expanded = false
          }
        )
      }
    }
  }
}
