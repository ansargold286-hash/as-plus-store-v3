package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.DownloadRecord
import com.example.model.PortalApp
import com.example.model.PortalCatalog
import com.example.model.PortalTab
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchPartition(
  val matched: List<PortalApp>,
  val others: List<PortalApp>,
  val hasQuery: Boolean,
)

data class PortalUiState(
  val allApps: List<PortalApp> = PortalCatalog.apps,
  val activeTab: PortalTab = PortalTab.Apps,
  val searchQuery: String = "",
  val selectedApp: PortalApp? = null,
  val activeDownloads: Int = 0,
  val completedDownloads: Int = 0,
  val downloadRecords: List<DownloadRecord> = emptyList(),
  val showDownloadsSheet: Boolean = false,
  val showNotificationsSheet: Boolean = false,
  val focusSearchNonce: Int = 0,
  val currentTimeMillis: Long = System.currentTimeMillis(),
) {
  val recentNewApps: List<PortalApp>
    get() = allApps.filter { it.isRecent(currentTimeMillis) }

  val featuredApp: PortalApp
    get() = recentNewApps.firstOrNull { it.featured } ?: recentNewApps.firstOrNull() ?: allApps.first()

  val activeNotificationCount: Int
    get() = recentNewApps.size

  val searchPartition: SearchPartition
    get() {
      val q = searchQuery.trim().lowercase()
      if (q.isEmpty()) {
        return SearchPartition(matched = allApps, others = emptyList(), hasQuery = false)
      }
      val prefixName = mutableListOf<PortalApp>()
      val containsName = mutableListOf<PortalApp>()
      val metaMatch = mutableListOf<PortalApp>()
      val others = mutableListOf<PortalApp>()

      allApps.forEach { app ->
        val nameLower = app.name.lowercase()
        val devLower = app.developer.lowercase()
        val catLower = app.category.lowercase()
        val aboutLower = app.about.lowercase()

        when {
          nameLower.startsWith(q) -> prefixName.add(app)
          nameLower.contains(q) -> containsName.add(app)
          devLower.contains(q) || catLower.contains(q) || aboutLower.contains(q) ->
            metaMatch.add(app)
          else -> others.add(app)
        }
      }

      return SearchPartition(
        matched = prefixName + containsName + metaMatch,
        others = others,
        hasQuery = true,
      )
    }
}

class PortalViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(PortalUiState())
  val uiState: StateFlow<PortalUiState> = _uiState.asStateFlow()

  fun selectTab(tab: PortalTab) {
    _uiState.update { state ->
      state.copy(
        activeTab = tab,
        currentTimeMillis = System.currentTimeMillis(),
        focusSearchNonce =
          if (tab == PortalTab.Search) state.focusSearchNonce + 1 else state.focusSearchNonce,
      )
    }
  }

  fun updateSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun openAppDetail(app: PortalApp) {
    _uiState.update { it.copy(selectedApp = app) }
  }

  fun closeAppDetail() {
    _uiState.update { it.copy(selectedApp = null) }
  }

  fun triggerDownload(app: PortalApp) {
    val record =
      DownloadRecord(
        appId = app.id,
        appName = app.name,
        sizeLabel = app.sizeLabel,
        version = app.version,
        color = app.color,
        iconText = app.iconText,
        iconResId = app.iconResId,
        isCompleted = false,
      )
    _uiState.update { state ->
      state.copy(
        activeDownloads = state.activeDownloads + 1,
        downloadRecords = listOf(record) + state.downloadRecords.filterNot { it.appId == app.id },
      )
    }
    viewModelScope.launch {
      delay(1500L)
      _uiState.update { state ->
        state.copy(
          activeDownloads = (state.activeDownloads - 1).coerceAtLeast(0),
          completedDownloads = state.completedDownloads + 1,
          downloadRecords =
            state.downloadRecords.map { item ->
              if (item.appId == app.id) item.copy(isCompleted = true) else item
            },
        )
      }
    }
  }

  fun setShowDownloadsSheet(show: Boolean) {
    _uiState.update { it.copy(showDownloadsSheet = show) }
  }

  fun setShowNotificationsSheet(show: Boolean) {
    _uiState.update {
      it.copy(
        showNotificationsSheet = show,
        currentTimeMillis = System.currentTimeMillis(),
      )
    }
  }
}
