package com.example.flight_tracker.data

import com.example.flight_tracker.commom.Utils
import com.example.flight_tracker.network.OpenSkiApiService
import com.example.flight_tracker.network.RequestListener

/**
 * @author by Idricealy on 07/11/2023
 */
object DataManager {

    fun getFlights(
        airport: String,
        begin: Int,
        end: Int,
        isDeparture: Boolean
    ): RequestListener<*> {

        return when(val requestListener = OpenSkiApiService.getFlights(airport, begin, end, isDeparture)) {
            // If response API is 500, we use sample data
            is RequestListener.Error -> {
                RequestListener.Success<String>(Utils.assetsJsonFileToJsonArray("flights_list_sample.json").toString())
            }

            else -> {
                requestListener
            }
        }
    }
}