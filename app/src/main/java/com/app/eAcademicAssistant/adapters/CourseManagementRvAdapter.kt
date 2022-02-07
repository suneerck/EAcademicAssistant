package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.activities.coursesManagement.departments.Departments
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.coursesManagement.courses.Courses
import com.app.eAcademicAssistant.activities.coursesManagement.semester.Semesters
import com.app.eAcademicAssistant.activities.coursesManagement.subjects.Subjects
import com.app.eAcademicAssistant.model.MainMenuModel
import com.app.eAcademicAssistant.objects.MenuConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CourseManagementRvAdapter (
    var context : Context,
    var menuList : ArrayList<MainMenuModel>
    ) :

        RecyclerView.Adapter<CourseManagementRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseManagementRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.btn_item_course_management, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CourseManagementRvAdapter.ViewHolder, position: Int) {
        holder.fab.setImageResource(menuList[position].drawable)
        holder.tvLabel.text = menuList[position].name
        when (menuList[position].code) {

            MenuConstants.DEPARTMENT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, Departments::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.SEMESTER -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, Semesters::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }
            MenuConstants.COURSE -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, Courses::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }
            MenuConstants.SUBJECT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, Subjects::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvLabel: TextView = itemView.findViewById(R.id.iconTextMainCourse)
        var fab: FloatingActionButton = itemView.findViewById(R.id.iconMainCourse)
    }
}