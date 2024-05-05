package fr.stein.maxbooker.ui.tab.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Duration

class SettingsTabViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(SettingsTabUiState())
    val uiState: StateFlow<SettingsTabUiState> = _uiState.asStateFlow()

    fun showPastReservationsSwitchStateChanged(state: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(showPastReservations = state)
        }
    }

    fun pastReservationsDurationChanged(pastReservationsDuration: Duration) {
        _uiState.update { currentState ->
            currentState.copy(pastReservationsDuration = pastReservationsDuration)
        }
    }

    fun autoConfirmReservationsSwitchStateChanged(state: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(autoConfirmReservations = state)
        }
    }
}