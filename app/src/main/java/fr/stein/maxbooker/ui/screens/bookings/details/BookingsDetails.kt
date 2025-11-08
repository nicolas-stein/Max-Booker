package fr.stein.maxbooker.ui.screens.bookings.details

import android.content.Intent
import android.icu.text.MessageFormat
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.stein.maxbooker.R
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfStation
import fr.stein.maxbooker.domain.utils.StringUtils
import fr.stein.maxbooker.domain.utils.UiUtils
import fr.stein.maxbooker.ui.theme.MaxBookerTheme
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsDetails(sncfReservation: SncfReservation?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    if (sncfReservation == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.screen_book_none_selected))
        }
        return
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(),
                title = {
                    Text(
                        stringResource(
                            R.string.screen_book_list_item_trip_to,
                            sncfReservation.destination.label
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
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
            HorizontalDivider()
            Column(
                modifier = Modifier.padding(horizontal = 4.dp).padding(bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
            HorizontalDivider()
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val textStyle = MaterialTheme.typography.bodyMedium
                val iconSize = with(LocalDensity.current) { textStyle.fontSize.toDp() }
                val duration = Duration.between(sncfReservation.departureDateTime, sncfReservation.arrivalDateTime)

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Travel duration icon",
                    modifier = Modifier.size(iconSize)
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    style = textStyle,
                    text =
                    (if (duration.toHours() > 0) "${duration.toHours()}h" else "") +
                        "${
                            duration.minusHours(
                                duration.toHours()
                            ).toMinutes()
                        } - ${sncfReservation.transportationServiceOffer}"
                )
            }
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = "${sncfReservation.transportationServiceOffer} n°${sncfReservation.trainNumber}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(
                            R.string.screen_book_detail_travel_class,
                            MessageFormat("{0,ordinal}").format(arrayOf(sncfReservation.travelClass.toInt()))
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                HorizontalDivider()
                Column(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val textStyle = MaterialTheme.typography.bodyMedium
                        val iconSize = with(LocalDensity.current) { textStyle.fontSize.toDp() }

                        Icon(
                            imageVector = Icons.Default.Train,
                            contentDescription = "Train icon",
                            modifier = Modifier.size(iconSize)
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            style = textStyle,
                            text = stringResource(R.string.screen_book_detail_coach_number, sncfReservation.coachNumber)
                        )
                        Text(modifier = Modifier.padding(horizontal = 4.dp), text = "-")
                        Icon(
                            imageVector = Icons.Default.AirlineSeatReclineNormal,
                            contentDescription = "Seat icon",
                            modifier = Modifier.size(iconSize)
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            style = textStyle,
                            text = stringResource(R.string.screen_book_detail_seat_number, sncfReservation.seatNumber)
                        )
                    }
                    if (sncfReservation.seat != null) {
                        Text(
                            text = "${StringUtils.getScreenBookDetailSeatString(
                                sncfReservation.seat!!.seatPosition
                            )} - ${
                                StringUtils.getScreenBookDetailSeatString(
                                    sncfReservation.seat!!.spaceType
                                )
                            } - ${StringUtils.getScreenBookDetailSeatString(sncfReservation.seat!!.tgvDeck)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = if (sncfReservation.seat!!.facingForward) {
                                stringResource(R.string.screen_book_detail_seat_facing_forward)
                            } else {
                                stringResource(
                                    R.string.screen_book_detail_seat_not_facing_forward
                                )
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.screen_book_detail_reference)
                    )
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        text = sncfReservation.dvNumber
                    )
                }
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.screen_book_detail_associated_name)
                    )
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        text = sncfReservation.customerLastName.uppercase()
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.screen_book_detail_price)
                    )
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        text = sncfReservation.amount ?: ""
                    )
                }
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                "https://www.sncf-connect.com/redirect?redirection_type=TRIP_IMPORT&prex=nl_fr_conv_auto-cdv-cdv&pnrRef=${sncfReservation.dvNumber}&name=${sncfReservation.customerLastName.uppercase()}&rfrr=CDV_blocpreview_accesmonvoyage"
                            )
                        )
                    )
                }
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = stringResource(R.string.screen_book_detail_open_in_sncf_connect)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Open in SNCF Connect icon"
                    )
                }
            }
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
                customerLastName = "DUPONT",
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
