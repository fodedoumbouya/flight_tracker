import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.flight_tracker.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FlightDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    // Declare TextViews as global variables
    private var flightNumberTextView: TextView? = null
    private var departureTextView: TextView? = null
    private var destinationTextView: TextView? = null
    private  var flightNumberText: String? = null
    private  var flightDepartureText: String? = null
    private  var flightDestinationText: String? = null


    interface UpdateDataListener {
        fun onUpdateButtonClicked(flightNumber: String,departure: String, destination: String,isLiveClicke: Boolean)
    }

    private var listener: UpdateDataListener? = null

    fun setUpdateDataListener(listener: UpdateDataListener) {
        this.listener = listener
    }
    fun updateValues(flightNumber: String, position: String, speed: String,verticalRate: String ,buttonAction: Boolean) {
        val buttonText = if (!buttonAction) "Live" else "Info"
        if( buttonAction){
            flightNumberTextView?.text = "Flight number: $flightNumber"
            destinationTextView?.text = "Speed: $speed kph"
            departureTextView?.text = "Altitude: $position ft\nVertical Rate: $verticalRate m/s"
        }else{
            flightNumberTextView?.text = "Flight number: $flightNumber"
            departureTextView?.text = "Departure: $position"
            destinationTextView?.text = "Destination: $speed"
        }


        // Update button text
        val updateButton = dialog?.findViewById<Button>(R.id.liveButton)
        updateButton?.text = buttonText
    }

    companion object {
        private const val ARG_FLIGHT_NUMBER = "flightNumber"
        private const val ARG_DEPARTURE = "departure"
        private const val ARG_DESTINATION = "destination"
        private const val ARG_IS_LIVE = "isLive"




        fun newInstance(flightNumber: String, departure: String, destination: String, isBottomSheetLiveState: Boolean): FlightDetailsBottomSheetFragment {
            val fragment = FlightDetailsBottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_FLIGHT_NUMBER, flightNumber)
            args.putString(ARG_DEPARTURE, departure)
            args.putString(ARG_DESTINATION, destination)
            args.putBoolean(ARG_IS_LIVE, isBottomSheetLiveState)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_flight_details_bottom_sheet, container, false)

        // Initialize TextViews
        flightNumberTextView = rootView.findViewById(R.id.textFlightNumber)
        departureTextView = rootView.findViewById(R.id.textDeparture)
        destinationTextView = rootView.findViewById(R.id.textDestination)

        // Retrieve arguments
        arguments?.let {
            val flightNumber = it.getString(ARG_FLIGHT_NUMBER)
            val departure = it.getString(ARG_DEPARTURE)
            val destination = it.getString(ARG_DESTINATION)
            val isLive: Boolean = it.getBoolean(ARG_IS_LIVE)


            flightNumberText = flightNumber
            flightDepartureText = departure
            flightDestinationText = destination

            // Update TextViews
            flightNumberTextView?.text = "Flight number: $flightNumber"
            departureTextView?.text = "Departure: $departure"
            destinationTextView?.text = "Destination: $destination"

            if (isLive){
                listener?.onUpdateButtonClicked(flightNumberText!!, flightDepartureText!!, flightDestinationText!!, false)
            }
        }

        val updateButton = rootView.findViewById<Button>(R.id.liveButton)
        updateButton.setOnClickListener {
            // Pass the data back to the calling class through the listener
            listener?.onUpdateButtonClicked(flightNumberText!!, flightDepartureText!!, flightDestinationText!!, updateButton.text.toString() == "Live")
        }
        return rootView
    }
}
