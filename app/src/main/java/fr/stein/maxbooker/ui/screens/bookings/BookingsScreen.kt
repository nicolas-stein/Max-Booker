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
fun BookingsScreen(initialTrainNumber: String? = null, initialDvNumber: String? = null, viewModel: BookingsViewModel = hiltViewModel<BookingsViewModel>()) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()

    LaunchedEffect(uiState.selectedDvNumber, uiState.selectedTrainNumber) {
        if (uiState.selectedDvNumber != null && uiState.selectedTrainNumber != null) {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail, "${uiState.selectedDvNumber}-${uiState.selectedTrainNumber}")
        } else {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.List, null)
        }
    }

    LaunchedEffect(listDetailNavigator.currentDestination) {
        if (listDetailNavigator.currentDestination?.pane == ListDetailPaneScaffoldRole.List) {
            viewModel.selectReservation(null, null)
        }
    }

    LaunchedEffect(initialDvNumber, initialTrainNumber) {
        if (initialDvNumber != null && initialTrainNumber != null) {
            viewModel.selectReservation(initialDvNumber, initialTrainNumber)
        }
    }

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            BookingsList(
                sncfReservations = uiState.sncfReservations,
                onReservationClick = { dvNumber, trainNumber -> viewModel.selectReservation(dvNumber, trainNumber) }
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
