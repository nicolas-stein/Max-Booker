package fr.stein.maxbooker.ui.screens.bookings

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.stein.maxbooker.ui.screens.bookings.details.BookingsDetails
import fr.stein.maxbooker.ui.theme.MaxBookerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookingsScreen(initialOrderId: String? = null, viewModel: BookingsViewModel = hiltViewModel<BookingsViewModel>()) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()

    LaunchedEffect(uiState.selectedOrderId) {
        val selectedId = uiState.selectedOrderId
        if (selectedId != null) {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail, selectedId)
        } else {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.List, selectedId)
        }
    }

    LaunchedEffect(listDetailNavigator.currentDestination) {
        if (listDetailNavigator.currentDestination?.pane == ListDetailPaneScaffoldRole.List) {
            viewModel.selectReservation(null)
        }
    }

    LaunchedEffect(initialOrderId) {
        if (initialOrderId != null) {
            viewModel.selectReservation(initialOrderId)
        }
    }

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            BookingsList(
                sncfReservations = uiState.sncfReservations,
                onReservationClick = { orderId -> viewModel.selectReservation(orderId) }
            )
        },
        detailPane = {
            BookingsDetails(
                sncfReservation = uiState.selectedSncfReservation,
                onDelete = { sncfReservation ->
                    scope.launch {
                        sncfReservation?.let { viewModel.deleteReservation(sncfReservation) }
                        if (listDetailNavigator.canNavigateBack()) {
                            listDetailNavigator.navigateBack()
                        }
                    }
                }
            )
        }
    )
}

@Preview
@Composable
private fun BookingsScreenPreview() {
    MaxBookerTheme {
        BookingsScreen()
    }
}
