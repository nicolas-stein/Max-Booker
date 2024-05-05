package fr.stein.maxbooker.ui.tab.myreservations

import fr.stein.maxbooker.api.data.SncfApiReservation

data class MyReservationsUiState(
    val isLoadingReservations: Boolean = false,
    val reservations: List<SncfApiReservation> = emptyList()
)