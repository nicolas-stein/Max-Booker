package fr.stein.maxbooker.data.remote

import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest
import fr.stein.maxbooker.domain.model.sncf.SncfCustomerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SncfApi {
    @POST("auth/sfc/token")
    suspend fun getSncfApiToken(
        @Body sncfApiTokenRequest: SncfApiTokenRequest,
        @Header("Cookie") cookie: String
    ): Response<SncfApiTokenDto>

    @POST("customer/read-customer")
    suspend fun getCustomer(
        @Body sncfCustomerRequest: SncfCustomerRequest,
        @Header("Authorization") authorization: String
    ): Response<SncfCustomerDto>
}
