package fr.stein.maxbooker.ui.screens.bookings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fr.stein.maxbooker.R
import fr.stein.maxbooker.ui.domain.model.Booking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsDetails(
    booking: Booking?,
    modifier: Modifier = Modifier
) {
    if (booking != null) {
        Scaffold (
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                    title = { Text(stringResource(R.string.app_destinations_bookings)) }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text("Booking ${booking.orderId} title", style = MaterialTheme.typography.headlineMedium)
                Text("Booking ${booking.orderId} details", style = MaterialTheme.typography.bodyLarge)
            }
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Select a booking to see its details.")
        }
    }
}