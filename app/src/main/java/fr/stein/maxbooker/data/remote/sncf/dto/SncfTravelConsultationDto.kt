package fr.stein.maxbooker.data.remote.sncf.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SncfTravelConsultationDto(
    val arrivalDateTime: String,
    val avantage: Boolean,
    val coachNumber: String,
    val departureDateTime: String,
    val destination: SncfStationDto,
    val dvNumber: String,
    val orderId: String,
    val origin: SncfStationDto,
    val reservationDate: String,
    val seatNumber: String,
    val serviceItemId: String,
    val trainNumber: String,
    val transporterCode: String,
    val travelClass: String,
    val travelConfirmed: String,
    val travelStatus: String
)
