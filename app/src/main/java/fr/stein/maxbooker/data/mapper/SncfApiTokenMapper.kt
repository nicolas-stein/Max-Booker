package fr.stein.maxbooker.data.mapper

import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiTokenProto
import fr.stein.maxbooker.data.remote.SncfApiTokenDto
import fr.stein.maxbooker.domain.model.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.SncfApiToken

fun SncfApiToken.toProto() = SncfApiTokenProto
    .newBuilder()
    .setIdToken(idToken)
    .setExpiresIn(expiresIn)
    .setRefreshToken(refreshToken)
    .setTokenType(tokenType)
    .build()

fun SncfApiTokenProto.toDomain() = SncfApiToken(
    idToken = idToken,
    expiresIn = expiresIn,
    refreshToken = refreshToken,
    tokenType = tokenType
)

fun SncfApiTokenDto.toDomain() = SncfApiToken(
    idToken = idToken,
    expiresIn = expiresIn,
    refreshToken = refreshToken,
    tokenType = tokenType
)

fun SncfApiAuthentication.toProto() = SncfApiAuthenticationProto.newBuilder()
    .setSncfApiToken(sncfApiToken.toProto())
    .setCookies(cookies)
    .build()