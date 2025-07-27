package fr.stein.maxbooker.data.mapper

import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiTokenProto
import fr.stein.maxbooker.data.remote.SncfApiTokenDto
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiToken

fun SncfApiToken.toProto(): SncfApiTokenProto = SncfApiTokenProto
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

fun SncfApiAuthentication.toProto(): SncfApiAuthenticationProto = SncfApiAuthenticationProto.newBuilder()
    .setSncfApiToken(sncfApiToken.toProto())
    .setCookies(cookies)
    .build()

fun SncfApiAuthenticationProto.toDomain(): SncfApiAuthentication? = if(cookies.isEmpty()) null else SncfApiAuthentication(
    sncfApiToken.toDomain(),
    cookies
)
