package com.example.flight_tracker.network

/**
 * @author by Idricealy on 07/11/2023
 *
 * using open sky api : https://openskynetwork.github.io/opensky-api/rest.html#
 */
object OpenSkiApiService {
    private const val BASE_URL = "https://opensky-network.org/api"
    private const val DEPARTURE_URL = "/flights/departure"
    private const val ARRIVAL_URL = "/flights/arrival"
    private const val TRACKS_URL = "/tracks/all"

    fun getFlights(
        airport: String,
        begin: Int,
        end: Int,
        isChecked: Boolean,
    ): RequestListener<*> {
        val paramsList = mutableMapOf<String, Any>()
        val departureOrArrivalUrl = if (isChecked) ARRIVAL_URL else DEPARTURE_URL

        paramsList.put("airport", airport)
        paramsList.put("begin", begin)
        paramsList.put("end", end)

        return RequestManager.get(
            BASE_URL + departureOrArrivalUrl,
            paramsList,
        )
    }

    fun getFlightsPosition(
        icao24: String,

    ): RequestListener<*> {
        val paramsList = mutableMapOf<String, Any>()
        val departureOrArrivalUrl = TRACKS_URL

        paramsList.put("icao24", icao24)

        return RequestManager.get(
            BASE_URL + departureOrArrivalUrl,
            paramsList,
        )
    }

    fun getFlightsPositionLive(
        icao24: String,

        ): RequestListener<*> {
        val paramsList = mutableMapOf<String, Any>()
        val departureOrArrivalUrl = TRACKS_URL

        paramsList.put("icao24", icao24)

        return RequestManager.get(
            BASE_URL + departureOrArrivalUrl,
            paramsList,
        )
    }

}