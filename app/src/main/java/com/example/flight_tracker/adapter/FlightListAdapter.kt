package com.example.flight_tracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flight_tracker.commom.Utils.Companion.convertirTimestampEnDate
import com.example.flight_tracker.commom.Utils.Companion.timestampToString
import com.example.flight_tracker.models.openSkyApiModels.FlightModel // Remplacement de Flight par FlightModel

class FlightListAdapter(private val flightList: List<FlightModel>, val cellClickListener: OnCellClickListener) : RecyclerView.Adapter<FlightListAdapter.FlightViewHolder>() {

    interface OnCellClickListener {
        fun onCellClicked(flightModel: FlightModel)
    }
    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textFlightName: TextView = itemView.findViewById(R.id.textAirlineName)
        val textAirline: TextView = itemView.findViewById(R.id.textFlightStatus)
        val textDate: TextView = itemView.findViewById(R.id.textFlightDate)
        val textDeparture: TextView = itemView.findViewById(R.id.textDepartureCity)
        val textDepartureAirportInitials: TextView = itemView.findViewById(R.id.textDepartureAirportInitials)
        val textDepartureTime: TextView = itemView.findViewById(R.id.textDepartureTime)
        val textArrival: TextView = itemView.findViewById(R.id.textArrivalCity)
        val textArrivalAirportInitials: TextView = itemView.findViewById(R.id.textArrivalAirportInitials)
        val textArrivalTime: TextView = itemView.findViewById(R.id.textArrivalTime)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedFlight = flightList[position]
                    cellClickListener.onCellClicked(clickedFlight)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.flight_item, parent, false)
        return FlightViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val currentFlight = flightList[position]
        val (dateStr, timeStr) = convertirTimestampEnDate(currentFlight.lastSeen)
        val arrivalTimeTimeStamp = currentFlight.lastSeen + (currentFlight.estArrivalAirportHorizDistance - currentFlight.estDepartureAirportHorizDistance) * 100
        val (_, arrivalTime) = convertirTimestampEnDate(arrivalTimeTimeStamp)
        holder.textFlightName.text = currentFlight.callsign + " Airline"
        holder.textAirline.text = "En route"
        holder.textDate.text = dateStr
        holder.textDeparture.text = "textDeparture"
        holder.textDepartureAirportInitials.text = currentFlight.estDepartureAirport
        holder.textDepartureTime.text = timeStr
        holder.textArrival.text = "20:45"
        holder.textArrivalAirportInitials.text = currentFlight.estArrivalAirport
        holder.textArrivalTime.text = arrivalTime
    }

    override fun getItemCount(): Int {
        return flightList.size
    }
}

