package fr.stein.maxbooker.ui.screens.settings.automations

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.stein.maxbooker.domain.model.maxbooker.MaxBookerSettingsAutomations
import fr.stein.maxbooker.domain.repository.sncf.MaxBookerSettingsRepository
import fr.stein.maxbooker.domain.utils.WorkerScheduler
import fr.stein.maxbooker.domain.work.SncfReservationsUpdateWorker
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsDetailsAutomationsUiState(
    val sncfReservationsUpdateWorkInfo: WorkInfo? = null,
    val maxBookerSettingsAutomations: MaxBookerSettingsAutomations? = null
)

@HiltViewModel
class SettingsDetailsAutomationsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val maxBookerSettingsRepository: MaxBookerSettingsRepository,
    private val workerScheduler: WorkerScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsDetailsAutomationsUiState())
    val uiState: StateFlow<SettingsDetailsAutomationsUiState> = _uiState.asStateFlow()

    init {
        val workManager = WorkManager.getInstance(context)

        viewModelScope.launch {
            workManager
                .getWorkInfosForUniqueWorkFlow(SncfReservationsUpdateWorker.WORKER_NAME)
                .collect { workInfos ->
                    if (workInfos.isNotEmpty()) {
                        _uiState.update {
                            it.copy(sncfReservationsUpdateWorkInfo = workInfos[0])
                        }
                    }
                }
        }

        viewModelScope.launch {
            maxBookerSettingsRepository.automations().flow().collect { automations ->
                _uiState.update {
                    it.copy(maxBookerSettingsAutomations = automations)
                }
            }
        }
    }

    fun handleRefreshBookingSwitch(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            maxBookerSettingsRepository.automations().setRefreshBookingsDisabled(!enabled)
            if (enabled) {
                workerScheduler.scheduleSncfReservationsUpdateWorker(WorkManager.getInstance(context))
            } else {
                WorkManager.getInstance(context).cancelUniqueWork(SncfReservationsUpdateWorker.WORKER_NAME)
            }
        }
    }
}
