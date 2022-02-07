package com.app.eAcademicAssistant.adapters

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.takeAttendance.SelectDetailsToTakeAttendance
import com.app.eAcademicAssistant.model.srModel

class SemListTakeAttendanceRvAdapter(

    var context: SelectDetailsToTakeAttendance,
    var list: ArrayList<srModel>,
    var tvCourse: TextView,
    var dialog: Dialog,
    var tvSemester: TextView,
    var clButton: ConstraintLayout
) :

    RecyclerView.Adapter<SemListTakeAttendanceRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SemListTakeAttendanceRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_department_in_charge_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = list[position].name
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != -1) {
                context.selectedSemId = list[holder.adapterPosition].id
                context.semId1 = context.sList[position].id
                tvCourse.text=list[holder.adapterPosition].name
                dialog.cancel()
                context.tvPeriod.visibility = View.VISIBLE
            } else {
                return@setOnClickListener
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvUserName)
    }

}