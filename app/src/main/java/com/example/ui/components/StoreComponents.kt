package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PortalApp
import com.example.ui.theme.StoreBadgeRed
import com.example.ui.theme.StoreSuccessGreen
import kotlinx.coroutines.isActive

@Composable
fun PlayTriangleBrand(modifier: Modifier = Modifier) {
  val triangleColor = MaterialTheme.colorScheme.primary
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Canvas(modifier = Modifier.size(width = 22.dp, height = 26.dp)) {
      val path =
        Path().apply {
          moveTo(0f, 0f)
          lineTo(size.width, size.height / 2f)
          lineTo(0f, size.height)
          close()
        }
      drawPath(path = path, color = triangleColor)
    }
    Text(
      text = stringResource(R.string.store_brand),
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.onBackground,
    )
  }
}

@Composable
fun DownloadBadgeButton(
  activeDownloads: Int,
  completedDownloads: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "download_status_button",
) {
  val isCompletedOnly = activeDownloads == 0 && completedDownloads > 0
  val displayCount =
    when {
      activeDownloads > 0 -> activeDownloads
      completedDownloads > 0 -> completedDownloads
      else -> 0
    }
  val iconVector = if (isCompletedOnly) Icons.Rounded.Check else Icons.Rounded.Download
  val iconTint =
    if (isCompletedOnly) StoreSuccessGreen else MaterialTheme.colorScheme.onBackground

  IconButton(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().testTag(testTag),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = iconVector,
        contentDescription = stringResource(R.string.cd_downloads),
        tint = iconTint,
        modifier = Modifier.size(24.dp),
      )
      CountBadge(
        count = displayCount,
        modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-5).dp),
      )
    }
  }
}

@Composable
fun NotificationsBadgeButton(
  unreadCount: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  IconButton(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().testTag("notifications_button"),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = Icons.Outlined.Notifications,
        contentDescription = stringResource(R.string.cd_notifications),
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(24.dp),
      )
      if (unreadCount > 0) {
        CountBadge(
          count = unreadCount,
          modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-5).dp),
        )
      }
    }
  }
}

@Composable
private fun CountBadge(count: Int, modifier: Modifier = Modifier) {
  Box(
    modifier =
      modifier
        .height(16.dp)
        .clip(CircleShape)
        .background(StoreBadgeRed)
        .padding(horizontal = 4.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = count.toString(),
      color = Color.White,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      lineHeight = 12.sp,
    )
  }
}

@Composable
fun UserAvatarButton(
  initial: String = "A",
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .clip(CircleShape)
        .clickable(onClick = onClick)
        .testTag("profile_avatar_button"),
    contentAlignment = Alignment.Center,
  ) {
    Box(
      modifier =
        Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = initial,
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
fun AppIconBox(
  iconText: String,
  backgroundColor: Color,
  size: Dp = 56.dp,
  cornerRadius: Dp = 14.dp,
  fontSize: TextUnit = 22.sp,
  @DrawableRes iconResId: Int? = null,
  contentDescription: String? = null,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .size(size)
        .shadow(elevation = 2.dp, shape = RoundedCornerShape(cornerRadius), clip = false)
        .clip(RoundedCornerShape(cornerRadius))
        .background(if (iconResId != null) Color.White else backgroundColor),
    contentAlignment = Alignment.Center,
  ) {
    if (iconResId != null) {
      Image(
        painter = painterResource(id = iconResId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
    } else {
      Text(
        text = iconText,
        color = Color.White,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
fun DownloadPillButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  compact: Boolean = false,
  testTag: String = "download_pill_button",
) {
  Button(
    onClick = onClick,
    shape = CircleShape,
    colors =
      ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ),
    contentPadding =
      if (compact) PaddingValues(horizontal = 8.dp, vertical = 4.dp)
      else PaddingValues(horizontal = 18.dp, vertical = 8.dp),
    modifier =
      if (compact) modifier.height(34.dp).testTag(testTag)
      else modifier.minimumInteractiveComponentSize().testTag(testTag),
  ) {
    Text(
      text = stringResource(R.string.action_download),
      style =
        if (compact) MaterialTheme.typography.labelMedium
        else MaterialTheme.typography.labelLarge,
      maxLines = 1,
    )
  }
}

@Composable
fun NewAppsFeaturedCard(
  recentApps: List<PortalApp>,
  featuredApp: PortalApp,
  onOpenDetail: (PortalApp) -> Unit,
  onDownload: (PortalApp) -> Unit,
  modifier: Modifier = Modifier,
) {
  val listState = rememberLazyListState()
  var direction by remember { mutableFloatStateOf(1f) }
  val density = LocalDensity.current
  val speedPxPerSec = remember(density) { with(density) { 28.dp.toPx() } }

  LaunchedEffect(recentApps.size) {
    if (recentApps.size <= 3) return@LaunchedEffect
    var lastNanos = 0L
    while (isActive) {
      withFrameNanos { frameNanos ->
        if (lastNanos == 0L) {
          lastNanos = frameNanos
          return@withFrameNanos
        }
        val dt = ((frameNanos - lastNanos) / 1_000_000_000f).coerceAtMost(0.064f)
        lastNanos = frameNanos
        if (!listState.isScrollInProgress) {
          val canForward = listState.canScrollForward
          val canBackward = listState.canScrollBackward
          if (direction > 0f && !canForward) direction = -1f
          else if (direction < 0f && !canBackward) direction = 1f
          listState.dispatchRawDelta(direction * speedPxPerSec * dt)
        }
      }
    }
  }

  val footerMetaParts = buildList {
    add(featuredApp.developer)
    if (featuredApp.version.isNotEmpty()) add(featuredApp.version)
    if (featuredApp.sizeLabel.isNotEmpty()) add(featuredApp.sizeLabel)
  }

  Surface(
    modifier = modifier.fillMaxWidth().testTag("featured_hero_card"),
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    shadowElevation = 2.dp,
    border =
      androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors =
                  listOf(
                    Color(0xFF2D1B69),
                    Color(0xFF6A1B9A),
                    Color(0xFF0B57D0),
                  )
              )
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              text = "New Apps",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White,
            )
            Box(
              modifier =
                Modifier.clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.18f))
                  .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${recentApps.size} newly added",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
              )
            }
          }

          BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val spacing = 8.dp
            val cardWidth = (maxWidth - spacing * 2) / 3
            LazyRow(
              state = listState,
              horizontalArrangement = Arrangement.spacedBy(spacing),
              modifier = Modifier.fillMaxWidth().testTag("new_apps_carousel"),
            ) {
              items(items = recentApps, key = { it.id }) { app ->
                Surface(
                  modifier =
                    Modifier.width(cardWidth)
                      .clip(RoundedCornerShape(14.dp))
                      .clickable { onOpenDetail(app) }
                      .testTag("new_app_slide_${app.id}"),
                  shape = RoundedCornerShape(14.dp),
                  color = Color.White,
                  shadowElevation = 2.dp,
                ) {
                  Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                  ) {
                    AppIconBox(
                      iconText = app.iconText,
                      backgroundColor = app.color,
                      size = 48.dp,
                      cornerRadius = 12.dp,
                      fontSize = 18.sp,
                      iconResId = app.iconResId,
                      contentDescription = "${app.name} logo",
                    )
                    Text(
                      text = app.name,
                      style = MaterialTheme.typography.labelMedium,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF1F1F1F),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      textAlign = TextAlign.Center,
                      modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                      text = if (app.isPlayStore) "Google Play" else app.version,
                      style = MaterialTheme.typography.bodySmall,
                      fontSize = 11.sp,
                      color = Color(0xFF5F6368),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      textAlign = TextAlign.Center,
                      modifier = Modifier.fillMaxWidth(),
                    )
                    DownloadPillButton(
                      onClick = { onDownload(app) },
                      compact = true,
                      modifier = Modifier.fillMaxWidth(),
                      testTag = "slide_download_${app.id}",
                    )
                  }
                }
              }
            }
          }
        }
      }

      Row(
        modifier =
          Modifier.fillMaxWidth()
            .clickable { onOpenDetail(featuredApp) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        AppIconBox(
          iconText = featuredApp.iconText,
          backgroundColor = featuredApp.color,
          size = 56.dp,
          cornerRadius = 14.dp,
          fontSize = 22.sp,
          iconResId = featuredApp.iconResId,
          contentDescription = "${featuredApp.name} logo",
        )
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = featuredApp.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = footerMetaParts.joinToString(" \u2022 "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        DownloadPillButton(
          onClick = { onDownload(featuredApp) },
          testTag = "featured_download_button",
        )
      }
    }
  }
}

@Composable
fun AppRowCard(
  app: PortalApp,
  onOpenDetail: () -> Unit,
  onDownload: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .clickable(onClick = onOpenDetail)
        .padding(8.dp)
        .testTag("app_row_card_${app.id}"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    AppIconBox(
      iconText = app.iconText,
      backgroundColor = app.color,
      size = 56.dp,
      cornerRadius = 14.dp,
      fontSize = 22.sp,
      iconResId = app.iconResId,
      contentDescription = "${app.name} logo",
    )

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = app.name,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = app.category,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        if (app.rating.isNotEmpty()) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = app.rating,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
              imageVector = Icons.Rounded.Star,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(13.dp),
            )
          }
        }
        if (app.sizeLabel.isNotEmpty()) {
          Text(
            text = app.sizeLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
        Text(
          text = if (app.isPlayStore) "Google Play" else app.version,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    DownloadPillButton(
      onClick = onDownload,
      testTag = "row_download_button_${app.id}",
    )
  }
}

@Composable
fun AgeRatingBadge(ageRating: String, modifier: Modifier = Modifier) {
  Box(
    modifier =
      modifier
        .border(
          width = 1.5.dp,
          color = MaterialTheme.colorScheme.onSurface,
          shape = RoundedCornerShape(4.dp),
        )
        .padding(horizontal = 5.dp, vertical = 1.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = ageRating,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
    )
  }
}
