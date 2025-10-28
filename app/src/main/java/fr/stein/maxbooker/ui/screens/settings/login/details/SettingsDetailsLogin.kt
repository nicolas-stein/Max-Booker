package fr.stein.maxbooker.ui.screens.settings.login.details

import android.webkit.WebStorage
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.stein.maxbooker.ui.screens.settings.login.details.webview.LoginWebViewFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailsLogin(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: SettingsDetailsLoginViewModel = hiltViewModel<SettingsDetailsLoginViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    var webView by remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            when (it) {
                is LoginViewEvent.ReloadWebView -> webView?.reload()
                is LoginViewEvent.NavigateBack -> navigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            SettingsDetailsLoginTopAppBar(
                onBackClick = navigateBack,
                onClearCookieClick = {
                    WebStorage.getInstance().deleteAllData()
                },
                onRestartClick = {
                    webView?.loadUrl("https://www.maxjeune-tgvinoui.sncf/sncf-connect/mes-voyages")
                },
                modifier = modifier
            )
        }
    ) { innerPadding ->
        AndroidView(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            factory = { context ->
                LoginWebViewFactory.create(
                    context = context,
                    recorder = uiState.recorder,
                    onAuthCookiesCaptured = { cookies ->
                        viewModel.handleAuthCookiesCaptured(cookies)
                    }
                ).also { webView = it }
            },
            update = { webView ->
                webView.loadUrl("https://www.maxjeune-tgvinoui.sncf/sncf-connect/mes-voyages")
            }
        )
    }

    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            navigateBack()
        }
    }
}
