package com.app.eAcademicAssistant.adapters

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.coursesManagement.courses.EditCourse
import com.app.eAcademicAssistant.model.DepartmentMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DepartmentListCourseEditRvAdapter (
var context: EditCourse,
var list: ArrayList<DepartmentMenuModel>,
var tvSelectDepartmentInCharge: TextView,
var dialog: Dialog
) :

RecyclerView.Adapter<DepartmentListCourseEditRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DepartmentListCourseEditRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_department_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fabEdit.hide()
        holder.tvName.text = list[position].name
        holder.tvHodName.text = list[position].inChargeUserName
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != -1) {
                context.selectedDepartmentId = list[holder.adapterPosition].id
                tvSelectDepartmentInCharge.text=list[holder.adapterPosition].name
                dialog.cancel()
            } else {
                return@setOnClickListener
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvDepartmentName)
        var tvHodName: TextView = itemView.findViewById(R.id.tvHodName)
        var fabEdit: FloatingActionButton = itemView.findViewById(R.id.fabEdit)
    }

}


