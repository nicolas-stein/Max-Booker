package fr.stein.maxbooker.domain.usecase

import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import javax.inject.Inject

class SncfApiFetchCustomerUseCase @Inject constructor(
    private val sncfApiRepository: SncfApiRepository
) {
    @Throws(SncfApiException::class)
    suspend operator fun invoke(cookiesOverride: String?): SncfCustomer {
        val sncfCustomer = sncfApiRepository.getCustomer(cookiesOverride)
        return sncfCustomer
    }
}
