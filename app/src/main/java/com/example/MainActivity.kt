package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DownloadRecord
import com.example.model.PortalApp
import com.example.model.PortalTab
import com.example.ui.PortalViewModel
import com.example.ui.components.AppIconBox
import com.example.ui.components.DownloadPillButton
import com.example.ui.screens.AppDetailScreen
import com.example.ui.screens.StoreHomeScreen
import com.example.ui.theme.ASPlusStoreTheme
import com.example.ui.theme.StoreSuccessGreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ASPlusStoreTheme {
        ASPlusStoreApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ASPlusStoreApp(viewModel: PortalViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  var showProfileDialog by remember { mutableStateOf(false) }

  val handleDownload: (PortalApp) -> Unit = { app ->
    viewModel.triggerDownload(app)
    val opened = launchDownloadUrl(context, app.downloadUrl)
    scope.launch {
      val sizeSuffix = if (app.sizeLabel.isNotEmpty()) " (${app.sizeLabel})" else ""
      val msg =
        when {
          app.isPlayStore && opened -> "Opening ${app.name} on Google Play..."
          opened -> "Downloading ${app.name}$sizeSuffix..."
          else -> "Preparing ${app.name}$sizeSuffix..."
        }
      snackbarHostState.showSnackbar(msg)
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val useNavRail = maxWidth >= 840.dp

      Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
          if (!useNavRail) {
            StoreBottomBar(
              activeTab = uiState.activeTab,
              onSelectTab = viewModel::selectTab,
            )
          }
        },
      ) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          if (useNavRail) {
            StoreNavRail(
              activeTab = uiState.activeTab,
              onSelectTab = viewModel::selectTab,
            )
          }
          StoreHomeScreen(
            uiState = uiState,
            onSearchQueryChange = viewModel::updateSearchQuery,
            onOpenAppDetail = viewModel::openAppDetail,
            onDownloadApp = handleDownload,
            onOpenDownloads = { viewModel.setShowDownloadsSheet(true) },
            onOpenNotifications = { viewModel.setShowNotificationsSheet(true) },
            onOpenProfile = { showProfileDialog = true },
            modifier = Modifier.weight(1f),
          )
        }
      }
    }

    // Full-screen App Detail Overlay
    AnimatedVisibility(
      visible = uiState.selectedApp != null,
      enter = slideInVertically(initialOffsetY = { it / 6 }) + fadeIn(),
      exit = slideOutVertically(targetOffsetY = { it / 6 }) + fadeOut(),
    ) {
      uiState.selectedApp?.let { app ->
        AppDetailScreen(
          app = app,
          activeDownloads = uiState.activeDownloads,
          completedDownloads = uiState.completedDownloads,
          onBack = viewModel::closeAppDetail,
          onDownload = handleDownload,
          onOpenDownloads = { viewModel.setShowDownloadsSheet(true) },
          onShowMessage = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } },
        )
      }
    }

    if (uiState.showDownloadsSheet) {
      ModalBottomSheet(onDismissRequest = { viewModel.setShowDownloadsSheet(false) }) {
        DownloadsSheetContent(records = uiState.downloadRecords)
      }
    }

    if (uiState.showNotificationsSheet) {
      ModalBottomSheet(onDismissRequest = { viewModel.setShowNotificationsSheet(false) }) {
        NotificationsSheetContent(
          recentApps = uiState.recentNewApps,
          nowMillis = uiState.currentTimeMillis,
          onOpenApp = { app ->
            viewModel.setShowNotificationsSheet(false)
            viewModel.openAppDetail(app)
          },
          onDownloadApp = handleDownload,
        )
      }
    }

    if (showProfileDialog) {
      AlertDialog(
        onDismissRequest = { showProfileDialog = false },
        title = { Text("Muhammad Ansar") },
        text = { Text("AS Plus Limited \u2022 App Store Publisher Portal") },
        confirmButton = {
          TextButton(onClick = { showProfileDialog = false }) { Text("Close") }
        },
      )
    }
  }
}

@Composable
private fun StoreBottomBar(
  activeTab: PortalTab,
  onSelectTab: (PortalTab) -> Unit,
) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    windowInsets = WindowInsets.navigationBars,
  ) {
    NavigationBarItem(
      selected = activeTab == PortalTab.Apps,
      onClick = { onSelectTab(PortalTab.Apps) },
      icon = {
        Icon(
          imageVector =
            if (activeTab == PortalTab.Apps) Icons.Rounded.Apps else Icons.Outlined.Apps,
          contentDescription = null,
        )
      },
      label = {
        Text(
          text = stringResource(R.string.nav_apps),
          style = MaterialTheme.typography.labelMedium,
        )
      },
      colors =
        NavigationBarItemDefaults.colors(
          indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
          selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
          selectedTextColor = MaterialTheme.colorScheme.onSurface,
        ),
      modifier = Modifier.testTag("nav_apps_tab"),
    )

    NavigationBarItem(
      selected = activeTab == PortalTab.Search,
      onClick = { onSelectTab(PortalTab.Search) },
      icon = {
        Icon(
          imageVector =
            if (activeTab == PortalTab.Search) Icons.Rounded.Search else Icons.Outlined.Search,
          contentDescription = null,
        )
      },
      label = {
        Text(
          text = stringResource(R.string.nav_search),
          style = MaterialTheme.typography.labelMedium,
        )
      },
      colors =
        NavigationBarItemDefaults.colors(
          indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
          selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
          selectedTextColor = MaterialTheme.colorScheme.onSurface,
        ),
      modifier = Modifier.testTag("nav_search_tab"),
    )
  }
}

@Composable
private fun StoreNavRail(
  activeTab: PortalTab,
  onSelectTab: (PortalTab) -> Unit,
) {
  NavigationRail(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    modifier = Modifier.fillMaxHeight(),
  ) {
    Spacer(modifier = Modifier.height(16.dp))
    NavigationRailItem(
      selected = activeTab == PortalTab.Apps,
      onClick = { onSelectTab(PortalTab.Apps) },
      icon = {
        Icon(
          imageVector =
            if (activeTab == PortalTab.Apps) Icons.Rounded.Apps else Icons.Outlined.Apps,
          contentDescription = null,
        )
      },
      label = { Text(stringResource(R.string.nav_apps)) },
      colors =
        NavigationRailItemDefaults.colors(
          indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
          selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    )
    NavigationRailItem(
      selected = activeTab == PortalTab.Search,
      onClick = { onSelectTab(PortalTab.Search) },
      icon = {
        Icon(
          imageVector =
            if (activeTab == PortalTab.Search) Icons.Rounded.Search else Icons.Outlined.Search,
          contentDescription = null,
        )
      },
      label = { Text(stringResource(R.string.nav_search)) },
      colors =
        NavigationRailItemDefaults.colors(
          indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
          selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    )
  }
}

@Composable
private fun DownloadsSheetContent(records: List<DownloadRecord>) {
  Column(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
  ) {
    Text(
      text = stringResource(R.string.downloads_title),
      style = MaterialTheme.typography.titleLarge,
    )
    Spacer(modifier = Modifier.height(12.dp))
    if (records.isEmpty()) {
      Text(
        text = "No downloads yet. Tap Download on any app to start.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 24.dp),
      )
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(bottom = 24.dp),
      ) {
        items(items = records, key = { it.appId }) { record ->
          Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            AppIconBox(
              iconText = record.iconText,
              backgroundColor = record.color,
              size = 44.dp,
              cornerRadius = 10.dp,
              fontSize = 18.sp,
              iconResId = record.iconResId,
              contentDescription = "${record.appName} logo",
            )
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = record.appName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
              )
              Text(
                text =
                  if (record.sizeLabel.isNotEmpty()) "${record.version} \u2022 ${record.sizeLabel}"
                  else record.version,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Icon(
              imageVector =
                if (record.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.Download,
              contentDescription = null,
              tint =
                if (record.isCompleted) StoreSuccessGreen else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun NotificationsSheetContent(
  recentApps: List<PortalApp>,
  nowMillis: Long,
  onOpenApp: (PortalApp) -> Unit,
  onDownloadApp: (PortalApp) -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
  ) {
    Text(
      text = stringResource(R.string.notifications_title),
      style = MaterialTheme.typography.titleLarge,
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = "Newly added apps from the last 7 days",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(12.dp))

    if (recentApps.isEmpty()) {
      Text(
        text = "No new app notifications in the last 7 days.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 20.dp),
      )
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 24.dp),
      ) {
        items(items = recentApps, key = { it.id }) { app ->
          Column(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onOpenApp(app) }
                .padding(vertical = 6.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              AppIconBox(
                iconText = app.iconText,
                backgroundColor = app.color,
                size = 44.dp,
                cornerRadius = 10.dp,
                fontSize = 18.sp,
                iconResId = app.iconResId,
                contentDescription = "${app.name} logo",
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "New app added: ${app.name}",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.SemiBold,
                )
                Text(
                  text =
                    "${app.developer} \u2022 ${app.version} \u2022 ${formatRelativeAddedTime(app.addedAtEpochMillis, nowMillis)}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              DownloadPillButton(
                onClick = { onDownloadApp(app) },
                compact = true,
                testTag = "notif_download_${app.id}",
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
          }
        }
      }
    }
  }
}

private fun formatRelativeAddedTime(addedAtMillis: Long, nowMillis: Long): String {
  val diffHours = ((nowMillis - addedAtMillis) / (60L * 60L * 1000L)).coerceAtLeast(0L)
  if (diffHours < 1L) return "Added today"
  if (diffHours < 24L) return "Added ${diffHours}h ago"
  val diffDays = diffHours / 24L
  return if (diffDays == 1L) "Added 1 day ago" else "Added $diffDays days ago"
}

private fun launchDownloadUrl(context: Context, url: String): Boolean {
  if (url.isBlank()) return false
  return try {
    val intent =
      Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    context.startActivity(intent)
    true
  } catch (_: Exception) {
    false
  }
}
