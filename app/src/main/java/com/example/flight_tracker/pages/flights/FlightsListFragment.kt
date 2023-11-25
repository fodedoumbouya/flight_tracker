package com.example.flight_tracker.pages.flights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flight_tracker.FlightListActivity
import com.example.flight_tracker.FlightListAdapter
import com.example.flight_tracker.R
import com.example.flight_tracker.models.openSkyApiModels.FlightModel
import com.example.flight_tracker.network.RequestListener
import com.example.flight_tracker.pages.dialog.DialogFragmentCustom
import com.example.flight_tracker.viewModel.FlightListViewModel

class FlightsListFragment : Fragment() {
    private lateinit var viewModel: FlightListViewModel
    private lateinit var progressBar : ProgressBar
    private lateinit var flightsList : MutableList<FlightModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_flights_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progress_bar_details)
        viewModel = ViewModelProvider(requireActivity()).get(FlightListViewModel::class.java)
        flightsList = mutableListOf<FlightModel>()

        viewModel.flightsUiState().value = RequestListener.Loading("Loading...")

        viewModel.flightsList().observe(viewLifecycleOwner) {
            flightsList.addAll(it)


            val recyclerView: RecyclerView = view.findViewById<RecyclerView>(R.id.flight_list_recyclerview_id)
            val adapter = FlightListAdapter(flightsList) { clickedFlight ->
                viewModel.setClickedFlightLiveData(clickedFlight)
                (requireActivity() as FlightListActivity).showMapFragment()
            }
            recyclerView.layoutManager = LinearLayoutManager(this.context)
            recyclerView.adapter = adapter
        }

        viewModel.flightsUiState().observe(viewLifecycleOwner) {
            when(it) {
                is RequestListener.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is RequestListener.Success -> {
                    progressBar.visibility = View.GONE
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


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FlightsListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}