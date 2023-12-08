package com.example.flight_tracker.models.openSkyApiModels;

import com.google.gson.annotations.SerializedName


//data class FlightData(
//    val icao24: String,
//    val callsign: String,
//    val startTime: Long,
//    val endTime: Long,
//    val path: List<List<Any>>
//)


data class FlightData (

    @SerializedName("icao24"    ) var icao24    : String?                   = null,
    @SerializedName("callsign"  ) var callsign  : String?                   = null,
    @SerializedName("startTime" ) var startTime : Int?                      = null,
    @SerializedName("endTime"   ) var endTime   : Int?                      = null,
    @SerializedName("path"      ) var path      : ArrayList<ArrayList<Any>> = arrayListOf()

)