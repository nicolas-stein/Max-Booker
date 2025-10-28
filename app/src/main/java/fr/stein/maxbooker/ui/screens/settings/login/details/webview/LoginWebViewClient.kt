package fr.stein.maxbooker.ui.screens.settings.login.details.webview

import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class LoginWebViewClient(
    private val onAuthCookiesCaptured: (
        cookies: String
    ) -> Unit
) : WebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        // Log.d("Max Book", "shouldInterceptRequest: ${request.method} ${request.url}")

        if (request != null &&
            request.url.toString() ==
            "https://www.maxjeune-tgvinoui.sncf/api/public/customer/read-customer"
        ) {
            onAuthCookiesCaptured(CookieManager.getInstance().getCookie(request.url.toString()))
        }

        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        view!!.loadUrl(request!!.url.toString())
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript(
            "for(elem of document.body.getElementsByTagName('*')) {elem.style.height=${view.height}};document.body.style.height=${view.height}",
            null
        )
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        view?.evaluateJavascript(
            view.context.assets.open("loginWebViewIntercept.js").reader().readText(),
            null
        )
    }
}
