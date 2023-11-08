package com.example.flight_tracker.viewModel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flight_tracker.data.DataManager
import com.example.flight_tracker.network.RequestListener
import com.example.flight_tracker.pages.dialog.DialogFragmentCustom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * @author by Idricealy on 08/11/2023
 */
class DetailsViewModel : ViewModel() {
    private var _searchUiState = MutableLiveData<RequestListener<String>>()

    fun searchUiState(): MutableLiveData<RequestListener<String>> {
        return _searchUiState
    }

    private var _stringData = MutableLiveData<String>()

    fun stringData() : MutableLiveData<String> {
        return _stringData
    }

    fun doRequest(icao : String, startDate : Int, endDate: Int, isChecked : Boolean, responseAlwaysOK : Boolean)  {
        _searchUiState
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = DataManager.doRequest(
                    icao,
                    startDate,
                    endDate,
                    isChecked,
                    responseAlwaysOK
                )

                when(result) {
                    is RequestListener.Loading -> {

                    }
                    is RequestListener.Success -> {
                        _searchUiState.postValue(RequestListener.Success(result.data))
                    }
                    is RequestListener.Error -> {
                        _searchUiState.postValue(RequestListener.Error(result.exception))
                    }
                    is RequestListener.Failed -> {
                        _searchUiState.postValue(RequestListener.Failed(result.message))
                    }
                }


            }
        }
    }

}