package fr.stein.maxbooker.data.mapper

import fr.stein.maxbooker.data.local.database.SncfReservationWithStations
import fr.stein.maxbooker.data.local.database.entity.SncfReservationEntity
import fr.stein.maxbooker.data.local.database.entity.SncfStationEntity
import fr.stein.maxbooker.data.remote.sncf.dto.SncfGetTravelDto
import fr.stein.maxbooker.data.remote.sncf.dto.SncfStationDto
import fr.stein.maxbooker.data.remote.sncf.dto.SncfTravelConsultationDto
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfStation
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun SncfTravelConsultationDto.toDomain(sncfCustomer: SncfCustomer): SncfReservation = SncfReservation(
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
    travelStatus = travelStatus,
    customerLastName = sncfCustomer.lastName
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
    customerLastName = reservation.customerLastName,
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
    transportationServiceOffer = transportationServiceOffer,
    customerLastName = customerLastName
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

fun SncfGetTravelDto.toDomain(sncfReservation: SncfReservation): SncfReservation = sncfReservation.updateDetails(
    amount = amount,
    exchangeable = exchangeable,
    refundable = refundable,
    seat = seat.toDomain(),
    tcn = tcn,
    transportationServiceOffer = transportationServiceOffer
)

fun SncfGetTravelDto.Seat.toDomain(): SncfReservation.Seat = SncfReservation.Seat(
    facingForward = facingForward,
    seatPosition = seatPosition,
    spaceType = spaceType,
    tgvDeck = tgvDeck
)
