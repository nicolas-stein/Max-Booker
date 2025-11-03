package fr.stein.maxbooker.ui.screens.settings.login.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.fetcher.sncf.SncfApiCustomerFetcher
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import fr.stein.maxbooker.domain.usecase.SncfApiFetchCustomerUseCase
import fr.stein.maxbooker.ui.screens.settings.login.details.webview.LoginPayloadRecorder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsDetailsLoginUiState(val recorder: LoginPayloadRecorder = LoginPayloadRecorder())

sealed class LoginViewEvent {
    object NavigateBack : LoginViewEvent()
    object ReloadWebView : LoginViewEvent()
}

@HiltViewModel
class SettingsDetailsLoginViewModel @Inject constructor(
    private val sncfApiAuthenticationRepository: SncfApiAuthenticationRepository,
    private val sncfApiFetchCustomerUseCase: SncfApiFetchCustomerUseCase,
    private val sncfApiCustomerFetcher: SncfApiCustomerFetcher
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsDetailsLoginUiState())
    val uiState: StateFlow<SettingsDetailsLoginUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LoginViewEvent>()
    val eventFlow: SharedFlow<LoginViewEvent> = _eventFlow.asSharedFlow()

    fun handleAuthCookiesCaptured(cookies: String) {
        viewModelScope.launch {
            try {
                val sncfCustomer = sncfApiFetchCustomerUseCase(cookies)
                Log.d(
                    "Max Book",
                    "handleAuthCookiesCaptured: successfully fetched sncfCustomer ${sncfCustomer.firstName} ${sncfCustomer.lastName}"
                )
                sncfApiAuthenticationRepository.updateAuthenticationCookie(cookies)
                sncfApiCustomerFetcher.fetchCustomer()
                _eventFlow.emit(LoginViewEvent.NavigateBack)
            } catch (exception: SncfApiException) {
                Log.e("Max Book", "Failed to fetch customer from SNCF API", exception)
            }
        }
    }
}
