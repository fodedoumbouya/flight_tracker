package com.example.flight_tracker.pages.home

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flight_tracker.Airport
import com.example.flight_tracker.R
import com.example.flight_tracker.Utils
import com.example.flight_tracker.databinding.FragmentHomeBinding
import com.example.flight_tracker.viewModel.HomeFragmentViewModel

/**
 * @author by Idricealy on 05/11/2023
 */
class HomeFragment : Fragment(){
    //Layout xml automatically bind
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val TAG = HomeFragment::class.java.simpleName

    private lateinit var viewModel : HomeFragmentViewModel
    private lateinit var airportAutoCompleteTextView : AutoCompleteTextView
    private lateinit var airportItem : Airport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity()).get(HomeFragmentViewModel::class.java)
        val airportAutoCompleteTextView : AutoCompleteTextView = binding.autocompleteAirport
        val btnSwitch : SwitchCompat = binding.btnSwitch
        val beginDatePickerEditText : EditText = binding.dateBegin
        val endDatePickerEditText : EditText = binding.dateEnd
        val flightSearchStatus : TextView = binding.flightSearchStatus

        val airportList = Utils.generateAirportList()
        val airportListNames = ArrayList<String>()

        for(airport in airportList) {
            airportListNames.add(airport.getFormattedName())
        }

        val listAirport: Array<out Airport> = Utils.generateAirportList().toTypedArray()

        btnSwitch.apply {
            isChecked = false
            setOnClickListener {
                if(isChecked) {
                    flightSearchStatus.text = getString(R.string.arrival)
                } else {
                    flightSearchStatus.text = getString(R.string.departure)
                }
            }
        }

        flightSearchStatus.text = getString(R.string.departure)

        ArrayAdapter<Airport>(requireContext(), android.R.layout.simple_list_item_1, listAirport).also { adapter ->
            airportAutoCompleteTextView.apply {
                setAdapter(adapter)
                threshold = 1
            }
        }

        airportAutoCompleteTextView.apply {
            setOnItemClickListener { parent, view, position, id ->
                airportItem = (airportAutoCompleteTextView.adapter.getItem(position) as Airport?)!!
                clearFocus()
            }
        }

        viewModel.startDate().observe(viewLifecycleOwner) {
            beginDatePickerEditText.hint = Utils.dateToString(it.time)
        }

        viewModel.endDate().observe(viewLifecycleOwner) {
            beginDatePickerEditText.hint = Utils.dateToString(it)
        }

        beginDatePickerEditText.setOnClickListener {

            val newFragment = DatePickerFragment(btnSwitch.isChecked,
                if (viewModel.startDate().value != null)
                    viewModel.startDate().value
                else
                    Calendar.getInstance() )
            newFragment.show(parentFragmentManager, "datePicker")
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
}