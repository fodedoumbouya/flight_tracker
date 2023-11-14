package com.example.flight_tracker.data

import com.example.flight_tracker.network.OpenSkiApiService
import com.example.flight_tracker.network.RequestListener

/**
 * @author by Idricealy on 07/11/2023
 */
object DataManager {

    suspend fun getFlights(
        airport: String,
        begin: Int,
        end: Int,
        isDeparture: Boolean
    ): RequestListener<*> {

        return OpenSkiApiService.getFlights(airport, begin, end, isDeparture)
    }
}