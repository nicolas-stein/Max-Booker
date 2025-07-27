package fr.stein.maxbooker.domain.model.sncf

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SncfApiTokenRequest(
    val authCode: String? = null,
    val refreshToken: String? = null,
    val redirectUri: String,
    val type: String
)
