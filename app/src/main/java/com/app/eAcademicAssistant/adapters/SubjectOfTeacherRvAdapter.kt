package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.model.SubjectMenuModel

class SubjectOfTeacherRvAdapter (
    var context : Context,
    var menuList : ArrayList<SubjectMenuModel>
):
    RecyclerView.Adapter<SubjectOfTeacherRvAdapter.ViewHolder> () {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubjectOfTeacherRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_teachers_subject, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SubjectOfTeacherRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].name
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvSubName)
        var tvSem: TextView = itemView.findViewById(R.id.tvSemester)
        var tvCourse: TextView = itemView.findViewById(R.id.tvCourse)
    }
}