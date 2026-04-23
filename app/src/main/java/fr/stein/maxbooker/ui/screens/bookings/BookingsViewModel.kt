package fr.stein.maxbooker.ui.screens.bookings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toEntity
import fr.stein.maxbooker.domain.fetcher.DataState
import fr.stein.maxbooker.domain.fetcher.sncf.SncfApiReservationsFetcher
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.utils.WorkerScheduler
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingsUiState(
    val sncfReservationsFetcherState: DataState<List<SncfReservation>> = DataState.Offline(null),
    val sncfReservations: List<SncfReservation> = emptyList(),
    val selectedDvNumber: String? = null
) {
    val selectedSncfReservation: SncfReservation?
        get() = sncfReservations.find { it.dvNumber == selectedDvNumber }
}

@HiltViewModel
class BookingsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    sncfApiReservationsFetcher: SncfApiReservationsFetcher,
    private val sncfReservationDao: SncfReservationDao,
    private val workerScheduler: WorkerScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsUiState())
    val uiState: StateFlow<BookingsUiState> = _uiState.asStateFlow()
    private val workManager = WorkManager.getInstance(context)

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

    fun selectReservation(dvNumber: String?) {
        _uiState.update { it.copy(selectedDvNumber = dvNumber) }
    }

    fun deleteReservation(sncfReservation: SncfReservation) {
        workerScheduler.cancelSncfReservationConfirmWorker(workManager, sncfReservation)
        viewModelScope.launch { sncfReservationDao.deleteReservation(sncfReservation.toEntity()) }
    }
}
