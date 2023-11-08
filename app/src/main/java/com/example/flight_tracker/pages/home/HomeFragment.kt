package com.example.flight_tracker.pages.home

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
    private lateinit var getAirportList : List<AirportModel>
    private lateinit var flightSearchStatus : TextView

    private fun init() {
        viewModel = ViewModelProvider(requireActivity()).get(FlightTrackViewModel::class.java)
        viewModel.isChecked().value = false
        airportAutoCompleteTextView = binding.autocompleteAirport
        listAirport = Utils.generateAirportList().toTypedArray()
        flightSearchStatus = binding.flightSearchStatus
        flightSearchStatus.text = getString(R.string.departure)

        ArrayAdapter<AirportModel>(requireContext(), android.R.layout.simple_list_item_1, listAirport).also { adapter ->
            airportAutoCompleteTextView.apply {
                setAdapter(adapter)
                threshold = 1
            }
        }
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

        airportAutoCompleteTextView.apply {
            setOnDismissListener {
                airportAutoCompleteTextView.clearFocus()
            }
            setOnItemClickListener { parent, view, position, id ->
                airportItem = (airportAutoCompleteTextView.adapter.getItem(position) as AirportModel?)!!
                viewModel.airport().value = airportItem
                clearFocus()
            }
        }

        viewModel.startDate().observe(viewLifecycleOwner) {
            beginDatePickerEditText.hint = Utils.dateToString(it.time)
        }

        viewModel.endDate().observe(viewLifecycleOwner) {
            endDatePickerEditText.hint = Utils.dateToString(it.time)
        }

        beginDatePickerEditText.setOnClickListener {
            val newFragment = DatePickerFragment(btnSwitch.isChecked,
                if (viewModel.startDate().value != null)
                    viewModel.startDate().value
                else
                    Calendar.getInstance() )
            newFragment.show(parentFragmentManager, "datePickerStart")
        }

        endDatePickerEditText.setOnClickListener {
            val newFragment = DatePickerFragment(!btnSwitch.isChecked,
                if(viewModel.endDate().value != null)
                    viewModel.endDate().value
                else
                    Calendar.getInstance() )

            newFragment.show(parentFragmentManager, "datePickEnd")
        }

        btnValidate.setOnClickListener {

            if(isDataCompleted()){
                val openDetailsFragment = HomeFragmentDirections.openListFlight(
                    viewModel.airport().value?.icao.toString(),
                    (viewModel.startDate().value?.timeInMillis!! / 1000).toInt(),
                    (viewModel.endDate().value?.timeInMillis!! / 1000).toInt(),
                    viewModel.isChecked().value!!
                )

                findNavController().navigate(openDetailsFragment)
            } else {
                DialogFragmentCustom("Please fill all input to search your flight", "Cancel").show(
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
}