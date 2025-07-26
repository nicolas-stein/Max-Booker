package fr.stein.maxbooker.ui.screens.settings.login

import android.webkit.JavascriptInterface

class LoginPayloadRecorder {
    private val payloadMap: MutableMap<String, String> = mutableMapOf()

    @JavascriptInterface
    fun recordPayload(method: String, url: String, payload: String) {
        payloadMap["$method-$url"] = payload
    }

    fun getPayload(method: String, url: String): String? = payloadMap["$method-$url"]
}
