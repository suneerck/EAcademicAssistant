package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.takeAttendance.StudentsViewForTakeAttendanceActivity
import com.app.eAcademicAssistant.model.StudentAttendanceModel
import com.app.eAcademicAssistant.model.TeachersMenuModel

class StudentAttendanceAddRvAdapter(
    val context: StudentsViewForTakeAttendanceActivity,
    val menuList: ArrayList<StudentAttendanceModel>
) :
    RecyclerView.Adapter<StudentAttendanceAddRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttendanceAddRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_student_list_for_attendance, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].name
//        holder.rbPresent.isChecked = true
        holder.tvSiNo.text = "${position+1}"

        holder.rgAttendance.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rbPresent){
                context.attendanceList[position].attendanceStatus = 1
            } else if (checkedId == R.id.rbAbsent){
                context.attendanceList[position].attendanceStatus = 2
            }
        }

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvName)
        var rgAttendance : RadioGroup = itemView.findViewById(R.id.rgAttendance)
        var tvSiNo : TextView = itemView.findViewById(R.id.tvSiNo)
    }

}