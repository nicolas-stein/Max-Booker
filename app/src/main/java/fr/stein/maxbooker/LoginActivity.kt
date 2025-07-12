package fr.stein.maxbooker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import fr.stein.maxbooker.api.PayloadRecorder
import fr.stein.maxbooker.ui.login.LoginWebViewClient
import fr.stein.maxbooker.ui.theme.MaxBookerTheme

class LoginActivity : ComponentActivity() {

    private val recorder = PayloadRecorder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginScreen() {
        MaxBookerTheme {
            Scaffold (
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text("Connexion TGV Max")
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                setResult(RESULT_CANCELED)
                                finish()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Annuler la connexion"
                                )
                            }
                        },
                    )
                }
            ) { innerPadding ->
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    factory = {context ->
                        WebView(context).apply {
                            @SuppressLint("SetJavaScriptEnabled")
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true

                            webViewClient = LoginWebViewClient(recorder = recorder, authenticationDataObtained = { authToken, refreshToken, sncfCookies ->
                                val data = Intent()
                                data.putExtra("SNCF_AUTH_TOKEN", authToken)
                                data.putExtra("SNCF_REFRESH_TOKEN", refreshToken)
                                data.putExtra("SNCF_COOKIES", sncfCookies)
                                setResult(RESULT_OK, data)
                                Log.d("Max Book", "LoginScreen: got authentication data, closing login screen !")
                                finish()
                            })
                            webChromeClient = WebChromeClient()
                            addJavascriptInterface(recorder, "recorder")
                        }
                    },
                    update = { webview ->
                        webview.loadUrl("https://www.maxjeune-tgvinoui.sncf/sncf-connect/mes-voyages")
                    }
                )
            }
        }
    }
}