package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.PortalApp
import com.example.model.PortalTab
import com.example.ui.PortalUiState
import com.example.ui.components.AppRowCard
import com.example.ui.components.DownloadBadgeButton
import com.example.ui.components.NewAppsFeaturedCard
import com.example.ui.components.NotificationsBadgeButton
import com.example.ui.components.PlayTriangleBrand
import com.example.ui.components.UserAvatarButton

@Composable
fun StoreHomeScreen(
  uiState: PortalUiState,
  onSearchQueryChange: (String) -> Unit,
  onOpenAppDetail: (PortalApp) -> Unit,
  onDownloadApp: (PortalApp) -> Unit,
  onOpenDownloads: () -> Unit,
  onOpenNotifications: () -> Unit,
  onOpenProfile: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val searchFocusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current
  val searchPartition = uiState.searchPartition

  LaunchedEffect(uiState.focusSearchNonce) {
    if (uiState.focusSearchNonce > 0) {
      searchFocusRequester.requestFocus()
    }
  }

  Column(
    modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Sticky Top Store Bar
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
          .statusBarsPadding()
          .padding(horizontal = 16.dp, vertical = 10.dp),
      contentAlignment = Alignment.Center,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().widthIn(max = 920.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        PlayTriangleBrand()

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          DownloadBadgeButton(
            activeDownloads = uiState.activeDownloads,
            completedDownloads = uiState.completedDownloads,
            onClick = onOpenDownloads,
          )
          NotificationsBadgeButton(
            unreadCount = uiState.activeNotificationCount,
            onClick = onOpenNotifications,
          )
          UserAvatarButton(
            initial = "A",
            onClick = onOpenProfile,
          )
        }
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 920.dp),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      // Search Input
      item(key = "search_bar") {
        OutlinedTextField(
          value = uiState.searchQuery,
          onValueChange = onSearchQueryChange,
          placeholder = {
            Text(
              text = stringResource(R.string.search_placeholder),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Rounded.Search,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          },
          trailingIcon = {
            if (uiState.searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchQueryChange("") }) {
                Icon(
                  imageVector = Icons.Rounded.Close,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          },
          singleLine = true,
          shape = CircleShape,
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.background,
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
          modifier =
            Modifier.fillMaxWidth()
              .focusRequester(searchFocusRequester)
              .testTag("search_input_field"),
        )
      }

      // Top New Apps Auto-Scroll Section (only newly added apps within 7 days)
      if (
        uiState.activeTab == PortalTab.Apps &&
          uiState.searchQuery.isBlank() &&
          uiState.recentNewApps.isNotEmpty()
      ) {
        item(key = "featured_new_apps") {
          NewAppsFeaturedCard(
            recentApps = uiState.recentNewApps,
            featuredApp = uiState.featuredApp,
            onOpenDetail = onOpenAppDetail,
            onDownload = onDownloadApp,
            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp),
          )
        }
      }

      // Section Heading
      item(key = "section_heading") {
        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text =
              if (uiState.activeTab == PortalTab.Search || searchPartition.hasQuery) {
                stringResource(R.string.section_search_results)
              } else {
                stringResource(R.string.section_suggested)
              },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Text(
            text =
              if (searchPartition.hasQuery) {
                val count = searchPartition.matched.size
                "$count match${if (count == 1) "" else "es"}"
              } else {
                "${uiState.allApps.size} apps"
              },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // Matching apps first at the top
      if (!searchPartition.hasQuery) {
        items(items = uiState.allApps, key = { "all_${it.id}" }) { app ->
          AppRowCard(
            app = app,
            onOpenDetail = { onOpenAppDetail(app) },
            onDownload = { onDownloadApp(app) },
          )
        }
      } else {
        if (searchPartition.matched.isEmpty()) {
          item(key = "no_exact_match") {
            Text(
              text = "No exact matches for \"${uiState.searchQuery.trim()}\".",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(vertical = 4.dp),
            )
          }
        } else {
          items(items = searchPartition.matched, key = { "match_${it.id}" }) { app ->
            AppRowCard(
              app = app,
              onOpenDetail = { onOpenAppDetail(app) },
              onDownload = { onDownloadApp(app) },
            )
          }
        }

        if (searchPartition.others.isNotEmpty()) {
          item(key = "more_apps_divider") {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
              Text(
                text =
                  if (searchPartition.matched.isNotEmpty()) "More apps on AS Plus Store"
                  else "Browse apps from home",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
              )
            }
          }
          items(items = searchPartition.others, key = { "other_${it.id}" }) { app ->
            AppRowCard(
              app = app,
              onOpenDetail = { onOpenAppDetail(app) },
              onDownload = { onDownloadApp(app) },
            )
          }
        }
      }
    }
  }
}
