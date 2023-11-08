package com.example.flight_tracker.models.openSkyApiModels

import com.google.gson.annotations.SerializedName

/**
 * @author by Idricealy on 07/11/2023
 */
class DepartureArrivalModel {
    @SerializedName("icao24") var icao24: String? = null
    @SerializedName("firstSeen") var firstSeen: Int?= null
    @SerializedName("estDepartureAirport") var estDepartureAirport: String? = null
    @SerializedName("lastSeen") var lastSeen: Int? = null
    @SerializedName("estArrivalAirport") var estArrivalAirport: String? = null
    @SerializedName("callsign") var callsign: String? = null
    @SerializedName("estDepartureAirportHorizDistance") var estDepartureAirportHorizDistance : Int?    = null
    @SerializedName("estDepartureAirportVertDistance") var estDepartureAirportVertDistance  : Int?    = null
    @SerializedName("estArrivalAirportHorizDistance") var estArrivalAirportHorizDistance   : String? = null
    @SerializedName("estArrivalAirportVertDistance") var estArrivalAirportVertDistance    : String? = null
    @SerializedName("departureAirportCandidatesCount") var departureAirportCandidatesCount  : Int?    = null
    @SerializedName("arrivalAirportCandidatesCount") var arrivalAirportCandidatesCount    : Int?    = null

}