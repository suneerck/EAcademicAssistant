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
import com.app.eAcademicAssistant.activities.coursesManagement.courses.SemSubjectLinkingActivity
import com.app.eAcademicAssistant.model.SemesterMenuModel

class SemSubjectLinkSubjectMenuRvAdapter (

    var context: SemSubjectLinkingActivity,
    var semestersList: ArrayList<SemesterMenuModel>,
    var dialog: Dialog,
    var pbLoading: ProgressBar
):
    RecyclerView.Adapter<SemSubjectLinkSubjectMenuRvAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemSubjectLinkSubjectMenuRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_dialog_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return semestersList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SemSubjectLinkSubjectMenuRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = semestersList[position].name
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != -1) {
                context.selectedSubjectId = semestersList[holder.adapterPosition].id
                context.selectedSubjectName = semestersList[holder.adapterPosition].name
                context.linkSemSubject()
                context.getSubjectsList()
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
