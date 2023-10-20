package com.example.flight_tracker.pages.maps;

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Property
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.flight_tracker.R
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class Maps : Fragment() {
    private var mapView: MapView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Mapbox with your access token
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))

        // linking the class to my fragment_maps layout
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        // mapping the maps to the maps layout inside the fragment_maps
        mapView = view.findViewById(R.id.mapView)
        // Config
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
//            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            mapboxMap.setStyle(
                Style.Builder()
                    .fromUri(getString(R.string.mapbox_style_url))
            ) {

                // Map is set up, and the style has loaded. Now you can add data or make other map adjustments
                // add button to the maps
                val zoomInButton = view.findViewById<Button>(R.id.zoomInButton)
                val zoomOutButton = view.findViewById<Button>(R.id.zoomOutButton)

                // Set up a click listener for the zoom in button
                zoomInButton.setOnClickListener {
                    mapboxMap.animateCamera(CameraUpdateFactory.zoomIn())
                }

                // Set up a click listener for the zoom out button
                zoomOutButton.setOnClickListener {
                    mapboxMap.animateCamera(CameraUpdateFactory.zoomOut())
                }

                val target:LatLng =  LatLng(48.864716, 2.349014)
                val  zoom:Double = 4.0

                // Set the initial map center and zoom level
                val cameraPosition = CameraPosition.Builder()
                    .target(target) // London coordinates
                    .zoom(zoom)
                    .build()

                mapboxMap.cameraPosition = cameraPosition

                    // Add two airports as markers
                val parisAirport = MarkerOptions()
                    .position(LatLng(49.0097, 2.5479)) // Paris Airport coordinates
                    .title("Paris Airport")
                    .snippet("Charles de Gaulle Airport")

                val lyonAirport = MarkerOptions()
                    .position(LatLng(45.7215, 5.0824)) // Lyon Airport coordinates
                    .title("Lyon Airport")
                    .snippet("Lyon-Saint Exupéry Airport")

                mapboxMap.addMarker(parisAirport)
                mapboxMap.addMarker(lyonAirport)


                // Create a LineString to draw the airplane route
                val lineString = LineString.fromLngLats(
                    listOf(
                        Point.fromLngLat(2.5479, 49.0097), // Paris Airport coordinates
                        Point.fromLngLat(5.0824, 45.7215)  // Lyon Airport coordinates
                    )
                )

                // Create a LineLayer with the LineString
                val lineLayer = LineLayer("line-layer", "line-source")
                lineLayer.setProperties(
                    PropertyFactory.lineDasharray(arrayOf(0.01f, 2f)),
                    PropertyFactory.lineCap(com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND),
                    PropertyFactory.lineWidth(4f),
                    PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                )

            // Add the LineLayer to the map
                mapboxMap.style?.addSource(GeoJsonSource("line-source", lineString))
                mapboxMap.style?.addLayer(lineLayer)


            }
        }

        return view
    }
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}