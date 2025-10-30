package fr.stein.maxbooker.data.mapper

import fr.stein.maxbooker.data.local.database.SncfReservationEntity
import fr.stein.maxbooker.data.local.database.SncfReservationWithStations
import fr.stein.maxbooker.data.local.database.SncfStationEntity
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerCardProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.remote.sncf.dto.SncfCustomerDto
import fr.stein.maxbooker.data.remote.sncf.dto.SncfStationDto
import fr.stein.maxbooker.data.remote.sncf.dto.SncfTravelConsultationDto
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomerCard
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfStation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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

fun SncfTravelConsultationDto.toDomain(): SncfReservation = SncfReservation(
    arrivalDateTime = LocalDateTime.parse(
        arrivalDateTime,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    ).atZone(
        ZoneId.of("Europe/Paris")
    ),
    avantage = avantage,
    coachNumber = coachNumber,
    departureDateTime = LocalDateTime.parse(
        departureDateTime,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    ).atZone(
        ZoneId.of("Europe/Paris")
    ),
    destination = destination.toDomain(),
    dvNumber = dvNumber,
    orderId = orderId,
    origin = origin.toDomain(),
    reservationDate = LocalDateTime.parse(
        reservationDate,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    ).atZone(
        ZoneId.of("Europe/Paris")
    ),
    seatNumber = seatNumber,
    serviceItemId = serviceItemId,
    trainNumber = trainNumber,
    travelClass = travelClass,
    travelConfirmed = travelConfirmed,
    travelStatus = travelStatus
)

fun SncfStationDto.toDomain(): SncfStation = SncfStation(
    label = label,
    rrCode = rrCode
)

fun SncfReservationWithStations.toDomain(): SncfReservation = SncfReservation(
    arrivalDateTime = reservation.arrivalDateTime,
    avantage = reservation.avantage,
    coachNumber = reservation.coachNumber,
    departureDateTime = reservation.departureDateTime,
    destination = destination.toDomain(),
    dvNumber = reservation.dvNumber,
    orderId = reservation.orderId,
    origin = origin.toDomain(),
    reservationDate = reservation.reservationDate,
    seatNumber = reservation.seatNumber,
    serviceItemId = reservation.serviceItemId,
    trainNumber = reservation.trainNumber,
    travelClass = reservation.travelClass,
    travelConfirmed = reservation.travelConfirmed,
    travelStatus = reservation.travelStatus,
    amount = reservation.amount,
    exchangeable = reservation.exchangeable,
    refundable = reservation.refundable,
    seat = reservation.seat?.toDomain(),
    tcn = reservation.tcn,
    transportationServiceOffer = reservation.transportationServiceOffer
)

fun SncfReservationEntity.Seat.toDomain(): SncfReservation.Seat = SncfReservation.Seat(
    facingForward = facingForward,
    seatPosition = seatPosition,
    spaceType = spaceType,
    tgvDeck = tgvDeck
)

fun SncfStationEntity.toDomain(): SncfStation = SncfStation(
    label = label,
    rrCode = rrCode
)

fun SncfReservation.toEntity(): SncfReservationEntity = SncfReservationEntity(
    arrivalDateTime = arrivalDateTime,
    avantage = avantage,
    coachNumber = coachNumber,
    departureDateTime = departureDateTime,
    destination = destination.rrCode,
    dvNumber = dvNumber,
    orderId = orderId,
    origin = origin.rrCode,
    reservationDate = reservationDate,
    seatNumber = seatNumber,
    serviceItemId = serviceItemId,
    trainNumber = trainNumber,
    travelClass = travelClass,
    travelConfirmed = travelConfirmed,
    travelStatus = travelStatus,
    amount = amount,
    exchangeable = exchangeable,
    refundable = refundable,
    seat = seat?.toEntity(),
    tcn = tcn,
    transportationServiceOffer = transportationServiceOffer
)

fun SncfReservation.Seat.toEntity(): SncfReservationEntity.Seat = SncfReservationEntity.Seat(
    facingForward = facingForward,
    seatPosition = seatPosition,
    spaceType = spaceType,
    tgvDeck = tgvDeck
)

fun SncfStation.toEntity(): SncfStationEntity = SncfStationEntity(
    rrCode = rrCode,
    label = label
)
