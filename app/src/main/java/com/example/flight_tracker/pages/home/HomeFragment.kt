package com.example.flight_tracker.pages.home

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.flight_tracker.FlightListActivity
import com.example.flight_tracker.models.openSkyApiModels.AirportModel
import com.example.flight_tracker.R
import com.example.flight_tracker.commom.Utils
import com.example.flight_tracker.databinding.FragmentHomeBinding
import com.example.flight_tracker.pages.dialog.DialogFragmentCustom
import com.example.flight_tracker.viewModel.FlightTrackViewModel

/**
 * @author by Idricealy on 05/11/2023
 */
class HomeFragment : Fragment(){
    //Layout xml automatically bind
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val TAG = HomeFragment::class.java.simpleName

    private lateinit var viewModel : FlightTrackViewModel
    private lateinit var airportAutoCompleteTextView : AutoCompleteTextView
    private lateinit var airportItem : AirportModel
    private lateinit var listAirport : Array<out AirportModel>
    private lateinit var flightSearchStatus : TextView
    private lateinit var btnEditAirport: Button

    private fun init() {
        viewModel = ViewModelProvider(requireActivity()).get(FlightTrackViewModel::class.java)
        viewModel.isChecked().value = if (viewModel.isChecked().value != null) viewModel.isChecked().value else false
        airportAutoCompleteTextView = binding.autocompleteAirport
        listAirport = Utils.generateAirportList().toTypedArray()
        flightSearchStatus = binding.flightSearchStatus
        flightSearchStatus.text = getString(R.string.departure)

        btnEditAirport = binding.btnEditAirport

        ArrayAdapter<AirportModel>(requireContext(), android.R.layout.simple_list_item_1, listAirport).also { adapter ->
            airportAutoCompleteTextView.apply {
                setAdapter(adapter)
                threshold = 1
            }
        }

        setAirportEdit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        val btnSwitch : SwitchCompat = binding.btnSwitch
        val beginDatePickerEditText : EditText = binding.dateBegin
        val endDatePickerEditText : EditText = binding.dateEnd
        val btnValidate : Button = binding.btnValidate

        viewModel.isChecked().observe(viewLifecycleOwner) {
            btnSwitch.isChecked = it
        }

        btnSwitch.apply {
            setOnClickListener {
                if(isChecked) {
                    flightSearchStatus.text = getString(R.string.arrival)
                    viewModel.isChecked().value = true
                    Log.d(TAG, viewModel.isChecked().value.toString())
                } else {
                    flightSearchStatus.text = getString(R.string.departure)
                    viewModel.isChecked().value = false
                    Log.d(TAG, viewModel.isChecked().value.toString())
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

        viewModel.startDate().observe(viewLifecycleOwner) {
            beginDatePickerEditText.hint = Utils.dateToString(it.time)
        }

        viewModel.endDate().observe(viewLifecycleOwner) {
            endDatePickerEditText.hint = Utils.dateToString(it.time)
        }

        beginDatePickerEditText.setOnClickListener {
            val newFragment = DatePickerFragment(true, viewModel.startDate())

            newFragment.show(parentFragmentManager, "datePickerStart")
        }

        endDatePickerEditText.setOnClickListener {
            val newFragment = DatePickerFragment(false, viewModel.endDate())

            newFragment.show(parentFragmentManager, "datePickerEnd")
        }

        btnValidate.setOnClickListener {

            if(isDataCompleted()){


                val intent = Intent(requireActivity(), FlightListActivity::class.java)

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
                DialogFragmentCustom(true,"Please fill all input to search your flight", "Cancel").show(
                    parentFragmentManager, "DialogFragmentErrorLoad"
                )
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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