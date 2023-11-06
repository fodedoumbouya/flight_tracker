package com.example.flight_tracker.viewModel

import android.icu.util.Calendar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flight_tracker.Airport
import java.util.Date

/**
 * @author by Idricealy on 05/11/2023
 */
class HomeFragmentViewModel: ViewModel() {
    private var _isDeparture = MutableLiveData<Boolean>()
    fun isDeparture(): MutableLiveData<Boolean> {
        return _isDeparture
    }

    private var _airport = MutableLiveData<Airport>()

    fun airport(): MutableLiveData<Airport> {
        return _airport
    }

    private var _startDate = MutableLiveData<Calendar>()

    fun startDate(): MutableLiveData<Calendar> {
        return _startDate
    }

    private var _endDate = MutableLiveData<Date>()

    fun endDate(): MutableLiveData<Date> {
        return _endDate
    }

}