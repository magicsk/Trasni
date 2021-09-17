package eu.magicsk.transi.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import eu.magicsk.transi.R
import eu.magicsk.transi.data.remote.responses.Step
import eu.magicsk.transi.util.dpToPx
import eu.magicsk.transi.util.getLineColor
import eu.magicsk.transi.util.getLineTextColor
import eu.magicsk.transi.util.isDarkTheme
import kotlinx.android.synthetic.main.trip_planner_list_step_transit.view.*
import kotlinx.android.synthetic.main.trip_planner_list_step_walking.view.*

class TripPlannerStepsAdapter(
    private val TripPlannerStepList: MutableList<Step>
) : RecyclerView.Adapter<TripPlannerStepsAdapter.TripPlannerViewHolder>() {
    class TripPlannerViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    override fun getItemViewType(position: Int): Int {
        return if (TripPlannerStepList[position].type == "TRANSIT") 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripPlannerViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = if (viewType == 1) {
            inflater.inflate(R.layout.trip_planner_list_step_transit, parent, false)
        } else {
            inflater.inflate(R.layout.trip_planner_list_step_walking, parent, false)
        }
        return TripPlannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripPlannerViewHolder, position: Int) {
        val current = TripPlannerStepList[position]
        holder.itemView.apply {
            when (current.type) {
                "TRANSIT" -> {
                    val rounded =
                        try {
                            current.line.number.contains("S") || current.line.number.toInt() < 10
                        } catch (e: NumberFormatException) {
                            false
                        }
                    if (rounded) {
                        TripPlannerListStepLineNum.setBackgroundResource(R.drawable.round_shape)
                        if (!current.line.number.contains("S")) TripPlannerListStepLineNum.setPadding(
                            12f.dpToPx(context),
                            5f.dpToPx(context),
                            12f.dpToPx(context),
                            5f.dpToPx(context)
                        ) else {
                            TripPlannerListStepLineNum.setPadding(5f.dpToPx(context))
                        }
                    } else {
                        TripPlannerListStepLineNum.setBackgroundResource(R.drawable.rounded_shape)
                    }
                    val drawable = TripPlannerListStepLineNum.background
                    drawable.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            getLineColor(current.line.number, isDarkTheme(resources))
                        ), PorterDuff.Mode.SRC
                    )
                    TripPlannerListStepLineNum.setTextColor(
                        ContextCompat.getColor(
                            context,
                            getLineTextColor(current.line.number)
                        )
                    )

                    TripPlannerListStepLineNum.background = drawable
                    TripPlannerListStepLineArrow.background = drawable
                    TripPlannerListStepLineNum.text = current.line.number
                    TripPlannerListStepHeadsign.text = context.getString(R.string.tripHeadsign).format(current.headsign)
                    TripPlannerListStepDepartureStop.text = current.departure_stop
                    TripPlannerListStepDepartureTime.text = current.departure_time
                    TripPlannerListStepDuration.text =
                        context.getString(R.string.tripStepDuration).format(current.num_stops, current.duration)
                    TripPlannerListStepArrivalStop.text = current.arrival_stop
                    TripPlannerListStepArrivalTime.text = current.arrival_time

                    setOnClickListener {
                        val duration = TripPlannerListStepDuration
                        val stopList = TripPlannerListStepStopList
                        var stopsListText = ""

                        current.stops.forEach { stop ->
                            stopsListText += if (stopsListText == "") "${stop.time}  ${stop.stop}" else {
                                "\n${stop.time}  ${stop.stop}"
                            }
                        }

                        TripPlannerListStepStopList.text = stopsListText

                        if (duration.visibility == View.VISIBLE) {
                            duration.visibility = View.GONE
                            stopList.visibility = View.VISIBLE
                        } else {
                            duration.visibility = View.VISIBLE
                            stopList.visibility = View.GONE
                        }
                    }
                }
                "WALKING" -> {
                    TripPlannerListStepWalkingText.text = current.text
                }
                else -> {
                    TripPlannerListStepWalkingText.text = context.getString(R.string.error)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return TripPlannerStepList.size
    }
}