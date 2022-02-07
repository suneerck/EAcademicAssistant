package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.timeTableManagement.TimeTableSelectSubjectsActivity
import com.app.eAcademicAssistant.model.PeriodModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SemSubLinkRvMainAdapter(
    var context : TimeTableSelectSubjectsActivity,
    var menuList : ArrayList<PeriodModel>
):
    RecyclerView.Adapter<SemSubLinkRvMainAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemSubLinkRvMainAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_sub_link_rv_main_adapter, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvSubjectName.text = menuList[position].SubName
        holder.tvPriority.text =
            when (position) {
                0 -> {
                    "1st period"
                }
                1 -> {
                    "2nd Period"
                }
                2 -> {
                    "3rd Period"
                }
                else -> {
                    "${position+1}th Period"
                }
            }
        holder.fabDelete.setOnClickListener {
            context.subPeriodList.removeAt(position)
            context.listSubjects()
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvPriority : TextView = itemView.findViewById(R.id.tvPriority)
        var tvSubjectName : TextView = itemView.findViewById(R.id.tvSubjectName)
        var fabDelete : FloatingActionButton = itemView.findViewById(R.id.fabDelete)
    }

}