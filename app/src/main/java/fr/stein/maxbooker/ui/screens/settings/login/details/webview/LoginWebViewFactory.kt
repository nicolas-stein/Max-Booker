package fr.stein.maxbooker.ui.screens.settings.login.details.webview

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebView

object LoginWebViewFactory {
    fun create(context: Context, recorder: LoginPayloadRecorder, onAuthCookiesCaptured: (String) -> Unit): WebView =
        WebView(context).apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            val loginWebViewClient = LoginWebViewClient(
                onAuthCookiesCaptured = onAuthCookiesCaptured
            )

            webViewClient = loginWebViewClient
            webChromeClient = WebChromeClient()

            addJavascriptInterface(recorder, "recorder")
        }
}
