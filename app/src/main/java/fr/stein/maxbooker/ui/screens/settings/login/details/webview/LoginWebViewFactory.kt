package fr.stein.maxbooker.ui.screens.settings.login.details.webview

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebView
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest

object LoginWebViewFactory {
    fun create(
        context: Context,
        recorder: LoginPayloadRecorder,
        onLoginRequest: (SncfApiTokenRequest, String) -> SncfApiAuthentication?
    ): WebView = WebView(context).apply {
        @SuppressLint("SetJavaScriptEnabled")
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        val loginWebViewClient = LoginWebViewClient(
            loginPayloadRecorder = recorder,
            requestLogin = onLoginRequest
        )

        webViewClient = loginWebViewClient
        webChromeClient = WebChromeClient()

        addJavascriptInterface(recorder, "recorder")
    }
}
