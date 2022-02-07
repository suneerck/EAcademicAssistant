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
import com.app.eAcademicAssistant.activities.coursesManagement.courses.CourseSemLinkActivity
import com.app.eAcademicAssistant.activities.coursesManagement.courses.EditCourse
import com.app.eAcademicAssistant.model.CourseMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CourseMenuRvAdapter (
    var context : Context,
    var menuList : ArrayList<CourseMenuModel>
):
    RecyclerView.Adapter<CourseMenuRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseMenuRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_courses, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].name
        holder.tvDepartment.text = "Department of " + menuList[position].departmentName
        holder.fabEdit.setOnClickListener {
            if (holder.adapterPosition != -1) {
                val intent = Intent(context, EditCourse::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("key_course_id", menuList[position].id)
                intent.putExtra("key_course_name", menuList[position].name)
                intent.putExtra("key_department_id", menuList[position].departmentId)
                intent.putExtra("key_department_name", menuList[position].departmentName)
                intent.putExtra("key_sem_count", menuList[position].semCount.toString())
                intent.putExtra("key_status", menuList[position].status)
                context.startActivity(intent)
            } else {
                return@setOnClickListener
            }
        }

        holder.fabLink.setOnClickListener {
            val intent=Intent(context, CourseSemLinkActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_course_id",menuList[position].id)
            intent.putExtra("key_course_name",menuList[position].name)
            intent.putExtra("key_department_id",menuList[position].departmentId)
            intent.putExtra("key_department_name",menuList[position].departmentName)
            intent.putExtra("key_sem_count",menuList[position].semCount.toString())
            intent.putExtra("key_status",menuList[position].status)
            context.startActivity(intent)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvCourseName)
        var tvDepartment : TextView = itemView.findViewById(R.id.tvDepartment)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
        var fabLink : FloatingActionButton = itemView.findViewById(R.id.fabLink)
    }

}