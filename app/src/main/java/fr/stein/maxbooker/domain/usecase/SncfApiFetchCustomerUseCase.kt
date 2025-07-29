package fr.stein.maxbooker.domain.usecase

import android.util.Log
import androidx.datastore.core.DataStore
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.mapper.toProto
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import javax.inject.Inject

class SncfApiFetchCustomerUseCase @Inject constructor(
    private val sncfApiRepository: SncfApiRepository,
    private val sncfCustomerDataStore: DataStore<SncfCustomerProto>
) {
    @Throws(SncfApiException::class)
    suspend operator fun invoke(): SncfCustomer {
        val sncfCustomer = sncfApiRepository.getCustomer()
        sncfCustomerDataStore.updateData { sncfCustomer.toProto() }
        Log.d("Max Book", "SncfApiFetchCustomerUseCase: saved fetched customer")
        return sncfCustomer
    }
}
