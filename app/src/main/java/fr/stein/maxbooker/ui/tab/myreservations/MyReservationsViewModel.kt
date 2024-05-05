package fr.stein.maxbooker.ui.tab.myreservations

import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import fr.stein.maxbooker.api.worker.SncfApiReservationsWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyReservationsViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(MyReservationsUiState())
    val uiState: StateFlow<MyReservationsUiState> = _uiState.asStateFlow()

    val sncfApiReservationsWorkRequest = OneTimeWorkRequestBuilder<SncfApiReservationsWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).build()

    fun setRefreshingReservations(isLoadingReservations: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isLoadingReservations = isLoadingReservations)
        }
    }
}