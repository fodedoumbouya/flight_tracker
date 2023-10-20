package com.example.flight_tracker;

import android.app.Activity;
import android.content.Intent

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.flight_tracker.pages.details.Details
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style


class MainActivity : AppCompatActivity() {

//    private lateinit var mapView: MapView

//    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))


        setContentView(R.layout.activity_main)
        // Call the Maps activity
//        startActivity(Intent(this, Details::class.java))
//
//        mapView = findViewById(R.id.mapView)
//        mapView?.onCreate(savedInstanceState)
//        mapView?.getMapAsync { mapboxMap ->
//
//            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
//// Map is set up and the style has loaded. Now you can add data or make other map adjustments
//
//
//            }
//
//        }
    }
}