package fr.stein.maxbooker.data.mapper

import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication

fun SncfApiAuthentication.toProto(): SncfApiAuthenticationProto =
    SncfApiAuthenticationProto.newBuilder()
        .setCookies(cookies)
        .build()

fun SncfApiAuthenticationProto.toDomain(): SncfApiAuthentication? = if (cookies.isEmpty()) {
    null
} else {
    SncfApiAuthentication(cookies)
}
