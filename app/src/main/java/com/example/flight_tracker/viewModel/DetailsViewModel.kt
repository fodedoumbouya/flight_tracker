package com.example.flight_tracker.viewModel

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
class DetailsViewModel : ViewModel() {
    private var _flightsUiState = MutableLiveData<RequestListener<String>>()
    private var _flightsList = MutableLiveData<List<FlightModel>>()

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
                        _flightsUiState.postValue(RequestListener.Success(result.data))
                        _flightsList.postValue(requestResponseToList(result.data))
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