package fr.stein.maxbooker.ui.screens.bookings

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    viewModel: BookingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()

    LaunchedEffect(uiState.selectedBookingId) {
        Log.i("Max Book", "BookingsScreen: ${listDetailNavigator.currentDestination?.pane}")
        val selectedId = uiState.selectedBookingId
        if (selectedId != null) {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        } else {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.List)
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
