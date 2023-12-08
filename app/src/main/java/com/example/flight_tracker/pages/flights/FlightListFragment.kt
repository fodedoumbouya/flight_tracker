package com.example.flight_tracker.pages.flights

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flight_tracker.FlightListAdapter
import com.example.flight_tracker.R
import com.example.flight_tracker.commom.Utils
import com.example.flight_tracker.models.openSkyApiModels.AirportModel
import com.example.flight_tracker.models.openSkyApiModels.FlightModel
import com.example.flight_tracker.network.RequestListener
import com.example.flight_tracker.pages.dialog.DialogFragmentCustom
import com.example.flight_tracker.viewModel.FlightListViewModel

class FlightListFragment : Fragment(), FlightListAdapter.OnCellClickListener, DialogFragmentCustom.CustomDialogListener {
    private lateinit var viewModel: FlightListViewModel
    private lateinit var progressBar : ProgressBar
    private lateinit var flightsList : MutableList<FlightModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        viewModel.flightsList().observe(requireActivity()) {
            flightsList.addAll(it)

            val recyclerView: RecyclerView = view.findViewById<RecyclerView>(R.id.flight_list_recyclerview_id)
            val airportMap: Map<String, AirportModel> = Utils.generateAirportList().toTypedArray().associateBy { it.icao }
            recyclerView.adapter = FlightListAdapter(it, airportMap,this )
            recyclerView.layoutManager = LinearLayoutManager(this.context)
        }

        viewModel.flightsUiState().observe(requireActivity()) {
            when(it) {
                is RequestListener.Loading -> {}

                is RequestListener.Success -> {
                    progressBar.visibility = View.GONE
                }
                is RequestListener.Error -> {
                    progressBar.visibility = View.GONE
                    if (isAdded && parentFragmentManager != null) {
                        DialogFragmentCustom("Failed during the request, check your connection or the API is not available now.", "Try again", this).show(
                            parentFragmentManager, "DialogFragmentError"
                        )
                    }

                }
                is RequestListener.Failed -> {
                    progressBar.visibility = View.GONE
                    if (isAdded && parentFragmentManager != null) {
                        DialogFragmentCustom(it.message, "Cancel", this).show(
                            parentFragmentManager, "DialogFragmentFailed"
                        )
                    }
                }
            }
        }
    }

    override fun onCellClicked(flightModel: FlightModel) {
        viewModel.setClickedFlightLiveData(flightModel)
    }

    override fun onNegativeButtonClickDialogFragment() {
        val activity = activity

        if (activity != null && activity is AppCompatActivity) {
            activity.onBackPressed()
        }
    }
}