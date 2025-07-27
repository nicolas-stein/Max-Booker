package fr.stein.maxbooker.ui.screens.settings.login.details

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.stein.maxbooker.data.exception.SncfApiRepositoryException
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer
import fr.stein.maxbooker.domain.usecase.SncfApiAuthenticateUseCase
import fr.stein.maxbooker.domain.usecase.SncfApiFetchCustomerUseCase
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class SettingsDetailsLoginUiState(
    val recorder: LoginPayloadRecorder = LoginPayloadRecorder(),
    var webView: WebView? = null,
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

    fun handleBackPressed() {
        val webView = uiState.value.webView
        if (webView?.canGoBack() == true) {
            webView.goBack()
        }
    }

    val topAppBarClearCookiesHandler: () -> Unit = {
        WebStorage.getInstance().deleteAllData()
    }

    val topAppBarRestartHandler: () -> Unit = {
        uiState.value.webView?.loadUrl(
            "https://www.maxjeune-tgvinoui.sncf/sncf-connect/mes-voyages"
        )
    }

    fun buildWebView(context: Context, navigateBack: () -> Unit): View = WebView(context).apply {
        @SuppressLint("SetJavaScriptEnabled")
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        val loginWebViewClient = LoginWebViewClient(
            loginPayloadRecorder = uiState.value.recorder,
            requestLogin = { sncfApiTokenRequest: SncfApiTokenRequest, cookies: String ->
                handleRequestLogin(sncfApiTokenRequest, cookies, navigateBack)
            }
        )

        webViewClient = loginWebViewClient
        webChromeClient = WebChromeClient()

        addJavascriptInterface(uiState.value.recorder, "recorder")
    }

    fun updateWebView(view: View) {
        val webView = view as WebView
        webView.loadUrl("https://www.maxjeune-tgvinoui.sncf/sncf-connect/mes-voyages")
        _uiState.update { currentState ->
            currentState.copy(webView = webView)
        }
    }

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
                sncfApiAuthentication = sncfApiAuthenticateUseCase(sncfApiTokenRequest, cookies)
            }
        } catch (exception: SncfApiRepositoryException) {
            Log.e("Max Book", "Failed to authenticate to SNCF API", exception)
            _uiState.update { currentState ->
                currentState.copy(
                    loginAuthenticationDialogState = LoginAuthenticationDialogState.FAILED,
                    loginAuthenticationDialogError = exception
                )
            }
            return null
        }

        viewModelScope.launch {
            fetchSncfCustomer(navigateBack)
        }
        return sncfApiAuthentication
    }

    fun fetchSncfCustomer(navigateBack: () -> Unit) {
        val sncfCustomer: SncfCustomer

        try {
            runBlocking {
                sncfCustomer = sncfApiFetchCustomerUseCase()
            }
        } catch (exception: SncfApiRepositoryException) {
            Log.e("Max Book", "Failed to fetch customer from SNCF API", exception)
            if (exception is SncfApiRepositoryException.ApiErrorException
                && exception.code == 403) {
                _uiState.update { currentState -> currentState.copy(showLoginAuthenticationDialog = false) }
                uiState.value.webView?.reload()
            }

            _uiState.update { currentState ->
                currentState.copy(
                    loginAuthenticationDialogState = LoginAuthenticationDialogState.FAILED,
                    loginAuthenticationDialogError = exception
                )
            }
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                loginAuthenticationDialogState = LoginAuthenticationDialogState.SUCECSS
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
