package com.app.eAcademicAssistant.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.coursesManagement.departments.DepartmentEditActivity
import com.app.eAcademicAssistant.activities.coursesManagement.departments.Departments
import com.app.eAcademicAssistant.model.DepartmentMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DepartmentListRvAdapter(
    var context: Departments,
    var list: ArrayList<DepartmentMenuModel>
) :

    RecyclerView.Adapter<DepartmentListRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentListRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_department_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = list[position].name
        holder.tvHodName.text = list[position].inChargeUserName
        holder.fabEdit.setOnClickListener {

            val intent = Intent(context, DepartmentEditActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_department_id", list[position].id)
            intent.putExtra("key_department_name", list[position].name)
            intent.putExtra("key_department_incharge_user_name", list[position].inChargeUserName)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvDepartmentName)
        var tvHodName :TextView = itemView.findViewById(R.id.tvHodName)
        var fabEdit :FloatingActionButton = itemView.findViewById(R.id.fabEdit)
        }

    }


