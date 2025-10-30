package fr.stein.maxbooker.ui.screens.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.domain.fetcher.DataState
import fr.stein.maxbooker.domain.fetcher.sncf.SncfApiReservationsFetcher
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class BookingsUiState(
    val sncfReservationsFetcherState: DataState<List<SncfReservation>> = DataState.Offline(null),
    val sncfReservations: List<SncfReservation> = emptyList(),
    val selectedOrderId: String? = null
) {
    val selectedSncfReservation: SncfReservation?
        get() = sncfReservations.find { it.orderId == selectedOrderId }
}

@HiltViewModel
class BookingsViewModel @Inject constructor(
    sncfApiReservationsFetcher: SncfApiReservationsFetcher,
    sncfReservationDao: SncfReservationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsUiState())
    val uiState: StateFlow<BookingsUiState> = _uiState.asStateFlow()

    init {
        sncfApiReservationsFetcher.reservationsState.onEach { dataState ->
            _uiState.update { currentState ->
                currentState.copy(sncfReservationsFetcherState = dataState)
            }
        }.launchIn(viewModelScope)

        sncfReservationDao.observeAllReservations().onEach { sncfReservations ->
            _uiState.update { it.copy(sncfReservations = sncfReservations.map { it.toDomain() }) }
        }.launchIn(viewModelScope)
    }

    fun selectReservation(orderId: String?) {
        _uiState.update { it.copy(selectedOrderId = orderId) }
    }
}
