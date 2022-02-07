package com.app.eAcademicAssistant.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.studentManagement.StudentSemCourseLinkingActivity
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentSemListRvAdapter(
    var studentsList: ArrayList<StudentsMenuModel>,
    val context: StudentSemCourseLinkingActivity
):
    RecyclerView.Adapter<StudentSemListRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentSemListRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_user_management, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return studentsList.size
    }

    override fun onBindViewHolder(holder: StudentSemListRvAdapter.ViewHolder, position: Int) {
        holder.tvStudentName.text = studentsList[position].name
        holder.tvEmail.text = studentsList[position].email
        holder.fabView.hide()
        holder.fabEdit.setImageResource(R.drawable.ic_basket_black)
        holder.fabEdit.setOnClickListener {
            context.studentLinkingId = studentsList[position].id
            context.dialogStudentStatusChange(
                studentsList[position].name
            )
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvStudentName : TextView = itemView.findViewById(R.id.tvStudentName)
        var tvEmail : TextView = itemView.findViewById(R.id.tvEmail)
        var fabView : FloatingActionButton = itemView.findViewById(R.id.fabView)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
    }

}