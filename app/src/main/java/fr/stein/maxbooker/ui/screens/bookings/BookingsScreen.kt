package fr.stein.maxbooker.ui.screens.bookings

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.stein.maxbooker.ui.theme.MaxBookerTheme

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookingsScreen(
    initialBookingId: String? = null,
    viewModel: BookingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()

    LaunchedEffect(uiState.selectedBookingId) {
        val selectedId = uiState.selectedBookingId
        if (selectedId != null) {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        } else {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.List)
        }
    }

    LaunchedEffect(listDetailNavigator.currentDestination) {
        if (listDetailNavigator.currentDestination?.pane == ListDetailPaneScaffoldRole.List) {
            viewModel.selectBooking(null)
        }
    }

    LaunchedEffect(initialBookingId) {
        if (initialBookingId != null) {
            viewModel.selectBooking(initialBookingId)
        }
    }

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = { BookingsList(
            bookings = uiState.bookings,
            onBookingClick = { bookingId -> viewModel.selectBooking(bookingId)}
        ) },
        detailPane = { BookingsDetails(booking = uiState.selectedBooking) }
    )
}

@Preview
@Composable
private fun BookingsScreenPreview() {
    MaxBookerTheme {
        BookingsScreen()
    }
}
