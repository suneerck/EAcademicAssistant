package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.coursesManagement.courses.SemSubjectLinkingActivity
import com.app.eAcademicAssistant.model.SemCourseMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SemSubjectLinkSubjectsListRvAdapter (
    var context : SemSubjectLinkingActivity,
    var menuList : ArrayList<SemCourseMenuModel>

):
    RecyclerView.Adapter<SemSubjectLinkSubjectsListRvAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemSubjectLinkSubjectsListRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_sem_course_linking, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SemSubjectLinkSubjectsListRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].semName
        holder.fabLink.hide()

        holder.fabDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val idSemSub = menuList[position].semCourseId
            val nameSub = menuList[position].semName
            builder.setTitle("Delete..!?")
            builder.setMessage("Are you sure to delete $nameSub")
            builder.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                context.changeSubjectStatus(idSemSub)
            }
            builder.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvName)
        var fabLink : FloatingActionButton = itemView.findViewById(R.id.fabLink)
        var fabDelete : FloatingActionButton = itemView.findViewById(R.id.fabDelete)
    }
}