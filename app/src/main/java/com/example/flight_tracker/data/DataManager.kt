package com.example.flight_tracker.data

import android.util.Log
import com.example.flight_tracker.network.OpenSkiApiService
import com.example.flight_tracker.network.RequestListener

/**
 * @author by Idricealy on 07/11/2023
 */
object DataManager {

    suspend fun doRequest(airport: String, begin: Int, end: Int, isDeparture: Boolean, responseAlwaysOK : Boolean): RequestListener<*> {
        val result = OpenSkiApiService.doSearchRequest(airport, begin, end, isDeparture, responseAlwaysOK)

        return result
    }
}