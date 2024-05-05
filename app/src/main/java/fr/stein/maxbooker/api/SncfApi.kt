package fr.stein.maxbooker.api

import fr.stein.maxbooker.api.data.SncfApiReservation
import fr.stein.maxbooker.api.data.SncfApiToken
import fr.stein.maxbooker.api.data.SncfApiTravel
import fr.stein.maxbooker.api.data.SncfApiUser
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SncfApi {

    @POST("auth/sfc/token")
    suspend fun getAuthToken(@Body requestBody: RequestBody): Response<SncfApiToken?>

    @POST("customer/read-customer")
    suspend fun readCustomer(@Body requestBody: RequestBody): Response<SncfApiUser?>

    @POST("reservation/travel-consultation")
    suspend fun getReservations(@Body requestBody: RequestBody): Response<List<SncfApiReservation>?>

    @POST("reservation/get-travel")
    suspend fun getTravelDetails(@Body requestBody: RequestBody): Response<SncfApiTravel>

    @POST("reservation/travel-confirm")
    suspend fun confirmTravel(@Body requestBody: RequestBody): Response<ResponseBody>
}