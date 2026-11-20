package com.example

import com.example.model.PortalCatalog
import com.example.model.SEVEN_DAYS_MILLIS
import com.example.ui.PortalUiState
import com.example.ui.PortalViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun search_orders_matching_apps_first_and_keeps_other_apps_below() {
    val vm = PortalViewModel()
    vm.updateSearchQuery("Gmail")
    val partition = vm.uiState.value.searchPartition

    assertTrue(partition.hasQuery)
    assertEquals("Gmail", partition.matched.first().name)
    assertTrue(partition.others.any { it.name == "NICH Employee Attendance 2" })
    assertTrue(partition.matched.none { it.name == "NICH Employee Attendance 2" })
  }

  @Test
  fun notifications_expire_after_seven_days() {
    val nowAtPublish = 1795182300000L
    val stateRecent = PortalUiState(allApps = PortalCatalog.apps, currentTimeMillis = nowAtPublish)
    assertTrue(stateRecent.activeNotificationCount > 0)

    val afterSevenDays = nowAtPublish + SEVEN_DAYS_MILLIS + 1000L
    val stateExpired = PortalUiState(allApps = PortalCatalog.apps, currentTimeMillis = afterSevenDays)
    assertEquals(0, stateExpired.activeNotificationCount)
  }
}
