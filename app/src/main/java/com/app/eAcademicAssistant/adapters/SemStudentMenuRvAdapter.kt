package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.studentManagement.StudentSemCourseLinkingActivity
import com.app.eAcademicAssistant.model.UserModel1
import com.app.eAcademicAssistant.model.srModel

class SemStudentMenuRvAdapter(
    var context: StudentSemCourseLinkingActivity,
    var studentsList: ArrayList<UserModel1>,
    var dialog: Dialog,
    var pbLoading: ProgressBar
):
    RecyclerView.Adapter<SemStudentMenuRvAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemStudentMenuRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_dialog_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return studentsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SemStudentMenuRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = studentsList[position].name
        holder.tvMail.text = studentsList[position].mail
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != -1) {
                context.selectedStudentName = studentsList[holder.adapterPosition].name
                context.selectedStudentId = studentsList[holder.adapterPosition].id
                context.linkSemStudent()
                dialog.cancel()
            } else {
                return@setOnClickListener
            }
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvName)
        var tvMail : TextView = itemView.findViewById(R.id.tvMail)
    }
}
