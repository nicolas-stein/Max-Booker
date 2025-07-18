package fr.stein.maxbooker.ui.screens.bookings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.stein.maxbooker.ui.domain.model.Booking

@Composable
fun BookingsDetails(
    booking: Booking?,
    modifier: Modifier = Modifier
) {
    if (booking != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Booking ${booking.orderId} title", style = MaterialTheme.typography.headlineMedium)
            Text("Booking ${booking.orderId} details", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        // Show a placeholder when no item is selected
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Select a booking to see its details.")
        }
    }
}