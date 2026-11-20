package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PortalApp
import com.example.ui.components.AgeRatingBadge
import com.example.ui.components.AppIconBox
import com.example.ui.components.DownloadBadgeButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppDetailScreen(
  app: PortalApp,
  activeDownloads: Int,
  completedDownloads: Int,
  onBack: () -> Unit,
  onDownload: (PortalApp) -> Unit,
  onOpenDownloads: () -> Unit,
  onShowMessage: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBack)

  var menuExpanded by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  Column(
    modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Sticky Detail Top Bar
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .background(MaterialTheme.colorScheme.background)
          .statusBarsPadding()
          .padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier.minimumInteractiveComponentSize().testTag("detail_back_button"),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
          contentDescription = stringResource(R.string.cd_back),
          tint = MaterialTheme.colorScheme.onBackground,
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        DownloadBadgeButton(
          activeDownloads = activeDownloads,
          completedDownloads = completedDownloads,
          onClick = onOpenDownloads,
          testTag = "detail_download_status_button",
        )

        Box {
          IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("detail_more_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.MoreVert,
              contentDescription = stringResource(R.string.cd_more),
              tint = MaterialTheme.colorScheme.onBackground,
            )
          }

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
          ) {
            DropdownMenuItem(
              text = { Text("Share app") },
              onClick = {
                menuExpanded = false
                val shareIntent =
                  Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                      Intent.EXTRA_TEXT,
                      "${app.name} (${app.version}) - ${app.downloadUrl}",
                    )
                  }
                context.startActivity(Intent.createChooser(shareIntent, "Share ${app.name}"))
              },
            )
            DropdownMenuItem(
              text = { Text("Copy download link") },
              onClick = {
                menuExpanded = false
                clipboardManager.setText(AnnotatedString(app.downloadUrl))
                onShowMessage("Copied ${app.name} download link")
              },
            )
          }
        }
      }
    }

    // Detail Body
    Column(
      modifier =
        Modifier.fillMaxSize()
          .widthIn(max = 780.dp)
          .verticalScroll(rememberScrollState())
          .navigationBarsPadding()
          .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 28.dp),
    ) {
      // Hero
      Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        AppIconBox(
          iconText = app.iconText,
          backgroundColor = app.color,
          size = 84.dp,
          cornerRadius = 20.dp,
          fontSize = 32.sp,
          iconResId = app.iconResId,
          contentDescription = "${app.name} logo",
        )

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = app.name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = app.developer,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text =
              "${if (app.isPlayStore) "Official Google Play" else "Direct APK"} \u2022 ${app.version}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 3-Column Metrics Strip (no random/placeholder MB)
      Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(
          modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          if (app.rating.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = app.rating,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
              )
              Spacer(modifier = Modifier.width(3.dp))
              Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(14.dp),
              )
            }
          } else {
            Text(
              text = app.version,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground,
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text =
              app.reviews.ifEmpty { if (app.isPlayStore) "Official Listing" else "Version" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        VerticalDivider(
          modifier = Modifier.height(34.dp),
          color = MaterialTheme.colorScheme.outlineVariant,
        )

        Column(
          modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          AgeRatingBadge(ageRating = app.ageRating)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Rated for ${app.ageRating}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        VerticalDivider(
          modifier = Modifier.height(34.dp),
          color = MaterialTheme.colorScheme.outlineVariant,
        )

        Column(
          modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
            text =
              app.sizeLabel.ifEmpty { if (app.isPlayStore) "Play Store" else "Direct APK" },
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text =
              if (app.sizeLabel.isNotEmpty()) stringResource(R.string.apk_size_label)
              else if (app.isPlayStore) "Varies by device"
              else "Exact size not set",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Full-Width Download Button
      Button(
        onClick = { onDownload(app) },
        shape = CircleShape,
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
        contentPadding = PaddingValues(vertical = 13.dp),
        modifier =
          Modifier.fillMaxWidth()
            .minimumInteractiveComponentSize()
            .testTag("detail_download_button"),
      ) {
        Text(
          text = stringResource(R.string.action_download),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Promo / Preview Banner
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .shadow(
              elevation = 4.dp,
              shape = RoundedCornerShape(16.dp),
              ambientColor = Color(0xFF0B57D0).copy(alpha = 0.25f),
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(Color(0xFF0B57D0), Color(0xFF1A73E8), Color(0xFF00838F))
              )
            )
            .padding(20.dp)
      ) {
        Column {
          Text(
            text = app.promoTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = app.promoText,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.95f),
          )
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      if (app.screenshotResIds.isNotEmpty()) {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        ) {
          items(app.screenshotResIds) { resId ->
            Image(
              painter = painterResource(id = resId),
              contentDescription = "${app.name} screenshot",
              contentScale = ContentScale.Crop,
              modifier =
                Modifier.height(220.dp)
                  .width(124.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(14.dp),
                  ),
            )
          }
        }
      }

      // About Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = stringResource(R.string.about_this_app),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onBackground,
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = app.about,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Meta Chips
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        MetaChip(text = app.category)
        MetaChip(
          text = if (app.isPlayStore) "Google Play Store" else "Version ${app.version}",
        )
        if (app.sizeLabel.isNotEmpty()) {
          MetaChip(text = app.sizeLabel)
        }
      }
    }
  }
}

@Composable
private fun MetaChip(text: String) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    modifier =
      Modifier.border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
        shape = RoundedCornerShape(8.dp),
      ),
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
    )
  }
}
