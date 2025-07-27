package fr.stein.maxbooker.data.mapper

import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerCardProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.remote.SncfCustomerCardDto
import fr.stein.maxbooker.data.remote.SncfCustomerDto
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.SncfCustomerCard
import java.time.LocalDate
import java.time.LocalDateTime

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
    createdAt = LocalDateTime.parse(createdAt),
    updatedAt = LocalDateTime.parse(updatedAt),
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
    pictureUpdate = LocalDateTime.parse(pictureUpdate),
    cniUpdate = LocalDateTime.parse(cniUpdate),
    cniType = cniType,
    cniValue = cniValue,
    cards = cards.map { it.toDomain() }
)

fun SncfCustomerCardDto.toDomain(): SncfCustomerCard = SncfCustomerCard(
    cardNumber = cardNumber,
    marketingCarrierRef = marketingCarrierRef,
    productType = productType,
    contractStatus = contractStatus,
    validityStartDate = LocalDate.parse(validityStartDate),
    validityEndDate = LocalDate.parse(validityEndDate),
    ticketlessIndicator = ticketlessIndicator
)

fun SncfCustomerProto.toDomain(): SncfCustomer? = if(iuc.isEmpty()) null else SncfCustomer(
    iuc = iuc,
    createdAt = LocalDateTime.parse(createdAt),
    updatedAt = LocalDateTime.parse(updatedAt),
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
    pictureUpdate = LocalDateTime.parse(pictureUpdate),
    cniUpdate = LocalDateTime.parse(cniUpdate),
    cniType = cniType,
    cniValue = cniValue,
    cards = cardsList.map { it.toDomain() }
)

fun SncfCustomerCardProto.toDomain(): SncfCustomerCard = SncfCustomerCard(
    cardNumber = cardNumber,
    marketingCarrierRef = marketingCarrierRef,
    productType = productType,
    contractStatus = contractStatus,
    validityStartDate = LocalDate.parse(validityStartDate),
    validityEndDate = LocalDate.parse(validityEndDate),
    ticketlessIndicator = ticketlessIndicator
)