package fr.stein.maxbooker.data.remote.sncf

import fr.stein.maxbooker.data.remote.sncf.dto.SncfCustomerDto
import fr.stein.maxbooker.domain.model.sncf.SncfCustomerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SncfApi {
    @POST("customer/read-customer")
    suspend fun getCustomer(
        @Body sncfCustomerRequest: SncfCustomerRequest,
        @Header ("Cookie") cookiesOverride: String?
    ): Response<SncfCustomerDto>
}
