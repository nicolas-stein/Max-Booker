package fr.stein.maxbooker.domain.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SncfApiTokenRequest(
    val authCode: String?,
    val refreshToken: String?,
    val redirectUri: String,
    val type: String
)
