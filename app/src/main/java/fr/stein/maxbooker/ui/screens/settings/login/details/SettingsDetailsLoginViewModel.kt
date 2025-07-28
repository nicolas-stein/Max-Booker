package fr.stein.maxbooker.ui.screens.settings.login.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer
import fr.stein.maxbooker.domain.usecase.SncfApiAuthenticateUseCase
import fr.stein.maxbooker.domain.usecase.SncfApiFetchCustomerUseCase
import fr.stein.maxbooker.ui.screens.settings.login.details.webview.LoginPayloadRecorder
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class SettingsDetailsLoginUiState(
    val recorder: LoginPayloadRecorder = LoginPayloadRecorder(),
    var showLoginAuthenticationDialog: Boolean = false,
    var loginAuthenticationDialogState: LoginAuthenticationDialogState =
        LoginAuthenticationDialogState.IN_PROGRESS,
    var loginAuthenticationDialogError: Throwable? = null
)

@HiltViewModel
class SettingsDetailsLoginViewModel @Inject constructor(
    private val sncfApiAuthenticateUseCase: SncfApiAuthenticateUseCase,
    private val sncfApiFetchCustomerUseCase: SncfApiFetchCustomerUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsDetailsLoginUiState())
    val uiState: StateFlow<SettingsDetailsLoginUiState> = _uiState.asStateFlow()

    private val _reloadWebView = MutableSharedFlow<Unit>()
    val reloadWebView: SharedFlow<Unit> = _reloadWebView

    private var previousSncfApiTokenRequest: SncfApiTokenRequest? = null

    fun handleRequestLogin(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String,
        navigateBack: () -> Unit
    ): SncfApiAuthentication? {
        val sncfApiAuthentication: SncfApiAuthentication

        _uiState.update { currentState ->
            currentState.copy(
                showLoginAuthenticationDialog = true,
                loginAuthenticationDialogState = LoginAuthenticationDialogState.IN_PROGRESS
            )
        }

        try {
            runBlocking {
                if (sncfApiTokenRequest != previousSncfApiTokenRequest) {
                    sncfApiAuthentication =
                        sncfApiAuthenticateUseCase.authenticate(sncfApiTokenRequest, cookies)
                    previousSncfApiTokenRequest = sncfApiTokenRequest
                } else {
                    sncfApiAuthenticateUseCase.updateCookies(cookies)
                    sncfApiAuthentication = sncfApiAuthenticateUseCase.getSavedAuthentication()!!
                }
            }
        } catch (exception: SncfApiException) {
            Log.e("Max Book", "Failed to authenticate to SNCF API", exception)
            _uiState.update { currentState ->
                currentState.copy(
                    loginAuthenticationDialogState = LoginAuthenticationDialogState.FAILED,
                    loginAuthenticationDialogError = exception
                )
            }
            return null
        }

        viewModelScope.launch(Dispatchers.IO) {
            fetchSncfCustomer(navigateBack)
        }
        return sncfApiAuthentication
    }

    fun fetchSncfCustomer(navigateBack: () -> Unit) {
        val sncfCustomer: SncfCustomer

        try {
            sncfCustomer = runBlocking {
                sncfApiFetchCustomerUseCase()
            }
        } catch (exception: SncfApiException) {
            Log.e("Max Book", "Failed to fetch customer from SNCF API", exception)
            if (exception is SncfApiException.ApiErrorException &&
                exception.code == 403
            ) {
                viewModelScope.launch {
                    _uiState.update { currentState ->
                        currentState.copy(showLoginAuthenticationDialog = false)
                    }
                    _reloadWebView.emit(Unit)
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        loginAuthenticationDialogState = LoginAuthenticationDialogState.FAILED,
                        loginAuthenticationDialogError = exception
                    )
                }
            }
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                loginAuthenticationDialogState = LoginAuthenticationDialogState.SUCCESS
            )
        }
        viewModelScope.launch {
            delay(3000)
            navigateBack()
            _uiState.update { currentState ->
                currentState.copy(showLoginAuthenticationDialog = false)
            }
        }
    }
}
