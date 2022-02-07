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
import com.app.eAcademicAssistant.activities.teacherManagement.TeacherSubjectLinkListActivity
import com.app.eAcademicAssistant.activities.teacherManagement.EditTeacherActivity
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TeachersManagementRvAdapter (
    var context : Context,
    var menuList : ArrayList<StudentsMenuModel>
):
    RecyclerView.Adapter<TeachersManagementRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeachersManagementRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_teachers_management, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].name
        holder.tvDepartment.text = menuList[position].email
        holder.fabEdit.setOnClickListener{
            val intent = Intent(context, EditTeacherActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_id", menuList[position].id)
            intent.putExtra("key_name", menuList[position].name)
            intent.putExtra("key_email", menuList[position].email)
            intent.putExtra("key_password", menuList[position].password)
            intent.putExtra("key_user_type", menuList[position].userType)
            intent.putExtra("key_gender", menuList[position].gender.toString())
            intent.putExtra("key_status", menuList[position].status)
            context.startActivity(intent)
        }

        holder.fabView.setImageResource(R.drawable.ic_subjects)
        holder.fabView.setOnClickListener{
            val intent = Intent(context, TeacherSubjectLinkListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_id", menuList[position].id)
            intent.putExtra("key_name", menuList[position].name)
            intent.putExtra("key_email", menuList[position].email)
            intent.putExtra("key_password", menuList[position].password)
            intent.putExtra("key_user_type", menuList[position].userType)
            intent.putExtra("key_gender", menuList[position].gender.toString())
            intent.putExtra("key_status", menuList[position].status)
            context.startActivity(intent)
        }


    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvTeachers)
        var tvDepartment : TextView = itemView.findViewById(R.id.tvDepartment)
        var fabView : FloatingActionButton = itemView.findViewById(R.id.fabView)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
    }

}