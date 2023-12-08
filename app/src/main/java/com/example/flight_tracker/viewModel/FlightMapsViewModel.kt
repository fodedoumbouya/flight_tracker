package com.example.flight_tracker.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flight_tracker.data.DataManager
import com.example.flight_tracker.models.openSkyApiModels.FlightData
import com.example.flight_tracker.network.RequestListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import com.mapbox.mapboxsdk.geometry.LatLng

class FlightMapsViewModel : ViewModel() {

    private var _flightsListTracking = MutableLiveData<FlightData>()
    private var _flightsUiState = MutableLiveData<RequestListener<String>>()
    private var _flightsPosition = MutableLiveData<FlightData>()


    fun flightTracking() : MutableLiveData<FlightData> {
        return _flightsListTracking
    }

    fun flightLiveData() :MutableLiveData<FlightData> {
        return _flightsPosition
    }
    fun getFlights(icao : String)  {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = DataManager.getFlightsTrackPosition(
                    icao)

                when(result) {
                    is RequestListener.Loading<*> -> {

                    }
                    is RequestListener.Success<*> -> {
                        Log.d("message",result.data.toString())
                        _flightsUiState.postValue(RequestListener.Success(result.data))
                        _flightsListTracking.postValue(requestResponseToList(result.data))
                    }
                    is RequestListener.Error -> {
                        _flightsUiState.postValue(RequestListener.Error(result.exception))
                    }
                    is RequestListener.Failed -> {
                        _flightsUiState.postValue(RequestListener.Failed(result.message))
                    }
                }
            }
        }
    }

    fun getFlightsLivePosition(icao : String)  {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = DataManager.getFlightsPositionLive(
                    icao)

                when(result) {
                    is RequestListener.Loading<*> -> {

                    }
                    is RequestListener.Success<*> -> {
                        _flightsUiState.postValue(RequestListener.Success(result.data))
                        _flightsPosition.postValue(requestResponseToList(result.data))

                    }
                    is RequestListener.Error -> {
                        _flightsUiState.postValue(RequestListener.Error(result.exception))
                    }
                    is RequestListener.Failed -> {
                        _flightsUiState.postValue(RequestListener.Failed(result.message))
                    }
                }
            }
        }
    }

    private fun requestResponseToList(strJson : String) : FlightData{
        val typeFlightModel = object : TypeToken<FlightData>() {}.type

        return Gson().fromJson(strJson, typeFlightModel)
    }
}


