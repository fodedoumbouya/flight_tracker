package com.example.flight_tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flight_tracker.databinding.ActivityFlightListBinding
import com.example.flight_tracker.pages.flights.FlightListFragment
import com.example.flight_tracker.pages.maps.FlightViewMapsFragment
import com.example.flight_tracker.viewModel.FlightListViewModel

class FlightListActivity : AppCompatActivity() {
    private var isLargeScreen = false
    private var isMapFragmentVisible = false
    private lateinit var viewModel : FlightListViewModel
    private lateinit var binding: ActivityFlightListBinding
    private val flightsListFragment by lazy { FlightListFragment() }
    private val flightViewMapsFragment by lazy { FlightViewMapsFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlightListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isLargeScreen = findViewById<FragmentContainerView>(R.id.fragment_map_container) != null
        val fragmentManager = supportFragmentManager
        val fragmentLittleScreen = fragmentManager.findFragmentById(R.id.fragment_list_container)
        isMapFragmentVisible = fragmentLittleScreen is FlightViewMapsFragment

        if (isLargeScreen) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_list_container, flightsListFragment)
                .replace(R.id.fragment_map_container, flightViewMapsFragment)
                .commit()
        }


        val startDate = intent.getIntExtra("START_DATE", 0)
        val endDate = intent.getIntExtra("END_DATE", 0)
        val isDeparture = intent.getBooleanExtra("IS_DEPARTURE", false)
        val icao = intent.getStringExtra("ICAO")


        viewModel = ViewModelProvider(this).get(FlightListViewModel::class.java)

        viewModel.getFlights(icao !!, startDate, endDate, isDeparture)

        viewModel.getClickedFlightLiveData().observe(this, Observer {

            if (it != null) {
                showMapFragment()
            }
        })
    }

    override fun onBackPressed() {
        viewModel.resetClickedFlightLiveData()
        when {
            !isLargeScreen && isMapFragmentVisible -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_list_container, flightsListFragment)
                    .commit()
                isMapFragmentVisible = false
            }
            else -> super.onBackPressed()
        }
    }



    fun showMapFragment() {
        if (!isLargeScreen) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_list_container, flightViewMapsFragment)
                .commit()
            isMapFragmentVisible = true
        }
    }
}