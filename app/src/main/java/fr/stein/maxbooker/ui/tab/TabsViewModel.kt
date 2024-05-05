package fr.stein.maxbooker.ui.tab

import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import fr.stein.maxbooker.api.worker.SncfApiAuthTokenWorker
import fr.stein.maxbooker.api.worker.SncfApiUserWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TabsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TabsUiState())
    val uiState: StateFlow<TabsUiState> = _uiState.asStateFlow()

    val sncfApiUserWorkRequest = OneTimeWorkRequestBuilder<SncfApiUserWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).build()
    val sncfApiAuthTokenWorkRequest = OneTimeWorkRequestBuilder<SncfApiAuthTokenWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).build()
}