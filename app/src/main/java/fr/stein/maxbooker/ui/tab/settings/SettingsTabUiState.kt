package fr.stein.maxbooker.ui.tab.settings

import java.time.Duration

data class SettingsTabUiState(
    val showPastReservations: Boolean = false,
    val pastReservationsDuration: Duration = Duration.ofDays(7),
    val autoConfirmReservations: Boolean = false
)