package fr.stein.maxbooker.api.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import fr.stein.maxbooker.api.getSncfApi
import fr.stein.maxbooker.datastore.MaxBookerDataStore
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SncfApiAuthTokenWorker(appContext: Context, workerParameters: WorkerParameters):
    CoroutineWorker(appContext, workerParameters) {

    companion object {
        suspend fun work(context: Context): Result {
            val sncfApiSettingsDatastore = MaxBookerDataStore(context).getSncfApiSettingsDatastore()
            Log.d("Max Book", "Running SncfAuthTokenWorker !")
            val sncfApiSettings  = sncfApiSettingsDatastore.data.first()

            val sncfApi = getSncfApi(null, sncfApiSettings.cookies)
            val requestBodyJson = JSONObject()
            requestBodyJson.put("type", "REFRESH_TOKEN")
            requestBodyJson.put("refreshToken", sncfApiSettings.refreshToken)
            requestBodyJson.put("redirectUri", "https://maxjeune-tgvinoui.sncf/auth/login/redirect")

            val apiResponse = try {
                sncfApi.getAuthToken(requestBodyJson.toString()
                    .toRequestBody("application/json".toMediaTypeOrNull()))
            } catch (e: Exception) {
                return Result.failure(
                    Data.Builder()
                        .putBoolean("networkException", true)
                        .putString("networkExceptionDetail", e.message).build())
            }

            val apiToken = apiResponse.body()

            if(!apiResponse.isSuccessful || apiToken == null) {
                Log.e("Mox Book", "Worker failed to update sncf api auth token : ${apiResponse.code()} ${apiResponse.errorBody()?.string()}")
                return Result.failure()
            }

            sncfApiSettingsDatastore.updateData { currentData ->
                currentData.toBuilder()
                    .setAuthToken(apiToken.idToken)
                    .setRefreshToken(apiToken.refreshToken)
                    .build()
            }

            Log.d("Max Book", "Worker updated sncf api auth token successfully !")
            return Result.success()
        }
    }

    override suspend fun doWork(): Result {
        return work(applicationContext)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(SncfApiWorkerNotifications.NOTIFICATION_ID_SNCF_API_AUTH_TOKEN_WORKER,
            SncfApiWorkerNotifications.getAuthTokenWorkerNotification(applicationContext))
    }
}