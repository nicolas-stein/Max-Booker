package fr.stein.maxbooker.domain.repository.sncf

import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer

interface SncfApiRepository {

    @Throws(SncfApiException::class)
    suspend fun getCustomer(cookiesOverride: String?): SncfCustomer
}
