package fr.stein.maxbooker.ui.screens.settings.automations

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.stein.maxbooker.domain.work.SncfReservationsUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsDetailsAutomationsUiState(val sncfReservationsUpdateWorkInfo: WorkInfo? = null)

@HiltViewModel
class SettingsDetailsAutomationsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
): ViewModel() {
    private val _uiState = MutableStateFlow(SettingsDetailsAutomationsUiState())
    val uiState: StateFlow<SettingsDetailsAutomationsUiState> = _uiState.asStateFlow()

    init {
        val workManager = WorkManager.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            workManager.getWorkInfosForUniqueWorkFlow(SncfReservationsUpdateWorker.WORKER_NAME).collect { workInfos ->
                if (workInfos.isNotEmpty()) {
                    _uiState.update { it.copy(sncfReservationsUpdateWorkInfo = workInfos[0]) }
                }
            }
        }
    }
}