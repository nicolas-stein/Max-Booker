package fr.stein.maxbooker.ui.screens.bookings.details

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
import androidx.compose.ui.tooling.preview.Preview
import fr.stein.maxbooker.R
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfStation
import fr.stein.maxbooker.ui.theme.MaxBookerTheme
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsDetails(sncfReservation: SncfReservation?, modifier: Modifier = Modifier) {
    if (sncfReservation != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(),
                    title = { Text(stringResource(R.string.app_destinations_bookings)) }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(
                    "Booking ${sncfReservation.orderId} title",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "Booking ${sncfReservation.orderId} details",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.screen_book_none_selected))
        }
    }
}

@Preview
@Composable
private fun BookingsDetailsPreview() {
    MaxBookerTheme {
        BookingsDetails(
            sncfReservation = SncfReservation(
                arrivalDateTime = ZonedDateTime.now().plusDays(1),
                avantage = false,
                coachNumber = "11",
                departureDateTime = ZonedDateTime.now().plusDays(1).minusHours(3),
                destination = SncfStation(
                    label = "PARIS EST",
                    rrCode = "FRPST"
                ),
                dvNumber = "ABC123",
                orderId = "DEF456",
                origin = SncfStation(
                    label = "THIONVILLE",
                    rrCode = "FRXTH"
                ),
                reservationDate = ZonedDateTime.now().minusHours(1),
                seatNumber = "86",
                serviceItemId = "abcdef-12345-ghijkl-6789",
                trainNumber = "1234",
                travelClass = "2",
                travelConfirmed = "TOO_LATE_TO_CONFIRM",
                travelStatus = "VALIDE",
                amount = "0 EUR",
                exchangeable = false,
                refundable = true,
                seat = SncfReservation.Seat(
                    facingForward = false,
                    seatPosition = "AFEN",
                    spaceType = "ADOC",
                    tgvDeck = "ANVH"
                ),
                tcn = "0123456789",
                transportationServiceOffer = "TGV INOUI"
            )
        )
    }
}
