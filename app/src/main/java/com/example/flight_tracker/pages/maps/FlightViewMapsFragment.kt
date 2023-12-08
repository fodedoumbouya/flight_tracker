package com.example.flight_tracker.pages.maps

import FlightDetailsBottomSheetFragment
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flight_tracker.R
import com.example.flight_tracker.models.openSkyApiModels.FlightData
import com.example.flight_tracker.models.openSkyApiModels.FlightModel
import com.example.flight_tracker.viewModel.FlightListViewModel
import com.example.flight_tracker.viewModel.FlightMapsViewModel
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.launch
import kotlin.random.Random

class FlightViewMapsFragment : Fragment(), FlightDetailsBottomSheetFragment.UpdateDataListener {
    private var mapView: MapView? = null
    private var zoom:Double = 2.0
    private lateinit var viewModel: FlightListViewModel
    private lateinit var viewTrackingModel: FlightMapsViewModel
    private lateinit var flightTracking : FlightData
    private lateinit var postionFlight : List<LatLng>
    private lateinit var mapboxMap: MapboxMap
    private lateinit var  flightInfo: FlightModel
    private var bottomSheetFragment: FlightDetailsBottomSheetFragment? = null
    // Maintain a list to keep track of added markers
    private val markersList = mutableListOf<Marker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Mapbox with your access token
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))

        // linking the class to my fragment_maps layout
        val view = inflater.inflate(R.layout.fragment_flight_view_maps, container, false)
        // Map View Widget
        mapWidget(view,savedInstanceState)
        detailsView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(FlightListViewModel::class.java)
        viewTrackingModel = ViewModelProvider(requireActivity()).get(FlightMapsViewModel::class.java)
        viewModel.getClickedFlightLiveData().observe(requireActivity()) {

            Log.d("Test", it.toString());
            flightInfo = it!!
            if (it?.icao24 != null){
                viewTrackingModel.getFlights(it.icao24)
            }

        }
        viewTrackingModel.flightTracking().observe(requireActivity()) {
            // getting the flight track data
            flightTracking  = it
            /// getting all the lat log from the data

            Log.d("flightTracking",flightTracking.toString())

            postionFlight = mutableListOf()
            for (pos in flightTracking.path) {
                if (pos.size >= 3) { // Ensure the list has at least latitude and longitude
                    try {
                        val latitude = pos[1].toString().toDoubleOrNull() // Parse latitude to Double or null if parsing fails
                        val longitude = pos[2].toString().toDoubleOrNull() // Parse longitude to Double or null if parsing fails
                        if (latitude != null && longitude != null) {
                            val latLng = LatLng(latitude, longitude) // Create LatLng object
                            (postionFlight as MutableList<LatLng>).add(latLng) // Add LatLng to the list

                        }
                    } catch (e: NumberFormatException) {
                        // error during the convertion to double so we don't add it to the list
                    }
                }
            }
            // Maps Config
//
            /// sending to the maps

            createLine(mapboxMap, postionFlight,"line-layer", "line-source")

            var from = postionFlight.first()
            var to = postionFlight.last()
            var departure = flightInfo.estDepartureAirport
            var arrival = flightInfo.estArrivalAirport
            var flightName = flightInfo.callsign
            removeAllMarkers()

            // Add two airports as markers
            createMarker( "Departure: $departure", "Flight: $flightName",from,null)
            createMarker( "Arrival: $arrival", "Flight: $flightName",to,null) //R.drawable.airplane
        }
    }

    /// creating the map
    private fun mapWidget( view: View ,savedInstanceState: Bundle?) {
        // mapping the maps to the maps layout inside the fragment_maps
        mapView = view.findViewById(R.id.mapView)
        // Config
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMa ->
            // mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            mapboxMap = mapboxMa
            mapboxMap.setStyle(
                Style.Builder()
                    .fromUri(getString(R.string.mapbox_style_url))
            ) {
                // Linking Zoom Button to the maps
                zoomButtons(mapboxMap,view)
            }
        }


    }

    /// creating the lines on the map using the routes
    private fun createLine(mapboxMap: MapboxMap, routes: List<LatLng> , layerId: String, sourceId: String) {
        // Create a LineString to draw the airplane route
        lateinit var pointList : List<Point>
        pointList = mutableListOf()

        for (post in routes){
            var point =Point.fromLngLat(post.longitude, post.latitude)
            pointList.add(point)
        }
        val lineString = LineString.fromLngLats(
                pointList
        )

        // Create a LineLayer with the LineString
        val lineLayer = LineLayer(layerId, sourceId)
        mapboxMap.style?.removeSource(sourceId)
        mapboxMap.style?.removeLayer(layerId)
        lineLayer.setProperties(
            PropertyFactory.lineDasharray(arrayOf(0.01f, 2f)),
            PropertyFactory.lineCap(com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND),
            PropertyFactory.lineWidth(4f),
            PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
        )
    if (mapboxMap.style?.getSource(sourceId) == null && mapboxMap.style?.getLayer(layerId) == null){
        // Add the LineLayer to the map
        mapboxMap.style?.addSource(GeoJsonSource(sourceId, lineString))
        mapboxMap.style?.addLayer(lineLayer)
        /// center the camera
        mapsConfigCenterZoom(routes,null)
    }

    }
//    private fun createMarker(mapboxMap: MapboxMap, title: String, subTitle: String, position : LatLng, iconData: Int?) {
//        // Create a custom icon from the drawable resource
//        if (iconData == null){
//            var marker = MarkerOptions()
//                .position(position)
//                .title(title)
//                .snippet(subTitle)
//            markersList.add(marker)
//            mapboxMap.addMarker(marker);
//        }else{
//            val customIcon =
//                IconFactory.getInstance(requireContext()).fromResource(iconData)
//                var marker = MarkerOptions()
//                    .position(position)
//                    .title(title)
//                    .icon(customIcon)
//                    .snippet(subTitle)
//            markersList.add(marker)
//            mapboxMap.addMarker( marker);
//        }
//
//
//    }

    private fun createMarker(
        title: String,
        subTitle: String,
        position: LatLng,
        iconData: Int?
    ) {

        val markerOptions = if (iconData == null) {
            MarkerOptions()
                .position(position)
                .title(title)
                .snippet(subTitle)
        } else {
            val customIcon = IconFactory.getInstance(requireContext()).fromResource(iconData)
            MarkerOptions()
                .position(position)
                .title(title)
                .icon(customIcon)
                .snippet(subTitle)
        }

        val marker = mapboxMap.addMarker(markerOptions)
        markersList.add(marker)
    }

    private fun removeAllMarkers() {
        for (marker in markersList) {
            marker.remove()
        }
        markersList.clear()
    }

    private fun detailsView(view: View){
        var detailsButton = view.findViewById<Button>(R.id.details)

        detailsButton.setOnClickListener {

//            val bottomSheetFragment = FlightDetailsBottomSheetFragment()
            // Assuming flight details are available in your viewModel or other source

            var departure = flightInfo.estDepartureAirport
            var arrival = flightInfo.estArrivalAirport
            var flightName = flightInfo.callsign
            showBottomSheet(flightName,departure,arrival)

            // Call the method in FlightDetailsBottomSheetFragment to update TextViews=
//            view.findViewById<TextView>(R.id.textDeparture)?.text = flightNumber
//            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
//            bottomSheetFragment.updateFlightDetails(flightNumber, departure, destination)

        }
    }
    private fun zoomButtons(mapboxMap: MapboxMap, view: View){
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

    }

    private fun mapsConfigCenterZoom(targets: List<LatLng>, newZoom: Double? ) {
        // Ensure the list is not empty before processing
        if (targets.isNotEmpty()) {
            // Calculate the center of the LatLng objects in the list
            val centerLatLng = calculateCenter(targets)

            // Set the initial map center and zoom level
           var cameraZoom = if (newZoom == null) zoom else newZoom
            val cameraPosition = CameraPosition.Builder()
                .target(centerLatLng)
                .zoom(cameraZoom) // Replace 'zoom' with the desired zoom level
                .build()

            mapboxMap.cameraPosition = cameraPosition
        }
    }

    private fun calculateCenter(targets: List<LatLng>): LatLng {
        var totalLat = 0.0
        var totalLng = 0.0

        // Calculate the sum of all latitudes and longitudes
        for (target in targets) {
            totalLat += target.latitude
            totalLng += target.longitude
        }

        // Calculate the average latitude and longitude to find the center
        val avgLat = totalLat / targets.size
        val avgLng = totalLng / targets.size

        return LatLng(avgLat, avgLng)
    }



    private fun showBottomSheet(flightNumber: String,departure: String, destination: String) {
        val fragment = FlightDetailsBottomSheetFragment.newInstance(flightNumber, departure, destination)
        fragment.setUpdateDataListener(this)
        bottomSheetFragment = fragment
        bottomSheetFragment?.show(parentFragmentManager, fragment.tag)
    }

    override fun onUpdateButtonClicked(flightNumber: String,departure: String,destination: String , isLiveClicked: Boolean) {
        if(isLiveClicked){
            bottomSheetFragment?.updateValues(flightNumber, "Paris","0", true)

            getLiveData()

        }else{
            bottomSheetFragment?.updateValues(flightNumber, departure,destination, false)
            viewTrackingModel.getFlights(flightInfo.icao24)
        }
    }
    private fun getLiveData(){
        viewTrackingModel.getFlightsLivePosition(flightInfo.icao24)
        viewTrackingModel.flightLiveData().observe(requireActivity()) {
            Log.d("Live Data", it.toString())
            var resp: FlightData = it
            var pos = resp.path.last()
            val latitude = pos[1].toString().toDoubleOrNull() // Parse latitude to Double or null if parsing fails
            val longitude = pos[2].toString().toDoubleOrNull() // Parse longitude to Double or null if parsing fails
            val speed = pos[3].toString()
            val flightName = resp.callsign.toString()
           showLivePlan(LatLng(latitude!!, longitude!!), speed, flightName)
        }

    }

    private fun  showLivePlan(position: LatLng, speed: String, flightName: String){
        removeLine("line-layer", "line-source")
        removeAllMarkers()
        createMarker( "Flight: $flightName", "Speed: $speed",position,R.drawable.airplane)
        mapsConfigCenterZoom(listOf(position),   Random.nextDouble(3.0, 6.0))
    }


    private fun removeLine( layerId: String, sourceId: String) {
        val style = mapboxMap.style
        if (style != null) {
            val layersToRemove = mutableListOf<String>()

            // Find and collect all layer IDs associated with the given source ID
            for (layer in style.layers) {
                if (layer.id == layerId) {
                    layersToRemove.add(layer.id)
                }
            }

            // Remove collected layers associated with the source
            for (layerIdToRemove in layersToRemove) {
                style.removeLayer(layerIdToRemove)
            }

            // Remove the source
            style.removeSource(sourceId)
        }
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