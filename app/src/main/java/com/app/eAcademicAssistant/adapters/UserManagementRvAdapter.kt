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
import com.app.eAcademicAssistant.activities.hodsManagement.EditHodActivity
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserManagementRvAdapter (
    var context : Context,
    var menuList : ArrayList<StudentsMenuModel>
):
    RecyclerView.Adapter<UserManagementRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserManagementRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_user_management, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvStudentName.text = menuList[position].name
        holder.tvEmail.text = menuList[position].email


    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvStudentName : TextView = itemView.findViewById(R.id.tvStudentName)
        var tvEmail : TextView = itemView.findViewById(R.id.tvEmail)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
        var fabView : FloatingActionButton = itemView.findViewById(R.id.fabView)
    }

}