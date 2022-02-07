package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.coursesManagement.courses.CourseSemLinkActivity
import com.app.eAcademicAssistant.activities.coursesManagement.courses.SemSubjectLinkingActivity
import com.app.eAcademicAssistant.model.SemCourseMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SemCourseLinkMenuRvAdapter (
    var context : CourseSemLinkActivity,
    var menuList : ArrayList<SemCourseMenuModel>

):
    RecyclerView.Adapter<SemCourseLinkMenuRvAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemCourseLinkMenuRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_sem_course_linking, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SemCourseLinkMenuRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].semName
        holder.fabLink.setImageResource(R.drawable.ic_subjects)
        holder.fabLink.setOnClickListener {
            if (holder.adapterPosition != -1) {
                val intent = Intent(context, SemSubjectLinkingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("key_sem_course_id", menuList[position].semCourseId)
                intent.putExtra("key_sem_id", menuList[position].semId)
                intent.putExtra("key_sem_name", menuList[position].semName)
                intent.putExtra("key_course_id", menuList[position].courseId)
                intent.putExtra("key_course_name", menuList[position].courseName)
                intent.putExtra("key_status", menuList[position].status)
                context.startActivity(intent)
            } else {
                return@setOnClickListener
            }
        }
        holder.fabDelete.setOnClickListener {
            if (holder.adapterPosition != -1) {
                val semCourseId2 = menuList[position].semCourseId
                val semName = menuList[position].semName
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Delete..!?")
                builder.setMessage("Are you sure to delete $semName")
                builder.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->

                    context.semStatusChange(semCourseId2)
                }
                builder.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->

                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
            } else {
                return@setOnClickListener
            }
        }
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvName)
        var fabLink : FloatingActionButton = itemView.findViewById(R.id.fabLink)
        var fabDelete : FloatingActionButton = itemView.findViewById(R.id.fabDelete)
    }
}