package fr.stein.maxbooker.data.repository.sncf

import androidx.datastore.core.IOException
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import fr.stein.maxbooker.domain.repository.sncf.SncfApiExecutor
import javax.inject.Provider
import retrofit2.Response

class SncfApiExecutorImpl(
    private val sncfApiAuthenticationRepository: Provider<SncfApiAuthenticationRepository>
) : SncfApiExecutor {
    @Throws(SncfApiException::class)
    override suspend fun <T : Any> execute(call: suspend () -> Response<T>): T {
        val response = try {
            call()
        } catch (e: IOException) {
            throw SncfApiException.NetworkException(e)
        } catch (e: JsonParseException) {
            throw SncfApiException.ParsingException(e)
        } catch (e: JsonMappingException) {
            throw SncfApiException.ParsingException(e)
        } catch (e: Exception) {
            throw SncfApiException("Unexpected error", e)
        }

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            if (response.code() == 401) {
                if (sncfApiAuthenticationRepository.get().getSncfApiAuthentication() == null) {
                    throw SncfApiException.AuthenticatedRequired()
                } else {
                    throw SncfApiException.InvalidAuthentication()
                }
            }
            throw SncfApiException.ApiErrorException(
                response.code(),
                errorBody
            )
        }

        val body = response.body()
        if (body == null) {
            throw SncfApiException.EmptyBodyException()
        }

        return body
    }
}
