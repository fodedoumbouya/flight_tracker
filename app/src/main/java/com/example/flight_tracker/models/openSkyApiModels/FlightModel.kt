package com.example.flight_tracker.models.openSkyApiModels


data class FlightModel (
    val icao24: String,
                        val firstSeen: Long,
                        val estDepartureAirport: String,
                        val lastSeen: Long,
                        val estArrivalAirport: String,
                        val callsign: String,
                        val estDepartureAirportHorizDistance: Int,
                        val estDepartureAirportVertDistance: Int,
                        val estArrivalAirportHorizDistance: Int,
                        val estArrivalAirportVertDistance: Int,
                        val departureAirportCandidatesCount: Int,
                        val arrivalAirportCandidatesCount: Int
)