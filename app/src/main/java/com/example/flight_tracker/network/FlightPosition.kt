//package com.example.flight_tracker.network
//
//import com.example.flight_tracker.commom.Utils
//import com.example.flight_tracker.models.openSkyApiModels.FlightData
//import com.google.gson.Gson
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Query
//import java.io.IOException
//
//interface FlightService {
//    @GET("tracks/all")
//    suspend fun getFlightData(@Query("icao24") icao24: String): FlightData
//}
//
//object FlightRepository {
//    private val retrofit = Retrofit.Builder()
//        .baseUrl("https://opensky-network.org/api/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    private val service = retrofit.create(FlightService::class.java)
//
//    suspend fun fetchFlightData(icao24: String): FlightData {
//        return try {
//            // Make the API request using Retrofit
//            service.getFlightData(icao24)
//        } catch (e: IOException) {
//            // If the request fails, handle the exception and fetch data from the local JSON file
//            fetchFlightDataFromLocalJson()
//        }
//    }
//
//    private suspend fun fetchFlightDataFromLocalJson(): FlightData {
//        return withContext(Dispatchers.IO) {
//            // Read the data from the local JSON file in assets
//            val jsonString = Utils.assetsJsonFileToJsonArray("flight.json").toString()
//            Gson().fromJson(jsonString, FlightData::class.java)
//        }
//    }
//}
