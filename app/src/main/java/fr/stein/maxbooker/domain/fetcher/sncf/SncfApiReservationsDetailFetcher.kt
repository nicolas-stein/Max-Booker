package fr.stein.maxbooker.domain.fetcher.sncf

import android.util.Log
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.mapper.toEntity
import fr.stein.maxbooker.domain.fetcher.DataState
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SncfApiReservationsDetailFetcher @Inject constructor(
    private val sncfApiRepository: SncfApiRepository,
    private val sncfReservationDao: SncfReservationDao
) {
    private val _reservationsState =
        MutableStateFlow<MutableMap<String, DataState<SncfReservation>>>(mutableMapOf())
    val reservationsState = _reservationsState.asStateFlow()

    suspend fun fetchReservationDetail(customer: SncfCustomer, sncfReservation: SncfReservation) {
        if (_reservationsState.value[sncfReservation.orderId] is DataState.Loading) return

        _reservationsState.value[sncfReservation.orderId] = DataState.Loading
        Log.d("Max Book", "SncfApiReservationsFetcher: fetching reservations...")

        runCatching {
            val sncfReservationDetailed = sncfApiRepository.getTravel(customer, sncfReservation)
            Log.d(
                "Max Book",
                "SncfApiReservationsFetcher: received detailed sncf reservation from API (${sncfReservationDetailed.orderId})"
            )

            return@runCatching sncfReservationDetailed
        }.onSuccess { sncfReservationDetailed ->
            runCatching {
                sncfReservationDao.upsertReservation(sncfReservationDetailed.toEntity())
                return@runCatching sncfReservationDetailed
            }.onSuccess { result ->
                _reservationsState.value[sncfReservation.orderId] = DataState.Success(result)
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
                    _reservationsState.value[sncfReservation.orderId] =
                        DataState.Offline(null)
                else ->
                    _reservationsState.value[sncfReservation.orderId] =
                        DataState.Error(SncfApiException.UnexpectedException(throwable))
            }
            Log.e("Max Book", "SncfApiReservationsFetcher: error fetching reservations", throwable)
        }
    }
}
