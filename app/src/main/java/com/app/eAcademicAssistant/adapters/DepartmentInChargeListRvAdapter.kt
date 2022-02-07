package com.app.eAcademicAssistant.adapters

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.coursesManagement.departments.DepartmentCreateActivity
import com.app.eAcademicAssistant.model.UserListModel

class DepartmentInChargeListRvAdapter(
    var context: DepartmentCreateActivity,
    var list: ArrayList<UserListModel>,
    var dialog: Dialog,
    var tvSelectDepartmentInCharge: TextView
) :

    RecyclerView.Adapter<DepartmentInChargeListRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentInChargeListRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_dialog_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvUserName.text = list[position].name
        holder.itemView.setOnClickListener {
            if(holder.adapterPosition != -1){
                context.selectedDepartmentInCharge=list[holder.adapterPosition].userId
                tvSelectDepartmentInCharge.text=list[holder.adapterPosition].name
                dialog.cancel()
            }
            else{
                return@setOnClickListener
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            var tvUserName : TextView = itemView.findViewById(R.id.tvName)
        }

    }


