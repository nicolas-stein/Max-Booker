package fr.stein.maxbooker.ui.screens.settings.login

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.stein.maxbooker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailsLogin(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: SettingsDetailsLoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(),
                title = { Text(stringResource(R.string.screen_settings_item_login_headline)) }
            )
        }
    ) { innerPadding ->
        AndroidView(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    @SuppressLint("SetJavaScriptEnabled")
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    val loginWebViewClient = LoginWebViewClient(
                        loginPayloadRecorder = uiState.recorder,
                        loginFailed = {
                            // TODO show failed/retry screen
                            navigateBack()
                        },
                        loginSuccess = {
                            // TODO fetch user details then navigate back
                            navigateBack()
                        })

                    webViewClient = loginWebViewClient
                    webChromeClient = WebChromeClient()

                    addJavascriptInterface(uiState.recorder, "recorder")
                }
            }, update = { webView ->
                webView.loadUrl("https://www.maxjeune-tgvinoui.sncf/sncf-connect/mes-voyages")
            }
        )
    }
}
