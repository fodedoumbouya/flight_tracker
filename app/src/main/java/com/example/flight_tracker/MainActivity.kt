package com.example.flight_tracker;


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.flight_tracker.commom.Utils
import com.example.flight_tracker.models.openSkyApiModels.AirportModel
import com.example.flight_tracker.pages.dialog.DialogFragmentCustom
import com.example.flight_tracker.pages.home.DatePickerFragment
import com.example.flight_tracker.viewModel.FlightTrackViewModel


class MainActivity : AppCompatActivity(){


    private lateinit var viewModel : FlightTrackViewModel
    private lateinit var airportAutoCompleteTextView : AutoCompleteTextView
    private lateinit var airportItem : AirportModel
    private lateinit var listAirport : Array<out AirportModel>
    private lateinit var flightSearchStatus : TextView
    private lateinit var btnEditAirport: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        val btnSwitch : SwitchCompat = findViewById(R.id.btn_switch)
        val beginDatePickerEditText : EditText = findViewById(R.id.date_begin)
        val endDatePickerEditText : EditText = findViewById(R.id.date_end)
        val btnValidate : Button = findViewById(R.id.btn_validate)

        viewModel.isChecked().observe(this) {
            btnSwitch.isChecked = it
        }

        btnSwitch.apply {
            setOnClickListener {
                if(isChecked) {
                    flightSearchStatus.text = getString(R.string.arrival)
                    viewModel.isChecked().value = true
                } else {
                    flightSearchStatus.text = getString(R.string.departure)
                    viewModel.isChecked().value = false
                }
            }
        }

        btnEditAirport.apply {
            setOnClickListener {
                airportAutoCompleteTextView.apply {
                    text.clear()
                    viewModel.airport().value = null
                    isEnabled = true
                }
                visibility = View.GONE
            }
        }

        airportAutoCompleteTextView.apply {
            setOnItemClickListener { _, _, position, _ ->
                airportItem = (adapter.getItem(position) as AirportModel?)!!
                viewModel.airport().value = airportItem
                setAirportEdit()
            }
        }

        viewModel.startDate().observe(this) {
            beginDatePickerEditText.hint = Utils.dateToString(it.time)
        }

        viewModel.endDate().observe(this) {
            endDatePickerEditText.hint = Utils.dateToString(it.time)
        }

        beginDatePickerEditText.setOnClickListener {
            val newFragment = DatePickerFragment(true, viewModel.startDate())
            newFragment.show(supportFragmentManager, "datePickerStart")
        }

        endDatePickerEditText.setOnClickListener {
            val newFragment = DatePickerFragment(false, viewModel.endDate())
            newFragment.show(supportFragmentManager, "datePickerEnd")
        }

        btnValidate.setOnClickListener {

            if(isDataCompleted()){

                val intent = Intent(this, FlightListActivity::class.java)

                val endDate = (viewModel.endDate().value?.timeInMillis!! / 1000).toInt()
                val icao = viewModel.airport().value?.icao.toString()
                val startDate = (viewModel.startDate().value?.timeInMillis!! / 1000).toInt()
                val isDeparture = viewModel.isChecked().value!!

                intent.putExtra("ICAO", icao)
                intent.putExtra("END_DATE", endDate)
                intent.putExtra("START_DATE", startDate)
                intent.putExtra("IS_DEPARTURE", isDeparture)

                startActivity(intent)

            } else {
//                DialogFragmentCustom("Please fill all input to search your flight", "Ok, I get it", this).show(
//                    supportFragmentManager, "DialogFragmentErrorLoad"
//                )

                DialogFragmentCustom.newInstance(
                    "Please fill all input to search your flight",
                    "Ok, I get it",
                    object : DialogFragmentCustom.CustomDialogListener {
                        override fun onNegativeButtonClickDialogFragment() {

                        }
                    }
                ).show(supportFragmentManager, "DialogFragmentErrorLoad")
            }
        }

    }

    private fun init() {
        viewModel = ViewModelProvider(this).get(FlightTrackViewModel::class.java)
        viewModel.isChecked().value = if (viewModel.isChecked().value != null) viewModel.isChecked().value else false
        airportAutoCompleteTextView = findViewById(R.id.autocomplete_airport)
        listAirport = Utils.generateAirportList().toTypedArray()
        flightSearchStatus = findViewById(R.id.flight_search_status)
        flightSearchStatus.text = getString(R.string.departure)

        btnEditAirport = findViewById(R.id.btn_edit_airport)

        ArrayAdapter<AirportModel>(this, android.R.layout.simple_list_item_1, listAirport).also { adapter ->
            airportAutoCompleteTextView.apply {
                setAdapter(adapter)
                threshold = 1
            }
        }

        setAirportEdit()
    }
    private fun isDataCompleted() : Boolean{
        return viewModel.airport().value != null &&
                viewModel.startDate().value != null &&
                viewModel.endDate().value != null
    }
    private fun setAirportEdit(){
        if (viewModel.airport().value != null) {
            airportAutoCompleteTextView.isEnabled = false
            btnEditAirport.visibility = View.VISIBLE
        }
    }
}