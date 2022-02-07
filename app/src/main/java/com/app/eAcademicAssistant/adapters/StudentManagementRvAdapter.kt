package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.studentManagement.EditStudent
import com.app.eAcademicAssistant.activities.studentManagement.ViewStudent
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentManagementRvAdapter (
    var context : Context,
    var studentsList : ArrayList<StudentsMenuModel>
):
    RecyclerView.Adapter<StudentManagementRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentManagementRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_user_management, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return studentsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvStudentName.text = studentsList[position].name
        holder.tvEmail.text = studentsList[position].email
        holder.fabEdit.setOnClickListener{
            val intent = Intent(context, EditStudent::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_student_id", studentsList[position].id)
            intent.putExtra("key_student_name", studentsList[position].name)
            intent.putExtra("key_student_email", studentsList[position].email)
            intent.putExtra("key_password", studentsList[position].password)
            intent.putExtra("key_user_type", studentsList[position].userType)
            intent.putExtra("key_gender", studentsList[position].gender.toString())
            intent.putExtra("key_status", studentsList[position].status)
            context.startActivity(intent)
        }

        holder.fabView.setOnClickListener{
            val intent = Intent(context, ViewStudent::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_student_id", studentsList[position].id)
            intent.putExtra("key_student_name", studentsList[position].name)
            intent.putExtra("key_student_email", studentsList[position].email)
            intent.putExtra("key_password", studentsList[position].password)
            intent.putExtra("key_user_type", studentsList[position].userType)
            intent.putExtra("key_gender", studentsList[position].gender.toString())
            intent.putExtra("key_status", studentsList[position].status)
            context.startActivity(intent)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvStudentName : TextView = itemView.findViewById(R.id.tvStudentName)
        var tvEmail : TextView = itemView.findViewById(R.id.tvEmail)
        var fabView : FloatingActionButton = itemView.findViewById(R.id.fabView)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
    }

}