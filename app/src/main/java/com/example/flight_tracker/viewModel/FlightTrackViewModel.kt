package com.example.flight_tracker.viewModel

import android.icu.util.Calendar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flight_tracker.models.openSkyApiModels.AirportModel

/**
 * @author by Idricealy on 05/11/2023
 */
class FlightTrackViewModel: ViewModel() {

    private var _isChecked = MutableLiveData<Boolean>()
    fun isChecked(): MutableLiveData<Boolean> {
        return _isChecked
    }

    private var _airport = MutableLiveData<AirportModel>()

    fun airport(): MutableLiveData<AirportModel> {
        return _airport
    }

    private var _startDate = MutableLiveData<Calendar>()

    fun startDate(): MutableLiveData<Calendar> {
        return _startDate
    }

    private var _endDate = MutableLiveData<Calendar>()

    fun endDate(): MutableLiveData<Calendar> {
        return _endDate
    }

}