package fr.stein.maxbooker.data.mapper

import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerCardProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.remote.sncf.dto.SncfCustomerDto
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomerCard
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun SncfCustomer.toProto(): SncfCustomerProto = SncfCustomerProto.newBuilder()
    .setIuc(iuc)
    .setCreatedAt(createdAt.toString())
    .setUpdatedAt(updatedAt.toString())
    .setCivility(civility)
    .setLastName(lastName)
    .setFirstName(firstName)
    .setBirthDate(birthDate.toString())
    .setLanguage(language)
    .setAddress(address)
    .setZipCode(zipCode)
    .setCity(city)
    .setCountry(country)
    .setEmail(email)
    .setMobilePhone(mobilePhone)
    .setNsdStatus(nsdStatus)
    .setPictureStatus(pictureStatus)
    .setPictureUpdate(pictureUpdate.toString())
    .setCniUpdate(cniUpdate.toString())
    .setCniType(cniType)
    .setCniValue(cniValue)
    .addAllCards(cards.map { it.toProto() })
    .build()

fun SncfCustomerCard.toProto(): SncfCustomerCardProto = SncfCustomerCardProto.newBuilder()
    .setCardNumber(cardNumber)
    .setMarketingCarrierRef(marketingCarrierRef)
    .setProductType(productType)
    .setContractStatus(contractStatus)
    .setValidityStartDate(validityStartDate.toString())
    .setValidityEndDate(validityEndDate.toString())
    .build()

fun SncfCustomerDto.toDomain(): SncfCustomer = SncfCustomer(
    iuc = iuc,
    createdAt = LocalDateTime.parse(createdAt).atZone(ZoneId.of("Europe/Paris")),
    updatedAt = LocalDateTime.parse(updatedAt).atZone(ZoneId.of("Europe/Paris")),
    civility = civility,
    lastName = lastName,
    firstName = firstName,
    birthDate = LocalDate.parse(birthDate),
    language = language,
    address = address,
    zipCode = zipCode,
    city = city,
    country = country,
    email = email,
    mobilePhone = mobilePhone,
    nsdStatus = nsdStatus,
    pictureCounter = pictureCounter,
    pictureStatus = pictureStatus,
    maxTravelsPerDay = maxTravelsPerDay,
    pictureUpdate = LocalDateTime.parse(pictureUpdate).atZone(ZoneId.of("Europe/Paris")),
    cniUpdate = LocalDateTime.parse(cniUpdate).atZone(ZoneId.of("Europe/Paris")),
    cniType = cniType,
    cniValue = cniValue,
    cards = cards.map { it.toDomain() }
)

fun SncfCustomerDto.SncfCustomerCardDto.toDomain(): SncfCustomerCard = SncfCustomerCard(
    cardNumber = cardNumber,
    marketingCarrierRef = marketingCarrierRef,
    productType = productType,
    contractStatus = contractStatus,
    validityStartDate = LocalDate.parse(validityStartDate),
    validityEndDate = LocalDate.parse(validityEndDate),
    ticketlessIndicator = ticketlessIndicator
)

fun SncfCustomerProto.toDomain(): SncfCustomer? = if (iuc.isEmpty()) {
    null
} else {
    SncfCustomer(
        iuc = iuc,
        createdAt = ZonedDateTime.parse(createdAt),
        updatedAt = ZonedDateTime.parse(updatedAt),
        civility = civility,
        lastName = lastName,
        firstName = firstName,
        birthDate = LocalDate.parse(birthDate),
        language = language,
        address = address,
        zipCode = zipCode,
        city = city,
        country = country,
        email = email,
        mobilePhone = mobilePhone,
        nsdStatus = nsdStatus,
        pictureCounter = pictureCounter,
        pictureStatus = pictureStatus,
        maxTravelsPerDay = maxTravelsPerDay,
        pictureUpdate = ZonedDateTime.parse(pictureUpdate),
        cniUpdate = ZonedDateTime.parse(cniUpdate),
        cniType = cniType,
        cniValue = cniValue,
        cards = cardsList.map { it.toDomain() }
    )
}

fun SncfCustomerCardProto.toDomain(): SncfCustomerCard = SncfCustomerCard(
    cardNumber = cardNumber,
    marketingCarrierRef = marketingCarrierRef,
    productType = productType,
    contractStatus = contractStatus,
    validityStartDate = LocalDate.parse(validityStartDate),
    validityEndDate = LocalDate.parse(validityEndDate),
    ticketlessIndicator = ticketlessIndicator
)
