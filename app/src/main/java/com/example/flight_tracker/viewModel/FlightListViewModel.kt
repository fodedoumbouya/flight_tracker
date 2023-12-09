package com.example.flight_tracker.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flight_tracker.data.DataManager
import com.example.flight_tracker.models.openSkyApiModels.FlightModel
import com.example.flight_tracker.network.RequestListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author by Idricealy on 08/11/2023
 */
class FlightListViewModel : ViewModel() {
    private var _flightsUiState = MutableLiveData<RequestListener<String>>()
    private var _flightsList = MutableLiveData<List<FlightModel>>()
    private var clickedFlightLiveData = MutableLiveData<FlightModel?>()

    fun flightsList() : MutableLiveData<List<FlightModel>> {
        return _flightsList
    }
    fun flightsUiState(): MutableLiveData<RequestListener<String>> {
        return _flightsUiState
    }

    private var _stringData = MutableLiveData<String>()

    fun stringData() : MutableLiveData<String> {
        return _stringData
    }

    fun getClickedFlightLiveData(): MutableLiveData<FlightModel?> {
        return clickedFlightLiveData
    }

    fun setClickedFlightLiveData(flight: FlightModel) {
        clickedFlightLiveData.value = flight
    }

    fun resetClickedFlightLiveData() {
        clickedFlightLiveData.value = null
    }

    fun getFlights(icao : String, startDate : Int, endDate: Int, isChecked : Boolean)  {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = DataManager.getFlights(
                    icao,
                    startDate,
                    endDate,
                    isChecked)

                when(result) {
                    is RequestListener.Loading<*> -> {

                    }
                    is RequestListener.Success<*> -> {
                        val filteredFlights = requestResponseToList(result.data)
                            .filter {
                                it.estDepartureAirport != null  && it.estDepartureAirport != "" && it.estArrivalAirport != ""  && it.lastSeen != null
                                && it.icao24 != ""  && it.icao24 != null && it.estArrivalAirport != null  && it.firstSeen != null
                            }
                        _flightsUiState.postValue(RequestListener.Success(result.data))
                        _flightsList.postValue(filteredFlights)
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

    private fun requestResponseToList(strJson : String) : List<FlightModel>{
        val typeFlightModel = object : TypeToken<List<FlightModel>>() {}.type

        return Gson().fromJson(strJson, typeFlightModel)
    }

}