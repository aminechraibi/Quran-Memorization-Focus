package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.UnlockMonitorService
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemorizeUnlockScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SurahListScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.QuranMemorizerTheme
import com.example.viewmodel.QuranViewModel

sealed class AppTab(val index: Int, val title: String, val icon: ImageVector) {
  object Home : AppTab(0, "Memorize", Icons.Default.MenuBook)
  object Surahs : AppTab(1, "Quran", Icons.Default.LibraryBooks)
  object Progress : AppTab(2, "Progress", Icons.Default.Insights)
  object Settings : AppTab(3, "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

  private val viewModel: QuranViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Start background monitor service if enabled
    UnlockMonitorService.startService(this)

    setContent {
      QuranMemorizerTheme {
        QuranAppContent(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun QuranAppContent(viewModel: QuranViewModel) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val allAyahs by (viewModel.getApplication<QuranApplication>()).repository.allAyahs.collectAsStateWithLifecycle(initialValue = emptyList())

  var currentTab by remember { mutableIntStateOf(0) }
  val snackbarHostState = remember { SnackbarHostState() }

  // Request notification permission on Android 13+
  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { /* handled gracefully */ }
  )

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
  }

  LaunchedEffect(uiState.userMessage) {
    uiState.userMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearUserMessage()
    }
  }

  // Back handling when not on Home screen
  BackHandler(enabled = currentTab != 0) {
    currentTab = 0
  }

  val launchUnlockScreen = {
    val intent = Intent(context, UnlockAyahActivity::class.java).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      snackbarHost = { SnackbarHost(snackbarHostState) },
      bottomBar = {
        NavigationBar(
          modifier = Modifier.testTag("main_navigation_bar")
        ) {
          val tabs = listOf(AppTab.Home, AppTab.Surahs, AppTab.Progress, AppTab.Settings)
          tabs.forEach { tab ->
            NavigationBarItem(
              selected = currentTab == tab.index,
              onClick = { currentTab = tab.index },
              icon = { Icon(tab.icon, contentDescription = tab.title) },
              label = { Text(tab.title, fontSize = 11.sp) },
              modifier = Modifier.testTag("nav_tab_${tab.title.lowercase()}"),
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldPrimary,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
              )
            )
          }
        }
      }
    ) { innerPadding ->
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        when (currentTab) {
          0 -> HomeScreen(
            uiState = uiState,
            onLaunchTestUnlock = launchUnlockScreen,
            onSelectSurahAndAyah = { surah, ayah -> viewModel.selectSurahAndAyah(surah, ayah) },
            onUnlockEnabledChanged = { viewModel.setUnlockEnabled(it) },
            onCooldownChanged = { viewModel.setCooldownMinutes(it) },
            onRepeatGoalChanged = { viewModel.setRepeatCountGoal(it) },
            onFontSizeChanged = { viewModel.setArabicFontSize(it) },
            onReciterChanged = { viewModel.setSelectedReciter(it) },
            onAudioSpeedChanged = { viewModel.setAudioSpeed(it) },
            onAutoPlayChanged = { viewModel.setAutoPlay(it) },
            onVersesCountChanged = { viewModel.setVersesCount(it) },
            onVerseJumpStepChanged = { viewModel.setVerseJumpStep(it) },
            onNavigateToSurahs = { currentTab = 1 }
          )

          1 -> SurahListScreen(
            selectedSurahNumber = uiState.selectedSurahNumber,
            surahAyahs = uiState.selectedSurahAyahs,
            currentAyahId = uiState.settings.currentAyahId,
            isDownloading = uiState.isSurahDownloading,
            onSelectSurah = { viewModel.loadSelectedSurah(it) },
            onSelectAyahToMemorize = { viewModel.selectAyahToMemorize(it) },
            onToggleAudioForAyah = { viewModel.toggleAudioPlayback(it) },
            onDownloadSurah = { viewModel.downloadSurah(it) },
            playingAyahNumber = if (uiState.audioState.isPlaying) uiState.audioState.activeAyah else null
          )

          2 -> ProgressScreen(
            completedCount = uiState.completedCount,
            totalRepetitions = uiState.totalRepetitions,
            allAyahs = allAyahs,
            onReviewAyah = { ayahId ->
              viewModel.selectAyahToMemorize(ayahId)
              currentTab = 0
            },
            onResetProgress = { viewModel.resetAllProgress() }
          )

          3 -> SettingsScreen(
            settings = uiState.settings,
            cacheSizeFormatted = uiState.cacheSizeFormatted,
            cachedCount = uiState.cachedAyahsCount,
            onUnlockEnabledChanged = { viewModel.setUnlockEnabled(it) },
            onCooldownChanged = { viewModel.setCooldownMinutes(it) },
            onReciterChanged = { viewModel.setSelectedReciter(it) },
            onFontSizeChanged = { viewModel.setArabicFontSize(it) },
            onShowTranslationChanged = { viewModel.setShowTranslation(it) },
            onShowTransliterationChanged = { viewModel.setShowTransliteration(it) },
            onAutoPlayChanged = { viewModel.setAutoPlay(it) },
            onRepeatGoalChanged = { viewModel.setRepeatCountGoal(it) },
            onVersesCountChanged = { viewModel.setVersesCount(it) },
            onVerseJumpStepChanged = { viewModel.setVerseJumpStep(it) },
            onClearCache = { viewModel.clearAudioCache() },
            onRefreshCache = { viewModel.refreshCacheInfo() },
            onLaunchTestUnlock = launchUnlockScreen
          )
        }
      }
    }
  }
}
