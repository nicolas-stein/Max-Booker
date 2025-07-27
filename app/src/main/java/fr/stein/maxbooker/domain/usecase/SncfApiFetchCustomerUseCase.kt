package fr.stein.maxbooker.domain.usecase

import fr.stein.maxbooker.data.exception.SncfApiRepositoryException
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer
import fr.stein.maxbooker.domain.repository.SncfApiRepository
import javax.inject.Inject

class SncfApiFetchCustomerUseCase @Inject constructor(
    private val sncfApiRepository: SncfApiRepository
) {
    @Throws(SncfApiRepositoryException::class)
    suspend operator fun invoke(): SncfCustomer = sncfApiRepository.getCustomer()
}
