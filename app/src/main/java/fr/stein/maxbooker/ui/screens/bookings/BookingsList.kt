package fr.stein.maxbooker.ui.screens.bookings

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import fr.stein.maxbooker.R
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsList(
    sncfReservations: List<SncfReservation>,
    onReservationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(),
                title = { Text(stringResource(R.string.app_destinations_bookings)) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            ).fillMaxWidth().fillMaxHeight()
        ) {
            items(sncfReservations, key = {
                it.dvNumber
            }) { sncfReservation ->
                BookingsListItem(sncfReservation, onReservationClick = {
                    onReservationClick(sncfReservation.dvNumber)
                })
            }
        }
    }
}
