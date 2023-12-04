package com.example.flight_tracker.pages.maps;

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Property
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.example.flight_tracker.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
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
    private var zoom:Double = 4.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Mapbox with your access token
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))

        // linking the class to my fragment_maps layout
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        // Map View Widget
        mapWidget(view,savedInstanceState)

        return view
    }


    private fun mapWidget( view: View ,savedInstanceState: Bundle?) {
        // mapping the maps to the maps layout inside the fragment_maps
        mapView = view.findViewById(R.id.mapView)
        // Config
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
        // mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            mapboxMap.setStyle(
                Style.Builder()
                    .fromUri(getString(R.string.mapbox_style_url))
            ) {
                // Linking Zoom Button to the maps
                zoomButtons(mapboxMap,view)

                // initializing our variable for button with its id.
               val btnShowBottomSheet = view.findViewById<Button>(R.id.details);
                // Set up a click listener for the details in button
                btnShowBottomSheet.setOnClickListener {
                    Log.d("","Tap on detail==================")
                    // Create and show the BottomSheetDialog
                    val bottomSheetDialog = BottomSheetDialog(requireContext())
                    val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null) // Replace 'your_bottom_sheet_layout' with the actual layout file for your bottom sheet
                    bottomSheetDialog.setContentView(bottomSheetView)
                    bottomSheetDialog.show()

                }


                // Maps Config
                mapsConfigCenterZoom(mapboxMap)

                // fake data
                val from = LatLng( 49.0097,2.5479)
                val to = LatLng( 45.7215,5.0824)

                // Add two airports as markers
                createMarker( mapboxMap,"Paris Airport", "Charles de Gaulle Airport",from,null)
                createMarker( mapboxMap,"Lyon Airport", "Lyon-Saint Exupéry Airport",to,null) //R.drawable.airplane


                // Create line between two Points
                createLine(mapboxMap, from,to,"line-layer", "line-source")



            }
        }


    }

    private fun createLine(mapboxMap: MapboxMap, from: LatLng, to: LatLng, layerId: String, sourceId: String) {
        // Create a LineString to draw the airplane route
        val lineString = LineString.fromLngLats(
            listOf(
                Point.fromLngLat(from.longitude, from.latitude),
                Point.fromLngLat(to.longitude, to.latitude),
            )
        )

        // Create a LineLayer with the LineString
        val lineLayer = LineLayer(layerId, sourceId)
        lineLayer.setProperties(
            PropertyFactory.lineDasharray(arrayOf(0.01f, 2f)),
            PropertyFactory.lineCap(com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND),
            PropertyFactory.lineWidth(4f),
            PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
        )

        // Add the LineLayer to the map
        mapboxMap.style?.addSource(GeoJsonSource(sourceId, lineString))
        mapboxMap.style?.addLayer(lineLayer)
    }

    private fun createMarker(mapboxMap: MapboxMap, title: String, subTitle: String, position :LatLng, iconData: Int?) {

        // Create a custom icon from the drawable resource

        if (iconData == null){
            mapboxMap.addMarker( MarkerOptions()
                .position(position)
                .title(title)
                .snippet(subTitle));
        }else{
            val customIcon =
                IconFactory.getInstance(requireContext()).fromResource(iconData)

            mapboxMap.addMarker( MarkerOptions()
                .position(position)
                .title(title)
                .icon(customIcon)
                .snippet(subTitle));
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

   private fun mapsConfigCenterZoom(mapboxMap: MapboxMap,){
       val target:LatLng =  LatLng(48.864716, 2.349014)
       // Set the initial map center and zoom level
       val cameraPosition = CameraPosition.Builder()
           .target(target) // London coordinates
           .zoom(zoom)
           .build()

       mapboxMap.cameraPosition = cameraPosition
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
