package com.app.eAcademicAssistant.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.teacherManagement.TeacherSubjectLinkListActivity
import com.app.eAcademicAssistant.model.TeachersSubjectListModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SubjectTeacherListRvAdapter(
    val context: TeacherSubjectLinkListActivity,
    var teachersSubjectList: ArrayList<TeachersSubjectListModel>
) :
    RecyclerView.Adapter<SubjectTeacherListRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubjectTeacherListRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_subject_teacher_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return teachersSubjectList.size
    }

    override fun onBindViewHolder(holder: SubjectTeacherListRvAdapter.ViewHolder, position: Int) {
        holder.tvSubjectName.text = teachersSubjectList[position].subName
        holder.tvCourse.text = teachersSubjectList[position].courseName
        holder.tvSemester.text = teachersSubjectList[position].semName
        holder.fabDelete.setOnClickListener {
            if (holder.adapterPosition != -1) {
            }
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSubjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
        var tvCourse: TextView = itemView.findViewById(R.id.tvCourse)
        var tvSemester: TextView = itemView.findViewById(R.id.tvSemester)
        var fabDelete: FloatingActionButton = itemView.findViewById(R.id.fabDelete)
    }

}