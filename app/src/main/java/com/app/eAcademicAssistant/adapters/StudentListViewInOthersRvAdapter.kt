package com.app.eAcademicAssistant.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.attendance.ViewAttendanceActivity
import com.app.eAcademicAssistant.activities.attendance.ViewAttendanceInOthersActivity
import com.app.eAcademicAssistant.activities.hodsManagement.EditHodActivity
import com.app.eAcademicAssistant.model.srModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentListViewInOthersRvAdapter (
    var context : ViewAttendanceActivity,
    var studentsList : ArrayList<srModel>
):
    RecyclerView.Adapter<StudentListViewInOthersRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListViewInOthersRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_admin, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return studentsList.size
    }

    override fun onBindViewHolder(holder: StudentListViewInOthersRvAdapter.ViewHolder, position: Int) {
        holder.tvAdmin.text = studentsList[position].name
        holder.fabDelete.setImageResource(R.drawable.ic_eye)
        holder.fabDelete.setOnClickListener {
            val intent = Intent(context, ViewAttendanceInOthersActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_student_id", studentsList[position].id)
            intent.putExtra("key_student_name", studentsList[position].name)
            intent.putExtra("selectedSemCourseId", context.selectedSemCourseId)
            context.startActivity(intent)
        }

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvAdmin : TextView = itemView.findViewById(R.id.tvAdmin)
        var fabDelete : FloatingActionButton = itemView.findViewById(R.id.fabDelete)
    }

}