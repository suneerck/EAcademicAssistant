package com.app.eAcademicAssistant.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.model.YearModel

/**
 * Created by basil on 12/28/2017.
 */
class CalendarCreateDateAdapter(
    private val years: ArrayList<YearModel>,
    private val snapHelper: SnapHelper,
    private val rvDate: RecyclerView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_EMPTY = 1 // this item was used to keep space on top and bottom of list
        private const val VIEW_TYPE_ITEM = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_empty, null)
            EmptyViewHolder(view, rvDate.height / 2)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_time_date_picker, null)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.tv.text = years[position - 1].year.toString()
            holder.itemView.setOnClickListener {
                if (holder.itemView === snapHelper.findSnapView(rvDate.layoutManager)) {

//                    Toast.makeText(
//                        holder.itemView.context,
//                        datesList[holder.getAdapterPosition() - 1],
//                        Toast.LENGTH_SHORT
//                    ).show()
//
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            VIEW_TYPE_EMPTY
        else if (position == years.size + 1)
            VIEW_TYPE_EMPTY
        else
            VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return years.size + 2
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv: TextView = itemView.findViewById(R.id.tv)
    }

    inner class EmptyViewHolder(itemView: View, height: Int) : RecyclerView.ViewHolder(itemView) {
        init {
            val params = RelativeLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, height)
            itemView.layoutParams = params
            itemView.invalidate()
        }
    }
}
