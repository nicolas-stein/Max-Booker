package fr.stein.maxbooker.ui.login

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson
import fr.stein.maxbooker.api.PayloadRecorder
import fr.stein.maxbooker.api.data.SncfApiToken
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class LoginWebViewClient(private val recorder: PayloadRecorder,
                         private val authenticationDataObtained: (authToken:String?, refreshToken: String?, sncfCookies: String?) -> Unit, ): WebViewClient() {
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        if (request == null) {
            return super.shouldInterceptRequest(view, request as WebResourceRequest?)
        }

        //Log.d("Max Book", "shouldInterceptRequest: ${request!!.method} ${request!!.url}")

        // Intercept request to get refresh token
        if (request.url.toString() == "https://www.maxjeune-tgvinoui.sncf/api/public/auth/sfc/token") {
            val payload = recorder.getPayload(request.method, request.url.toString())
            Log.d("Max Book", "Token request payload : $payload")

            // Manually create request to retrieve token
            val url = URL(request.url.toString())
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod(request.method)
            urlConnection.doOutput = true
            urlConnection.setRequestProperty("accept", "application/json")
            urlConnection.setRequestProperty("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
            urlConnection.setRequestProperty("content-type", "application/json")
            urlConnection.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
            urlConnection.setRequestProperty("x-client-app", "MAX_JEUNE")
            urlConnection.setRequestProperty("cookie", CookieManager.getInstance().getCookie(request.url.toString()))

            val outputStream = urlConnection.outputStream
            val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
            if (payload != null) {
                bufferedWriter.write(payload)
                bufferedWriter.flush()
            }
            bufferedWriter.close()
            outputStream.close()

            urlConnection.connect()

            if (urlConnection.errorStream != null) {
                val reader = BufferedReader(urlConnection.errorStream.reader())
                val content = reader.readText()
                reader.close()
                Log.e("Max Book", "Error while retrieving token ${urlConnection.responseCode} : $content")
            }

            if (urlConnection.responseCode == 200) {
                // Read the response
                val reader = BufferedReader(urlConnection.inputStream.reader())
                val content = reader.readText()
                reader.close()
                val sncfApiToken = Gson().fromJson(content, SncfApiToken::class.java)

                Log.d("Max Book", "Got refresh token : $content")

                authenticationDataObtained(sncfApiToken.idToken, sncfApiToken.refreshToken, CookieManager.getInstance().getCookie(request.url.toString()))
                // Return a WebResourceResponse with the data fetched
                return WebResourceResponse(urlConnection.contentType,
                    urlConnection.contentEncoding,
                    content.byteInputStream())
            }
            else if (urlConnection.responseCode == 400) {
                // Stored cookies are invalid, need to fully authenticate again
                Handler(Looper.getMainLooper()).post {
                    WebStorage.getInstance().deleteAllData()
                    CookieManager.getInstance().removeAllCookies {
                        CookieManager.getInstance().flush()
                        view?.loadUrl("https://www.maxjeune-tgvinoui.sncf/sncf-connect/mes-voyages")
                    }
                }
            }
        }
        else if (request.url.toString() == "https://www.maxjeune-tgvinoui.sncf/api/public/customer/read-customer") {
            authenticationDataObtained(null, null, CookieManager.getInstance().getCookie(request.url.toString()))
        }

        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        view!!.loadUrl(request!!.url.toString())
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript("for(elem of document.body.getElementsByTagName('*')) {if(elem.style.height == '100vh'){elem.style.height=window.innerHeight}}", null)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        view?.evaluateJavascript(
            view.context.assets.open("intercept.js").reader().readText(),
            null
        )
    }
}