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
import com.app.eAcademicAssistant.activities.coursesManagement.semester.EditSemester
import com.app.eAcademicAssistant.activities.coursesManagement.semester.Semesters
import com.app.eAcademicAssistant.model.SemesterMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SemesterMenuRvAdapter(

    var context: Context,
    var menuList: ArrayList<SemesterMenuModel>,
    var SEMESTER_EDIT: Int

):
    RecyclerView.Adapter<SemesterMenuRvAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemesterMenuRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_semesters, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SemesterMenuRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].name
        holder.fabEdit.setOnClickListener{
            val intent = Intent(context, EditSemester::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_sem_id", menuList[position].id)
            intent.putExtra("key_sem_name", menuList[position].name)
            intent.putExtra("key_status", menuList[position].status)
            (context as Semesters).startActivityForResult(intent,SEMESTER_EDIT)
        }

    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvSemesterName)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
    }
    }