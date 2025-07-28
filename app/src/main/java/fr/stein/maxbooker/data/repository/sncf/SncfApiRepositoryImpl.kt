package fr.stein.maxbooker.data.repository.sncf

import android.util.Log
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.remote.sncf.SncfApi
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.SncfCustomerRequest
import fr.stein.maxbooker.domain.repository.sncf.SncfApiExecutor
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository

class SncfApiRepositoryImpl(
    private val sncfApi: SncfApi,
    private val sncfApiExecutor: SncfApiExecutor
) : SncfApiRepository {

    @Throws(SncfApiException::class)
    override suspend fun getCustomer(): SncfCustomer {
        Log.d("Max Book", "SncfApiRepositoryImpl: requested getCustomer")
        val sncfCustomerDto = sncfApiExecutor.execute {
            sncfApi.getCustomer(
                SncfCustomerRequest(
                    productTypes = listOf("TGV_MAX_JEUNE", "FIDEL", "IDTGV_MAX")
                )
            )
        }
        val sncfCustomer = sncfCustomerDto.toDomain()
        return sncfCustomer
    }
}
