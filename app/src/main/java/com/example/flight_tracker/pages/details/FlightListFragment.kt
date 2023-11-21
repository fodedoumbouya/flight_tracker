package com.example.flight_tracker.pages.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flight_tracker.FlightListAdapter
import com.example.flight_tracker.databinding.FragmentFlightListBinding
import com.example.flight_tracker.models.openSkyApiModels.FlightModel
import com.example.flight_tracker.network.RequestListener
import com.example.flight_tracker.pages.dialog.DialogFragmentCustom
import com.example.flight_tracker.viewModel.DetailsViewModel
import kotlin.properties.Delegates

/**
 * @author by Idricealy on 08/11/2023
 */
class FlightListFragment : Fragment() {
    companion object {
        const val ICAO = "icao"
        const val END_DATE = "endDate"
        const val START_DATE = "startDate"
        const val IS_DEPARTURE = "isDeparture"
    }

    private var TAG = FlightListFragment::class.java.simpleName

    private var _binding: FragmentFlightListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DetailsViewModel
    private lateinit var stringData : TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var icao : String
    private var startDate by Delegates.notNull<Int>()
    private var endDate : Int = 0
    private var isDeparture by Delegates.notNull<Boolean>()
    // List flight to use to use by recycler view to display data
    private lateinit var flightsList : MutableList<FlightModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            icao = it.getString(ICAO).toString()
            startDate = it.getInt(START_DATE)
            endDate = it.getInt(END_DATE)
            isDeparture = it.getBoolean(IS_DEPARTURE)
        }
    }
    private fun init() {
        viewModel = ViewModelProvider(requireActivity()).get(DetailsViewModel::class.java)
        stringData = binding.stringData
        progressBar = binding.progressBarDetails
        viewModel.flightsUiState().value = RequestListener.Loading("Loading...")
        flightsList = mutableListOf<FlightModel>()
        viewModel.getFlights(icao, startDate, endDate, isDeparture)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()

        viewModel.flightsList().observe(viewLifecycleOwner) {
            flightsList.addAll(it)



            val recyclerView: RecyclerView = binding.flightListRecyclerviewId
            val adapter = FlightListAdapter(flightsList) { clickedFlight ->

                val openMapsFragment = FlightListFragmentDirections.openMaps()
                findNavController().navigate(openMapsFragment)
            }
            recyclerView.layoutManager = LinearLayoutManager(this.context)
            recyclerView.adapter = adapter
        }

        // update UI depending on getFlights request
        viewModel.flightsUiState().observe(viewLifecycleOwner) {
            when(it) {
                is RequestListener.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    stringData.visibility = View.GONE
                }
                is RequestListener.Success -> {
                    progressBar.visibility = View.GONE
                    //don't forget to delete this string
                    // this thing is here just for example to display data
                    stringData.apply {
                        visibility = View.GONE
                        text = it.toString()
                    }
                }
                is RequestListener.Error -> {
                    progressBar.visibility = View.GONE
                    DialogFragmentCustom(false,"Failed during the request, check your connection or the API is not available now.", "Try again").show(
                        parentFragmentManager, "DialogFragmentError"
                    )
                }
                is RequestListener.Failed -> {
                    progressBar.visibility = View.GONE
                    DialogFragmentCustom(false, it.message, "Cancel").show(
                        parentFragmentManager, "DialogFragmentFailed"
                    )
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFlightListBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}