package com.example.flight_tracker.pages.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.flight_tracker.viewModel.FlightTrackViewModel

/**
 * @author by Idricealy on 05/11/2023
 *
 * https://developer.android.com/develop/ui/views/components/pickers
 */
class DatePickerFragment(private val isChecked: Boolean, private val chooseDate: Calendar?) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    val TAG = DatePickerFragment::class.java.simpleName
    private lateinit var viewModel : FlightTrackViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProvider(requireActivity()).get(FlightTrackViewModel::class.java)
        // Use the current date as the default date in the picker.
        var c = Calendar.getInstance()

        Log.d(TAG, chooseDate.toString() + "before")

        if (chooseDate != null) {
            c = chooseDate
        }

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        Log.d(TAG, chooseDate.toString())
        // Create a new instance of DatePickerDialog and return it.
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val newDate = Calendar.getInstance()
        newDate.set(year,month,day)

        if(!isChecked) {
            Log.d(TAG, "${viewModel.startDate().value?.timeInMillis} $isChecked start")

            viewModel.startDate().postValue(newDate)
        } else {
            Log.d(TAG, "$isChecked end")
            viewModel.endDate().postValue(newDate)
        }
    }

}