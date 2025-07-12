package fr.stein.maxbooker.api.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import fr.stein.maxbooker.api.getSncfApi
import fr.stein.maxbooker.datastore.MaxBookerDataStore
import fr.stein.maxbooker.proto.SncfCard
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class SncfApiUserWorker(appContext: Context, workerParameters: WorkerParameters):
    CoroutineWorker(appContext, workerParameters) {

    companion object {
        const val WORK_NAME = "SNCF_API_USER_WORK"

        suspend fun work(context: Context): Result {
            val sncfApiSettingsDatastore = MaxBookerDataStore(context).getSncfApiSettingsDatastore()
            val sncfUserDatastore = MaxBookerDataStore(context).getSncfUserDatastore()
            Log.d("Max Book", "Running SncfApiUserWorker !")
            val sncfApiSettings  = sncfApiSettingsDatastore.data.first()

            val sncfApi = getSncfApi(sncfApiSettings.authToken, sncfApiSettings.cookies)

            val requestBodyJson = JSONObject()
            requestBodyJson.put("productTypes", JSONArray(listOf("TGV_MAX_JEUNE", "FIDEL", "IDTGV_MAX")))

            val apiResponse = try {
                sncfApi.readCustomer(requestBodyJson.toString()
                    .toRequestBody("application/json".toMediaTypeOrNull()))
            } catch (e: Exception) {
                return Result.failure(Data.Builder()
                    .putBoolean("networkException", true)
                    .putString("networkExceptionDetail", e.message).build())
            }
            val sncfApiUser = apiResponse.body()

            if(!apiResponse.isSuccessful || sncfApiUser == null) {
                Log.e("Mox Book", "Worker failed to update sncf api user : ${apiResponse.code()} ${apiResponse.errorBody()?.string()}")
                return Result.failure()
            }

            sncfUserDatastore.updateData { currentData ->
                currentData.toBuilder()
                    .setFirstName(sncfApiUser.firstName)
                    .setLastName(sncfApiUser.lastName)
                    .clearCards()
                    .addAllCards(sncfApiUser.cards.map { sncfApiCard ->  SncfCard.newBuilder()
                        .setCardNumber(sncfApiCard.cardNumber)
                        .setProductType(sncfApiCard.productType)
                        .build() })
                    .build()
            }
            Log.d("Max Book", "Worker updated sncf api user successfully !")
            return Result.success()
        }
    }

    override suspend fun doWork(): Result {
        return work(applicationContext)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(SncfApiWorkerNotifications.NOTIFICATION_ID_SNCF_API_USER_WORKER,
            SncfApiWorkerNotifications.getUserWorkerNotification(applicationContext))
    }
}