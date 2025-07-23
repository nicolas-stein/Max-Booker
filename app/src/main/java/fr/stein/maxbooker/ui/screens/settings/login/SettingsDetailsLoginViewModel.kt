package fr.stein.maxbooker.ui.screens.settings.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsDetailsLoginUiState(
    val recorder: LoginPayloadRecorder = LoginPayloadRecorder()
)

class SettingsDetailsLoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsDetailsLoginUiState())
    val uiState: StateFlow<SettingsDetailsLoginUiState> = _uiState.asStateFlow()
}