package fr.stein.maxbooker.api.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fr.stein.maxbooker.api.data.SncfApiStation
import fr.stein.maxbooker.api.getSncfApi
import fr.stein.maxbooker.database.MaxBookerDatabase
import fr.stein.maxbooker.datastore.MaxBookerDataStore
import kotlinx.coroutines.flow.first
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.time.LocalDateTime

class SncfApiReservationsWorker(appContext: Context, workerParameters: WorkerParameters):
    CoroutineWorker(appContext, workerParameters) {

    companion object {
        const val WORK_NAME = "SNCF_API_RESERVATIONS_WORK"

        suspend fun work(context: Context): Result {
            val sncfApiSettingsDatastore = MaxBookerDataStore(context).getSncfApiSettingsDatastore()
            val sncfUserDatastore = MaxBookerDataStore(context).getSncfUserDatastore()
            val maxBookerDatabase = MaxBookerDatabase.getInstance(context)

            Log.d("Max Book", "Running SncfApiReservationsWorker !")
            val sncfApiSettings  = sncfApiSettingsDatastore.data.first()
            val sncfUser = sncfUserDatastore.data.first()

            if(sncfUser.cardsCount == 0){
                Log.e("Mox Book", "Worker failed to update sncf api reservations : no cards")
                return Result.failure()
            }

            val sncfApi = getSncfApi(sncfApiSettings.authToken, sncfApiSettings.cookies)
            val requestBodyJson = JSONObject()
            requestBodyJson.put("cardNumber", sncfUser.cardsList[0].cardNumber)
            requestBodyJson.put("startDate", LocalDateTime.now().minusMonths(3))

            val apiResponse = try {
                sncfApi.getReservations(RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString()))
            } catch (e: Exception) {
                return Result.failure(Data.Builder()
                    .putBoolean("networkException", true)
                    .putString("networkExceptionDetail", e.message).build())
            }
            val sncfApiReservations = apiResponse.body()

            if(!apiResponse.isSuccessful || sncfApiReservations == null) {
                Log.e("Mox Book", "Worker failed to update sncf api reservations : ${apiResponse.code()} ${apiResponse.errorBody()?.string()}")
                return Result.failure()
            }

            val sncfApiSncfStationSet = mutableSetOf<SncfApiStation>()
            sncfApiReservations.forEach { sncfApiReservation ->
                sncfApiSncfStationSet.addAll(listOf(sncfApiReservation.origin, sncfApiReservation.destination))
            }

            maxBookerDatabase.sncfStationDao().upsertStations(sncfApiSncfStationSet.map { it.toSncfStation() })
            maxBookerDatabase.sncfReservationDao().upsertReservations(sncfApiReservations.map { it.toSncfReservation() })
            Log.d("Max Book", "Worker updated sncf api reservations (${sncfApiReservations.count()} reservations, ${sncfApiSncfStationSet.count()} stations) successfully !")
            val reservations = JsonArray()
            for(sncfApiReservation in sncfApiReservations) {
                val reservation = JSONObject()
                reservation.put("orderId", sncfApiReservation.orderId)
                reservation.put("origin", sncfApiReservation.origin)
                reservation.put("destination", sncfApiReservation.destination)
            }
            val outputData = Data.Builder().putString("reservations", reservations.toString()).build()
            return Result.success(outputData)
        }
    }

    override suspend fun doWork(): Result {
        return work(applicationContext)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(SncfApiWorkerNotifications.NOTIFICATION_ID_SNCF_API_RESERVATION_WORKER,
            SncfApiWorkerNotifications.getReservationWorkerNotification(applicationContext))
    }
}