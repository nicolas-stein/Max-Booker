package fr.stein.maxbooker.ui.screens.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.stein.maxbooker.R
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfStation
import fr.stein.maxbooker.ui.theme.MaxBookerTheme
import fr.stein.maxbooker.ui.utils.UiUtils
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun BookingsListItem(sncfReservation: SncfReservation, onReservationClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp) // Space between cards
            .clickable { onReservationClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = stringResource(
                        R.string.screen_book_list_item_trip_to,
                        sncfReservation.destination.label
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val textStyle = MaterialTheme.typography.bodyMedium
                    val iconSize = with(LocalDensity.current) { textStyle.fontSize.toDp() }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Travel confirmed icon",
                        tint = Color(0xFF245221),
                        modifier = Modifier.size(iconSize),
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = stringResource(
                            R.string.screen_book_list_item_outward_on,
                            sncfReservation.departureDateTime.format(
                                DateTimeFormatter.ofLocalizedDate(
                                    FormatStyle.MEDIUM
                                )
                            )
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.surface
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 4.dp)) {
                Row {
                    Text(
                        text = UiUtils.formatTimeWithLeadingZeros(
                            sncfReservation.departureDateTime
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = sncfReservation.origin.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row {
                    Text(
                        text = UiUtils.formatTimeWithLeadingZeros(sncfReservation.arrivalDateTime),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = sncfReservation.destination.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            BookingsListItemStatusText(sncfReservation.travelConfirmed, sncfReservation.travelStatus)
        }
    }
}

@Composable
fun BookingsListItemStatusText(reservationTravelConfirm: String, reservationTravelStatus: String) {
    val textStyle = MaterialTheme.typography.bodyMedium
    val iconSize = with(LocalDensity.current) { textStyle.fontSize.toDp() }

    if (reservationTravelConfirm in arrayListOf("CONFIRMED", "TOO_LATE_TO_CONFIRM") && reservationTravelStatus == "VALIDE") {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(Color(0xFFCAECAF)).fillMaxWidth().padding(horizontal = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Travel confirmed icon",
                tint = Color(0xFF245221),
                modifier = Modifier.size(iconSize),
                )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                color = Color(0xFF245221),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.screen_book_list_item_booking_status_confirmed))
        }
    } else if(reservationTravelConfirm == "TO_BE_CONFIRMED" && reservationTravelStatus == "VALIDE") {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(Color(0xFFECCAAF)).fillMaxWidth().padding(horizontal = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Travel to be confirmed icon",
                tint = Color(0xFF883F03),
                modifier = Modifier.size(iconSize),
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                color = Color(0xFF883F03),
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.screen_book_list_item_booking_status_to_be_confirmed)
            )
        }
    } else if (reservationTravelConfirm == "" && reservationTravelStatus == "") { // TODO : update condition with correct values
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(Color(0xFFECAFAF)).fillMaxWidth().padding(horizontal = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Travel status error icon",
                tint = Color(0xFF880303),
                modifier = Modifier.size(iconSize),
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                color = Color(0xFF880303),
                style = MaterialTheme.typography.bodyMedium,
                text = "Error"
            )
        }
    } else {
        Text(
            modifier = Modifier.background(Color(0xFFABCAF0)).fillMaxWidth().padding(horizontal = 8.dp),
            color = Color(0xFF234673),
            style = MaterialTheme.typography.bodyMedium,
            text = stringResource(R.string.screen_book_list_item_booking_status_unknown, reservationTravelConfirm, reservationTravelStatus))
    }
}

@Preview
@Composable
fun BookingsListItemPreview() {
    MaxBookerTheme {
        BookingsListItem(
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
        ) { }
    }
}

@Preview(locale = "fr-rFR")
@Composable
fun BookingsListItemPreviewFr() {
    BookingsListItemPreview()
}
