package com.app.eAcademicAssistant.adapters

import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.dayManagement.calendar.list.CalendarListActivity
import com.teamayka.smsadminsuper.utils.CalendarUtils
import java.text.SimpleDateFormat
import java.util.*

class CalendarListMonthRvAdapter(
    private val year: Int,
    private val month: Int,
    private val activity: CalendarListActivity,
    private val adapter: CalendarListRvAdapter
) :
    RecyclerView.Adapter<CalendarListMonthRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_rv_calendar_day, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return adapter.days.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(holder.adapterPosition == -1)
            return

        if (adapter.days[holder.adapterPosition].isSelected) {
            holder.vParent.setBackgroundColor(Color.parseColor("#09000000"))
        } else {
            holder.vParent.setBackgroundColor(Color.parseColor("#03000000"))
        }

        holder.cb.isChecked = adapter.days[holder.adapterPosition].isWorkingDay == 1

        val day = DateFormat.format("d", adapter.days[holder.adapterPosition].date)
        holder.tvDay.text = day

        val df = SimpleDateFormat(CalendarUtils.DATE_FORMAT, Locale.US)
        val date = df.format(adapter.days[holder.adapterPosition].date)
        val currentDate = df.format(Calendar.getInstance().time)

        val isActiveDate = (DateFormat.format(
            CalendarUtils.MONTH_FORMAT,
            adapter.days[holder.adapterPosition].date
        ).toString().toInt()) == month + 1 && (DateFormat.format(
            CalendarUtils.YEAR_FORMAT,
            adapter.days[holder.adapterPosition].date
        ).toString().toInt()) == year

        when {
            date == currentDate -> {
                holder.tvDay.setBackgroundResource(R.drawable.shape_bg_circle_solid_alpha_light_dark)
                holder.tvDay.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.white
                    )
                ) // current date text color
            }
            isActiveDate -> {
                holder.tvDay.setBackgroundResource(0)
                holder.tvDay.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorGray
                    )
                ) // active date text color
            }
            else -> {
                holder.tvDay.setBackgroundResource(0)
                holder.tvDay.setTextColor(Color.parseColor("#66666666")) // inactive date text color
            }
        }

        holder.cb.isEnabled = false

//        if we want to click all active item use this listener . this works for events and non events
//                holder.vParent.setOnClickListener {
//                    if ((DateFormat.format(
//                            CalendarUtils.MONTH_FORMAT,
//                            days[position]
//                        ).toString().toInt()) == month + 1
//                    ) { // if active day perform click
//                        showDialog(holder.itemView.context)
//                    }
//                }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val vParent: View = itemView.findViewById(R.id.vParent)
        val cb: CheckBox = itemView.findViewById(R.id.cb)
    }
}