package eu.magicsk.transi.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import eu.magicsk.transi.R
import eu.magicsk.transi.databinding.TripPlannerListStepTransitBinding
import eu.magicsk.transi.databinding.TripPlannerListStepWalkingBinding
import eu.magicsk.transi.util.*

class TripPlannerStepsAdapter(
    private val TripPlannerStepList: MutableList<TripPart>,
    private val onItemLongClick: () -> Unit
) : RecyclerView.Adapter<TripPlannerStepsAdapter.TripPlannerStepsViewHolder>() {
    class TripPlannerStepsViewHolder(val binding: ViewBinding, private val onItemLongClick: () -> Unit) :
        RecyclerView.ViewHolder(binding.root), View.OnLongClickListener {
        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            onItemLongClick()
            return true
        }
    }

    private var _binding: ViewBinding? = null
    private val binding get() = _binding!!

    override fun getItemViewType(position: Int): Int {
        return TripPlannerStepList[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripPlannerStepsViewHolder {
        _binding = if (viewType == 1) {
            TripPlannerListStepTransitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        } else {
            TripPlannerListStepWalkingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }

        return TripPlannerStepsViewHolder(binding, onItemLongClick)
    }

    @Suppress("USELESS_CAST")
    override fun onBindViewHolder(holder: TripPlannerStepsViewHolder, position: Int) {
        val current = TripPlannerStepList[position]
        holder.binding.apply {
            val context = root.context
            val resources = root.resources
            when (this) {
                is TripPlannerListStepWalkingBinding -> {
                    val bindingB = this as TripPlannerListStepWalkingBinding
                    println(current.arrival?.stop?.name)
                    println(current.departure?.stop?.name)
                    if (current.arrival?.stop?.name == current.departure?.stop?.name && itemCount > position + 1) {
                        val platform = TripPlannerStepList[position + 1].stops.first().platform
                        if (platform != null) {
                            bindingB.TripPlannerListStepWalkingText.text = "${current.duration} min to platform $platform"
                        } else {
                            bindingB.TripPlannerListStepWalkingText.text =
                                "${current.duration} min transfer between platforms"
                        }

                    } else if (current.arrival?.stop?.name == "") {
                        bindingB.TripPlannerListStepWalkingText.text = "${current.duration} min to the destination"
                    } else {
                        bindingB.TripPlannerListStepWalkingText.text = "${current.duration} min to ${current.arrival?.stop?.name}"
                    }
                    bindingB.TripPlannerListStepWalkingText.isSelected = true
                }

                else -> {
                    val bindingA = this as TripPlannerListStepTransitBinding
                    val line = current.line!!
                    val rounded =
                        try {
                            line.contains("S") || line.toInt() < 10
                        } catch (e: NumberFormatException) {
                            false
                        }
                    if (rounded) {
                        bindingA.TripPlannerListStepLineNum.setBackgroundResource(R.drawable.round_shape)
                        if (!line.contains("S")) bindingA.TripPlannerListStepLineNum.setPadding(
                            12f.dpToPx(context),
                            5f.dpToPx(context),
                            12f.dpToPx(context),
                            5f.dpToPx(context)
                        ) else {
                            bindingA.TripPlannerListStepLineNum.setPadding(5f.dpToPx(context))
                        }
                    } else {
                        bindingA.TripPlannerListStepLineNum.setBackgroundResource(R.drawable.rounded_shape)
                    }
                    val drawable = bindingA.TripPlannerListStepLineNum.background
                    drawable.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            getLineColor(line, isDarkTheme(resources))
                        ), PorterDuff.Mode.SRC
                    )
                    bindingA.TripPlannerListStepLineNum.setTextColor(
                        ContextCompat.getColor(
                            context,
                            getLineTextColor(line)
                        )
                    )

                    val arrowDrawable = bindingA.TripPlannerListStepLineArrow.background
                    arrowDrawable.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            getLineColor(line, isDarkTheme(resources))
                        ), PorterDuff.Mode.SRC
                    )

                    val departureStop = current.departure!!.stop
                    val arrivalStop = current.arrival!!.stop

                    bindingA.TripPlannerListStepLineNum.background = drawable
                    bindingA.TripPlannerListStepLineNum.text = line
                    bindingA.TripPlannerListStepLineArrow.background = arrowDrawable
                    bindingA.TripPlannerListStepHeadsign.text =
                        context.getString(R.string.tripHeadsign).format(current.headsign)
                    bindingA.TripPlannerListStepHeadsign.isSelected = true
                    bindingA.TripPlannerListStepDepartureTime.text = departureStop.time
                    bindingA.TripPlannerListStepDepartureStop.text = departureStop.name
                    bindingA.TripPlannerListStepDepartureStop.isSelected = true
                    bindingA.TripPlannerListStepDepartureRequest.isVisible = departureStop.request
                    bindingA.TripPlannerListStepDepartureZone.text = departureStop.zone
                    bindingA.TripPlannerListStepDuration.text =
                        context.getString(R.string.tripStepDuration, current.stops.size, current.duration) //TODO: hours
                    bindingA.TripPlannerListStepArrivalTime.text = arrivalStop.time
                    bindingA.TripPlannerListStepArrivalStop.text = arrivalStop.name
                    bindingA.TripPlannerListStepArrivalStop.isSelected = true
                    bindingA.TripPlannerListStepArrivalRequest.isVisible = arrivalStop.request
                    bindingA.TripPlannerListStepArrivalZone.text = arrivalStop.zone
                    val stopListContainer = bindingA.TripPlannerListStepStopListContainer
                    val durationContainer = bindingA.TripPlannerListStepDurationContainer
                    val stopList = bindingA.TripPlannerListStepStopList
                    stopList.layoutManager =
                        LinearLayoutManager(stopList.context, RecyclerView.VERTICAL, false)

                    when (current.stops.size) {
                        0 -> {
                            durationContainer.collapse(false)
                            stopListContainer.collapse(false)
                        }

                        1 -> {
                            durationContainer.collapse(false)
                            stopListContainer.expand(false)
                        }

                        else -> {
                            durationContainer.expand(false)
                            stopListContainer.collapse(false)
                        }
                    }

                    fun onListItemClick() {
                        if (current.stops.size > 1) {
                            if (durationContainer.isExpanded) {
                                durationContainer.collapse()
                                stopListContainer.expand()
                            } else {
                                durationContainer.expand()
                                stopListContainer.collapse()
                            }
                        }
                    }

                    stopList.adapter = TripPlannerStopsAdapter(current.stops, onItemLongClick) { onListItemClick() }
                    root.setOnClickListener {
                        onListItemClick()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return TripPlannerStepList.size
    }
}
