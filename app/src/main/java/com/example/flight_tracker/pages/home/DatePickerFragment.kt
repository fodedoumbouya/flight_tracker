package com.example.flight_tracker.pages.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData

/**
 * @author by Idricealy on 05/11/2023
 *
 * https://developer.android.com/develop/ui/views/components/pickers
 */
class DatePickerFragment(private val isBegin : Boolean,
                         private val chooseDate: MutableLiveData<Calendar>)
    : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker.
        var c = Calendar.getInstance()

        if (chooseDate.value != null) {
            c = chooseDate.value
        }

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it.
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val newDate = Calendar.getInstance()
        newDate.set(year,month,day)

        if(isBegin) {
            newDate.set(Calendar.HOUR_OF_DAY, 0)
            newDate.set(Calendar.MINUTE, 0)
            newDate.set(Calendar.SECOND, 1)
        } else {
            newDate.set(Calendar.HOUR_OF_DAY, 23)
            newDate.set(Calendar.MINUTE, 59)
            newDate.set(Calendar.SECOND, 59)
        }

        chooseDate.value = newDate
    }

}