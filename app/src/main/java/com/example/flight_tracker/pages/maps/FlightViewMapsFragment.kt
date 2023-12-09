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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.example.flight_tracker.R

import com.example.flight_tracker.models.openSkyApiModels.FlightData
import com.example.flight_tracker.models.openSkyApiModels.FlightModel
import com.example.flight_tracker.network.RequestListener

import com.example.flight_tracker.pages.dialog.DialogFragmentCustom

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



class FlightViewMapsFragment : Fragment(), DialogFragmentCustom.CustomDialogListener, FlightDetailsBottomSheetFragment.UpdateDataListener  {
    private var mapView: MapView? = null
    private var zoom:Double = 2.0
    private var isBottomSheetLiveState:Boolean =false
    private lateinit var viewModel: FlightListViewModel
    private lateinit var viewTrackingModel: FlightMapsViewModel
    private lateinit var flightTracking : FlightData
    private lateinit var postionFlight : List<LatLng>
    private lateinit var mapboxMap: MapboxMap
    private lateinit var  flightInfo: FlightModel
    private var bottomSheetFragment: FlightDetailsBottomSheetFragment? = null
    // Maintain a list to keep track of added markers
    private val markersList = mutableListOf<Marker>()
    private lateinit var noClickedLinearLayoutView: ViewGroup;


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
        mapView?.visibility = View.GONE
        noClickedLinearLayoutView = view.findViewById(R.id.linearMapsFragmentNoDataId)
        noClickedLinearLayoutView.visibility = View.VISIBLE

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(FlightListViewModel::class.java)
        viewTrackingModel = ViewModelProvider(requireActivity()).get(FlightMapsViewModel::class.java)

        //change ui depending on live track request
        viewTrackingModel.flightsUiState().observe(requireActivity()) {
            when(it) {
                is RequestListener.Loading -> {}

                is RequestListener.Success -> {
                }
                is RequestListener.Error -> {
                    if (isAdded && parentFragmentManager != null) {
                        DialogFragmentCustom("Failed during the request, check your connection or the API is not available now.", "Try again", this).show(
                            parentFragmentManager, "DialogFragmentError"
                        )
                    }

                }
                is RequestListener.Failed -> {
                    if (isAdded && parentFragmentManager != null) {
                        DialogFragmentCustom(it.message, "Cancel", this).show(
                            parentFragmentManager, "DialogFragmentFailed"
                        )
                    }
                }
            }
        }

        viewModel.getClickedFlightLiveData().observe(requireActivity()) {it ->

            if (it != null){
                mapView?.visibility = View.VISIBLE
                noClickedLinearLayoutView.visibility = View.GONE
                flightInfo = it
                if (it?.icao24 != null){
                    viewTrackingModel.getFlights(it.icao24)
                }
            }
        }
        viewTrackingModel.flightTracking().observe(requireActivity()) { it ->
            // getting the flight track data
            flightTracking  = it
            /// getting all the lat log from the data
//            Log.d("flightTracking",flightTracking.toString())

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

            // Ensure that mapboxMap is initialized before using it
            if (::mapboxMap.isInitialized) {
                createInfoOnMap()
            }
        }
    }

    private fun createInfoOnMap(){
        Log.d("flightTracking","isInitialized")

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

    /// creating the map
    private fun mapWidget( view: View ,savedInstanceState: Bundle?) {
        // mapping the maps to the maps layout inside the fragment_maps
        mapView = view.findViewById(R.id.mapView)

        // Config
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMa ->
            // mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            this.mapboxMap = mapboxMa
            mapboxMap.setStyle(
                Style.Builder()
                    .fromUri(getString(R.string.mapbox_style_url))
            ) {
                // Linking Zoom Button to the maps
                zoomButtons(mapboxMap,view)
                /// on create map view check if the the data are not null and if they are not then add it on the maps
                if ( (::postionFlight.isInitialized &&  postionFlight != null) && (::flightInfo.isInitialized && flightInfo != null)){
                    createInfoOnMap()
                }
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
//        mapboxMap.style!!.removeSource(sourceId)
//        mapboxMap.style!!.removeLayer(layerId)
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

    // La fonction qui creer du marker sur la maps
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
            marker?.remove()
        }
        markersList.clear()
    }

    /// detail bottun
    private fun detailsView(view: View){
        var detailsButton = view.findViewById<Button>(R.id.details)

        detailsButton.setOnClickListener {
            // Assuming flight details are available in your viewModel or other source

            var departure = flightInfo.estDepartureAirport
            var arrival = flightInfo.estArrivalAirport
            var flightName = flightInfo.callsign
            // show the bottom sheet
            showBottomSheet(flightName,departure,arrival)

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

    /// The function who center the maps on the list of coordinates you gave to it
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

    /// get the medium of a list of coordinates
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



    /// the function to show the botton sheet
    private fun showBottomSheet(flightNumber: String,departure: String, destination: String) {
        val fragment = FlightDetailsBottomSheetFragment.newInstance(flightNumber, departure, destination, isBottomSheetLiveState)
        fragment.setUpdateDataListener(this)
        bottomSheetFragment = fragment
        bottomSheetFragment?.show(parentFragmentManager, fragment.tag)
    }

    /// the call back when user tap on the "[Info]" or "[Live]" on the button sheet
    override fun onUpdateButtonClicked(flightNumber: String,departure: String,destination: String , isLiveClicked: Boolean) {
        if(isLiveClicked){
            getLiveData()
            isBottomSheetLiveState = true

        }else{
            bottomSheetFragment?.updateValues(flightNumber, departure,destination, "",false)
            viewTrackingModel.getFlights(flightInfo.icao24)
            isBottomSheetLiveState = false

        }
    }
    private fun getLiveData(){
        viewTrackingModel.getFlightsLivePosition(flightInfo.icao24)
        viewTrackingModel.flightLiveData().observe(requireActivity()) { it ->
//            Log.d("Live Data", it.toString())

            var resp: FlightData = it

            /// get the position for the marker
            var pos = resp.path.last()
            val latitude = pos[1].toString().toDoubleOrNull() // Parse latitude to Double or null if parsing fails
            val longitude = pos[2].toString().toDoubleOrNull() // Parse longitude to Double or null if parsing fails
            val speed = "0"
            val altitude = pos[3].toString().toDoubleOrNull().toString()
            val flightName = resp.callsign.toString()
            val vertical =  pos[4].toString().toDoubleOrNull().toString()
            /// update the button sheet view but as the api is not working and I don't have the model of the return so I just took the flight last destination for only the destination name
            bottomSheetFragment?.updateValues(resp.callsign.toString(), altitude,speed, vertical,true)

            // show the plan on the map
           showLivePlan(LatLng(latitude!!, longitude!!), speed, flightName)
        }

    }

    /// function to add the plan on the map
    private fun  showLivePlan(position: LatLng, speed: String, flightName: String){
        removeLine("line-layer", "line-source")
        removeAllMarkers()
        createMarker( "Flight: $flightName", "Speed: $speed",position,R.drawable.airplan27)//R.drawable.airplane
        mapsConfigCenterZoom(listOf(position),   6.0)
    }

    // remove all the line on the map
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
                style?.removeLayer(layerIdToRemove)
            }

            // Remove the source
            style?.removeSource(sourceId)
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



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onNegativeButtonClickDialogFragment() {
        //TODO change here or delete everytinhg
        val activity = activity

        if (activity != null && activity is AppCompatActivity) {
            val isLargeScreen = activity.findViewById<FragmentContainerView>(R.id.fragment_map_container) != null
            if(isLargeScreen){
                Log.d("Action", "En mode écran large")
            }else{
                Log.d("Action", "En mode petit écran")
            }
        }

    }
}