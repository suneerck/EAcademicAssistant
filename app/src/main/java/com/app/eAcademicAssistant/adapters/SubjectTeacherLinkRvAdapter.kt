package com.app.eAcademicAssistant.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.teacherManagement.TeacherSubjectLinkActivity
import com.app.eAcademicAssistant.model.SubjectMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SubjectTeacherLinkRvAdapter (
    val context: TeacherSubjectLinkActivity,
    var subjectList: ArrayList<SubjectMenuModel>
):
    RecyclerView.Adapter<SubjectTeacherLinkRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectTeacherLinkRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_link_subject_to_teacher, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return subjectList.size
    }

    override fun onBindViewHolder(holder: SubjectTeacherLinkRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = subjectList[position].name
        holder.fabAdd.setOnClickListener{
            if(holder.adapterPosition != -1){
                context.linkSubjectTeacher()
            }
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvName)
        var fabAdd : FloatingActionButton = itemView.findViewById(R.id.fabAdd)
    }

}