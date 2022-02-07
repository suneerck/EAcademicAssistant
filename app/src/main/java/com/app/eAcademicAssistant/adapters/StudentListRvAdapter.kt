package com.app.eAcademicAssistant.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.app.eAcademicAssistant.model.TeachersMenuModel
import com.app.eAcademicAssistant.model.srModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentListRvAdapter (
    var context : Context,
    var studentsList : ArrayList<TeachersMenuModel>
):
    RecyclerView.Adapter<StudentListRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_user_management, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return studentsList.size
    }

    override fun onBindViewHolder(holder: StudentListRvAdapter.ViewHolder, position: Int) {
        holder.tvStudentName.text = studentsList[position].name
        holder.tvEmail.text = studentsList[position].department
        holder.fabView.hide()
        holder.fabEdit.hide()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvStudentName : TextView = itemView.findViewById(R.id.tvStudentName)
        var tvEmail : TextView = itemView.findViewById(R.id.tvEmail)
        var fabView : FloatingActionButton = itemView.findViewById(R.id.fabView)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
    }

}