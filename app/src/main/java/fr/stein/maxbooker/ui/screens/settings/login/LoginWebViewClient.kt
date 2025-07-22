package fr.stein.maxbooker.ui.screens.settings.login

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dagger.hilt.android.EntryPointAccessors
import fr.stein.maxbooker.di.LoginWebViewClientEntryPoint
import fr.stein.maxbooker.domain.model.SncfApiTokenRequest
import kotlinx.coroutines.runBlocking


class LoginWebViewClient(
    private val loginPayloadRecorder: LoginPayloadRecorder
): WebViewClient() {

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        if (request == null || view == null || view.context !is Activity) {
            return super.shouldInterceptRequest(view, request)
        }
        //Log.d("Max Book", "shouldInterceptRequest: ${request.method} ${request.url}")

        // Intercept request to get refresh token
        if (request.url.toString() == "https://www.maxjeune-tgvinoui.sncf/api/public/auth/sfc/token") {
            val payload = loginPayloadRecorder.getPayload(request.method, request.url.toString())
            Log.d("Max Book", "SNCF API token request payload : $payload")

            if (payload == null) {
                return null
            }

            val mapper = jacksonObjectMapper()
            val sncfApiTokenRequest: SncfApiTokenRequest
            try {
                sncfApiTokenRequest = mapper.readValue(payload)
            } catch (e: JsonParseException) {
                Log.e("Max Book", "Failed to parse payload as a SncfApiTokenRequest object", e)
                return null
            }

            val entryPoint = EntryPointAccessors.fromActivity(view.context as Activity, LoginWebViewClientEntryPoint::class.java)
            val sncfApiRepository = entryPoint.getSncfApiRepository()

            return try {
                Log.d("Max Book", "Retrieving SNCF API token with input : $sncfApiTokenRequest")
                runBlocking {
                    val sncfApiAuthentication = sncfApiRepository.retrieveSncfApiToken(sncfApiTokenRequest, CookieManager.getInstance().getCookie(request.url.toString()))

                    val content = jacksonObjectMapper().writeValueAsString(sncfApiAuthentication.sncfApiToken)
                    WebResourceResponse(
                        "application/json",
                        "UTF-8",
                        content.byteInputStream()
                    )
                }
            } catch (e: Exception) {
                Log.e("Max book", "Exception while calling SncfApi to retrieve SncfApiToken", e)
                null
            }
        }
        else if (request.url.toString() == "https://www.maxjeune-tgvinoui.sncf/api/public/customer/read-customer") {
            // TODO authenticationDataObtained(null, null, CookieManager.getInstance().getCookie(request.url.toString()))
        }

        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        view!!.loadUrl(request!!.url.toString())
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript("for(elem of document.body.getElementsByTagName('*')) {elem.style.height=${view.height}};document.body.style.height=${view.height}", null)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        view?.evaluateJavascript(
            view.context.assets.open("loginWebViewIntercept.js").reader().readText(),
            null
        )
    }
}