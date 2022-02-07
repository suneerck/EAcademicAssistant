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
import com.app.eAcademicAssistant.activities.coursesManagement.courses.CourseSemLinkActivity
import com.app.eAcademicAssistant.model.SemesterMenuModel

class SemCourseLinkSemesterMenuRvAdapter(

    var context: CourseSemLinkActivity,
    var semestersList: ArrayList<SemesterMenuModel>,
    var dialog: Dialog,
    var pbLoading: ProgressBar
):
    RecyclerView.Adapter<SemCourseLinkSemesterMenuRvAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemCourseLinkSemesterMenuRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_dialog_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return semestersList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SemCourseLinkSemesterMenuRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = semestersList[position].name
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != -1) {
                context.selectedSemId = semestersList[holder.adapterPosition].id
                context.selectedSemName = semestersList[holder.adapterPosition].name
                context.linkSemCourse()
                context.getSemList()
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
