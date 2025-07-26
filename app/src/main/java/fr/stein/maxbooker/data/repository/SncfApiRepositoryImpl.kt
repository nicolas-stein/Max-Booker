package fr.stein.maxbooker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import fr.stein.maxbooker.data.exception.SncfRepositoryException
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toProto
import fr.stein.maxbooker.data.remote.SncfApi
import fr.stein.maxbooker.domain.model.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.SncfApiTokenRequest
import fr.stein.maxbooker.domain.repository.SncfApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

class SncfApiRepositoryImpl(
    private val sncfApi: SncfApi,
    private val sncfApiAuthenticationDataStore: DataStore<SncfApiAuthenticationProto>
) : SncfApiRepository {

    override val sncfApiAuthentication: Flow<SncfApiAuthentication> =
        sncfApiAuthenticationDataStore.data.map {
            SncfApiAuthentication(
                it.sncfApiToken.toDomain(),
                it.cookies
            )
        }

    override suspend fun storeSncfApiAuthentication(sncfApiAuthentication: SncfApiAuthentication) {
        sncfApiAuthenticationDataStore.updateData { sncfApiAuthentication.toProto() }
    }

    @Throws(SncfRepositoryException::class)
    override suspend fun authenticate(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String
    ): SncfApiAuthentication {
        val tokenDto = executeSncfApiCall {
            sncfApi.getSncfApiToken(sncfApiTokenRequest, cookies)
        }

        return SncfApiAuthentication(
            tokenDto.toDomain(),
            cookies
        )
    }

    @Throws(SncfRepositoryException::class)
    suspend fun <T : Any> executeSncfApiCall(call: suspend () -> Response<T>): T {
        val response = try {
            call()
        } catch (e: IOException) {
            throw SncfRepositoryException.NetworkException(e)
        } catch (e: JsonParseException) {
            throw SncfRepositoryException.ParsingException(e)
        } catch (e: JsonMappingException) {
            throw SncfRepositoryException.ParsingException(e)
        } catch (e: Exception) {
            throw SncfRepositoryException("Unexpected error", e)
        }

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw SncfRepositoryException.ApiErrorException(
                response.code(),
                errorBody
            )
        }

        val body = response.body()
        if (body == null) {
            throw SncfRepositoryException.EmptyBodyException()
        }

        return body
    }
}
