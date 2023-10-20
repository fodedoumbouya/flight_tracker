package com.example.flight_tracker.pages.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flight_tracker.R

class Details : Fragment() {
//    val mapboxAccessToken = getString(R.string.mapbox_access_token)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        Log.d("MapboxAccessToken", mapboxAccessToken)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }
}
