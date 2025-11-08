package fr.stein.maxbooker.domain.fetcher.sncf

import android.util.Log
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toEntity
import fr.stein.maxbooker.domain.fetcher.DataState
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Singleton
class SncfApiReservationsFetcher @Inject constructor(
    private val sncfApiRepository: SncfApiRepository,
    sncfApiCustomerFetcher: SncfApiCustomerFetcher,
    private val sncfApiReservationsDetailFetcher: SncfApiReservationsDetailFetcher,
    private val sncfReservationDao: SncfReservationDao,
    private val applicationScope: CoroutineScope
) {
    private val _reservationsState =
        MutableStateFlow<DataState<List<SncfReservation>>>(DataState.Offline(null))
    val reservationsState = _reservationsState.asStateFlow()

    init {
        sncfApiCustomerFetcher.customerState
            .onEach { dataState ->
                if (dataState is DataState.Success) {
                    applicationScope.launch(Dispatchers.IO) {
                        fetchReservations(dataState.data)
                    }
                }
            }
            .launchIn(applicationScope)
    }

    private suspend fun fetchReservations(customer: SncfCustomer) {
        if (_reservationsState.value is DataState.Loading) return

        _reservationsState.value = DataState.Loading
        Log.d("Max Book", "SncfApiReservationsFetcher: fetching reservations...")

        runCatching {
            val sncfReservations = sncfApiRepository.getTravelConsultations(customer)
            Log.d(
                "Max Book",
                "SncfApiReservationsFetcher: received ${sncfReservations.size} reservations from API"
            )

            return@runCatching sncfReservations
        }.onSuccess { sncfReservations ->
            runCatching {
                if (sncfReservations.isEmpty()) {
                    _reservationsState.value = DataState.Success(emptyList())
                    return
                }

                val orderIds = sncfReservations.map { it.orderId }
                val savedSncfReservations = sncfReservationDao.getReservationsByIds(orderIds)
                    .map { it.toDomain() }
                    .associateBy { it.orderId }

                val sncfReservationsToUpsert = mutableListOf<SncfReservation>()

                for (reservation in sncfReservations) {
                    val savedReservation = savedSncfReservations[reservation.orderId]

                    if (savedReservation != null) {
                        reservation.updateDetails(
                            amount = savedReservation.amount,
                            exchangeable = savedReservation.exchangeable,
                            refundable = savedReservation.refundable,
                            seat = savedReservation.seat,
                            tcn = savedReservation.tcn,
                            transportationServiceOffer = savedReservation.transportationServiceOffer
                        )
                    } else {
                        sncfReservationDao.insertStationIfNotExists(reservation.origin.toEntity())
                        sncfReservationDao.insertStationIfNotExists(
                            reservation.destination.toEntity()
                        )
                    }

                    sncfReservationsToUpsert.add(reservation)
                }

                sncfReservationDao.upsertReservations(
                    sncfReservationsToUpsert.map {
                        it.toEntity()
                    }
                )
                return@runCatching sncfReservationsToUpsert.toList()
            }.onSuccess { result ->
                _reservationsState.value = DataState.Success(result)
                result.forEach { sncfReservation ->
                    sncfApiReservationsDetailFetcher.fetchReservationDetail(
                        customer,
                        sncfReservation
                    )
                }
            }.onFailure { throwable ->
                Log.e(
                    "Max Book",
                    "SncfApiReservationsFetcher: error saving reservations in app database",
                    throwable
                )
            }
        }.onFailure { throwable ->
            when (throwable) {
                is SncfApiException.NetworkException ->
                    _reservationsState.value =
                        DataState.Offline(null)
                else ->
                    _reservationsState.value =
                        DataState.Error(SncfApiException.UnexpectedException(throwable))
            }
            Log.e("Max Book", "SncfApiReservationsFetcher: error fetching reservations", throwable)
        }
    }
}
