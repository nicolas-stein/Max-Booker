package fr.stein.maxbooker.api.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import fr.stein.maxbooker.api.getSncfApi
import fr.stein.maxbooker.database.MaxBookerDatabase
import fr.stein.maxbooker.datastore.MaxBookerDataStore
import kotlinx.coroutines.flow.first
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject

class SncfApiTravelDetailsWorker(appContext: Context, workerParameters: WorkerParameters):
    CoroutineWorker(appContext, workerParameters) {

    companion object {
        const val WORK_NAME = "SNCF_API_TRAVEL_DETAILS_WORK"
    }

    private val sncfApiSettingsDatastore = MaxBookerDataStore(appContext).getSncfApiSettingsDatastore()
    private val sncfUserDatastore = MaxBookerDataStore(appContext).getSncfUserDatastore()
    private val maxBookerDatabase = MaxBookerDatabase.getInstance(applicationContext)

    override suspend fun doWork(): Result {
        Log.d("Max Book", "Running SncfApiTravelDetailsWorker !")
        val sncfApiSettings  = sncfApiSettingsDatastore.data.first()
        val sncfUser = sncfUserDatastore.data.first()

        val orderId = inputData.getString("orderId")
        val origin = inputData.getString("origin")
        val destination = inputData.getString("destination")
        if (orderId == null || origin == null || destination == null) {
            Log.e("Mox Book", "Worker failed to update sncf api travel details : invalid input ($orderId, $origin, $destination)")
            return Result.failure()
        }

        val sncfReservation = maxBookerDatabase.sncfReservationDao().get(
            orderId = orderId,
            origin = origin,
            destination = destination
        )
        if (sncfReservation == null) {
            Log.e("Mox Book", "Worker failed to update sncf api travel details : no reservation found with orderId $orderId")
            return Result.failure()
        }

        val sncfApi = getSncfApi(sncfApiSettings.authToken, sncfApiSettings.cookies)
        val requestBodyJson = JSONObject()
        requestBodyJson.put("customerName", sncfUser.lastName)
        requestBodyJson.put("departureDateTime", sncfReservation.departureDateTime)
        requestBodyJson.put("marketingCarrierRef", sncfReservation.dvNumber)
        requestBodyJson.put("trainNumber", sncfReservation.trainNumber)

        val apiResponse = try {
            sncfApi.getTravelDetails(RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString()))
        } catch (e: Exception) {
            return Result.failure(
                Data.Builder()
                .putBoolean("networkException", true)
                .putString("networkExceptionDetail", e.message).build())
        }
        val sncfTravelDetails = apiResponse.body()

        if(!apiResponse.isSuccessful || sncfTravelDetails == null) {
            Log.e("Mox Book", "Worker failed to update sncf api travel details : ${apiResponse.code()} ${apiResponse.errorBody()?.string()}")
            return Result.failure()
        }

        Log.d("Max Book", "Worker updated sncf api travel details (orderId $orderId) successfully !")
        sncfReservation.setTravelDetails(sncfTravelDetails)
        maxBookerDatabase.sncfReservationDao().updateReservation(sncfReservation)

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(SncfApiWorkerNotifications.NOTIFICATION_ID_SNCF_API_TRAVEL_DETAILS_WORKER,
            SncfApiWorkerNotifications.getTravelDetailsWorkerNotification(applicationContext))
    }
}