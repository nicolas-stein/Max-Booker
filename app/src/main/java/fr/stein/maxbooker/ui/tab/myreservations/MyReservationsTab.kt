package fr.stein.maxbooker.ui.tab.myreservations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import fr.stein.maxbooker.api.worker.SncfApiUserWorker
import fr.stein.maxbooker.database.MaxBookerDatabase
import fr.stein.maxbooker.database.reservations.SncfReservation
import fr.stein.maxbooker.database.reservations.SncfSeat
import fr.stein.maxbooker.database.reservations.SncfStation
import fr.stein.maxbooker.proto.AppSettings
import fr.stein.maxbooker.ui.tab.TabsViewModel
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyReservationsTab(tabsViewModel: TabsViewModel,
                      reservationsViewModel: MyReservationsViewModel = viewModel(),
                      appSettingsFlow: Flow<AppSettings>,
                      workManager: WorkManager,
                      maxBookerDatabase: MaxBookerDatabase) {

    val reservationsUiState by reservationsViewModel.uiState.collectAsState()
    val reservationsDatabaseState by maxBookerDatabase.sncfReservationDao().getAllObservable().observeAsState(
        initial = emptyList()
    )
    val stationsDatabaseState by maxBookerDatabase.sncfStationDao().getAllObservable().observeAsState(
        initial = emptyList()
    )
    val appSettings = appSettingsFlow.collectAsState(initial = AppSettings.getDefaultInstance())

    val pullRefreshState = rememberPullRefreshState(
        refreshing = reservationsUiState.isLoadingReservations,
        onRefresh = {
            workManager.enqueueUniqueWork(SncfApiUserWorker.WORK_NAME, ExistingWorkPolicy.KEEP, tabsViewModel.sncfApiUserWorkRequest)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn (
            modifier = Modifier.fillMaxSize(),
        ) {
            val stationsMap = stationsDatabaseState.associateBy { it.rrcode }
            val reservationsToShow = if (appSettings.value.myReservationsSettings.showPastReservations) {
                val maxDepartureDateTime = LocalDateTime.now().minus(Duration.ofSeconds(appSettings.value.myReservationsSettings.pastReservationsDuration))
                reservationsDatabaseState.filter { it.departureDateTime.isAfter(maxDepartureDateTime) }
            } else {
                reservationsDatabaseState.filter { it.departureDateTime.isAfter(LocalDateTime.now()) }
            }
            items(reservationsToShow) { reservation: SncfReservation ->
                ReservationCard(reservation, stationsMap)
            }
        }

        PullRefreshIndicator(
            refreshing = reservationsUiState.isLoadingReservations,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun ReservationCard(reservation: SncfReservation, stationsMap: Map<String, SncfStation>) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.weight(1F)) {
                Text(text = reservation.departureDateTime
                    .format(DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.FULL))
                    .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Text(text = "", style = MaterialTheme.typography.bodySmall)
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text(text = reservation.departureDateTime
                        .format(DateTimeFormatter
                            .ofLocalizedTime(FormatStyle.SHORT))
                        .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.size(8.dp, 0.dp))
                    Text(text = stationsMap[reservation.origin]!!.label,
                        style = MaterialTheme.typography.bodyMedium)
                }
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text(text = reservation.arrivalDateTime
                        .format(DateTimeFormatter
                            .ofLocalizedTime(FormatStyle.SHORT))
                        .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.size(8.dp, 0.dp))
                    Text(text = stationsMap[reservation.destination]!!.label,
                        style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.size(0.dp, 8.dp))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Dossier : ",
                        style = MaterialTheme.typography.bodyMedium)
                    Text(text = reservation.dvNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1F)) {
                val transportationServiceOffer =
                    if (reservation.transportationServiceOffer != null) {
                        reservation.transportationServiceOffer!!
                    } else { "Train" }

                Text(text = "$transportationServiceOffer N°${reservation.trainNumber}",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)

                Text(text = "Classe : ${reservation.travelClass}",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.size(0.dp, 8.dp))
                Text(text = "Voiture ${reservation.coachNumber} - Place ${reservation.seatNumber}",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold)
                if (reservation.seat != null) {
                    val seat = reservation.seat!!
                    Text(text = "${seat.seatPositionToStr()} - ${seat.spaceTypeToStr()} - ${seat.tgvDeckToStr()}",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

}

@Preview
@Composable
private fun ReservationCardPreview() {
    ReservationCard(SncfReservation(
        orderId = "OID1234",
        dvNumber = "DV1234",
        origin = "OR1234",
        destination = "DST1234",
        departureDateTime = LocalDateTime.parse("2024-04-14T16:10:00.000"),
        arrivalDateTime = LocalDateTime.parse("2024-04-14T17:10:00.000"),
        travelClass = "2",
        trainNumber = "2872",
        coachNumber = "16",
        seatNumber = "111",
        reservationDate = "2024-03-14T12:00:00.000",
        travelConfirmed = "TOO_EARLY_TO_CONFIRM",
        travelStatus = "VALIDE",
        serviceItemId = "/refOrders/orders/S4L5NC/serviceItems/c3f59dca-06a0-45cf-a8c0-113557468069",
        transportationServiceOffer = "TGV INOUI",
        tcn = "123456789",
        seat = SncfSeat(facingForward = true, seatPosition = "AFEN", spaceType = "ACAR", tgvDeck = "ANVH")

    ), mapOf("OR1234" to SncfStation("OR1234", "Origin station"),
        "DST1234" to SncfStation("DST1234", "Desintation station"))
    )
}