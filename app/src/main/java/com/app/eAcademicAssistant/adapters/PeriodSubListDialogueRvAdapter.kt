package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.timeTableManagement.TimeTableSelectSubjectsActivity
import com.app.eAcademicAssistant.model.PeriodModel
import com.app.eAcademicAssistant.model.SemCourseMenuModel

class PeriodSubListDialogueRvAdapter(
    var context: TimeTableSelectSubjectsActivity,
    var subList: ArrayList<SemCourseMenuModel>,
    var dialog: Dialog,
    var pbLoading1: ProgressBar
):
    RecyclerView.Adapter<PeriodSubListDialogueRvAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeriodSubListDialogueRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_dialog_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return subList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PeriodSubListDialogueRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = subList[position].semName
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != -1) {
                context.subPeriodList.add(
                    PeriodModel(
                        1,
                        subList[position].semName,
                        subList[position].semId
                    )
                )
                context.listSubjects()
                dialog.cancel()
            } else {
                return@setOnClickListener
            }
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvName)
    }
}
