package fr.stein.maxbooker.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import fr.stein.maxbooker.data.exception.SncfApiRepositoryException
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toProto
import fr.stein.maxbooker.data.remote.SncfApi
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.SncfCustomerRequest
import fr.stein.maxbooker.domain.repository.SncfApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.Response

class SncfApiRepositoryImpl(
    private val sncfApi: SncfApi,
    private val sncfApiAuthenticationDataStore: DataStore<SncfApiAuthenticationProto>,
    private val sncfCustomerDataStore: DataStore<SncfCustomerProto>
) : SncfApiRepository {

    private val sncfApiAuthenticationFlow: Flow<SncfApiAuthentication?> =
        sncfApiAuthenticationDataStore.data.map { it.toDomain() }

    private suspend fun getSncfApiAuthentication(): SncfApiAuthentication? =
        sncfApiAuthenticationFlow.first()

    @Throws(SncfApiRepositoryException::class)
    override suspend fun authenticate(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String
    ): SncfApiAuthentication {
        Log.d("Max Book", "SncfApiRepositoryImpl: requested authenticate")
        val tokenDto = executeSncfApiCall { sncfApi.getSncfApiToken(sncfApiTokenRequest, cookies) }
        val newSncfApiAuthentication = SncfApiAuthentication(tokenDto.toDomain(), cookies)
        sncfApiAuthenticationDataStore.updateData { newSncfApiAuthentication.toProto() }

        return newSncfApiAuthentication
    }

    @Throws(SncfApiRepositoryException::class)
    override suspend fun getCustomer(): SncfCustomer {
        Log.d("Max Book", "SncfApiRepositoryImpl: requested getCustomer")
        val authorizationHeader = getAuthorizationHeader()
        val sncfCustomerDto = executeSncfApiCallAuthenticated {
            sncfApi.getCustomer(
                SncfCustomerRequest(
                    productTypes = listOf("TGV_MAX_JEUNE", "FIDEL", "IDTGV_MAX")
                ),
                authorization = authorizationHeader
            )
        }
        val sncfCustomer = sncfCustomerDto.toDomain()

        sncfCustomerDataStore.updateData { sncfCustomer.toProto() }
        return sncfCustomer
    }

    @Throws(SncfApiRepositoryException::class)
    suspend fun <T : Any> executeSncfApiCallAuthenticated(call: suspend () -> Response<T>): T {
        try {
            return executeSncfApiCall(call)
        } catch (_: SncfApiRepositoryException.InvalidAuthentication) {
            val sncfApiAuthentication = getSncfApiAuthentication()
            if (sncfApiAuthentication == null) {
                throw SncfApiRepositoryException.AuthenticatedRequired()
            }

            try {
                authenticate(
                    SncfApiTokenRequest(
                        refreshToken = sncfApiAuthentication.sncfApiToken.refreshToken,
                        redirectUri = "https://maxjeune-tgvinoui.sncf/auth/login/redirect",
                        type = "REFRESH_TOKEN"
                    ),
                    sncfApiAuthentication.cookies
                )
            } catch (_: SncfApiRepositoryException.ApiErrorException) {
                sncfApiAuthenticationDataStore.updateData { current ->
                    current.toBuilder().clear().build()
                }
                sncfCustomerDataStore.updateData { current -> current.toBuilder().clear().build() }
            }

            return executeSncfApiCall(call)
        }
    }

    @Throws(SncfApiRepositoryException.AuthenticatedRequired::class)
    private suspend fun getAuthorizationHeader(): String {
        val sncfApiAuthentication = getSncfApiAuthentication()
        if (sncfApiAuthentication == null) {
            throw SncfApiRepositoryException.AuthenticatedRequired()
        }

        return "Bearer ${sncfApiAuthentication.sncfApiToken.idToken}"
    }

    @Throws(SncfApiRepositoryException::class)
    suspend fun <T : Any> executeSncfApiCall(call: suspend () -> Response<T>): T {
        val response = try {
            call()
        } catch (e: IOException) {
            throw SncfApiRepositoryException.NetworkException(e)
        } catch (e: JsonParseException) {
            throw SncfApiRepositoryException.ParsingException(e)
        } catch (e: JsonMappingException) {
            throw SncfApiRepositoryException.ParsingException(e)
        } catch (e: Exception) {
            throw SncfApiRepositoryException("Unexpected error", e)
        }

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            if (response.code() == 401) {
                throw SncfApiRepositoryException.InvalidAuthentication()
            }
            throw SncfApiRepositoryException.ApiErrorException(
                response.code(),
                errorBody
            )
        }

        val body = response.body()
        if (body == null) {
            throw SncfApiRepositoryException.EmptyBodyException()
        }

        return body
    }
}
