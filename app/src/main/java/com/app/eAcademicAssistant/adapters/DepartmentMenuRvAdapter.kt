package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.model.DepartmentMenuModel

class DepartmentMenuRvAdapter (
    var context : Context,
    var menuList : ArrayList<DepartmentMenuModel>
        ) :

    RecyclerView.Adapter<DepartmentMenuRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentMenuRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_departments, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].name
        holder.tvHodName.text = menuList[position].inChargeUserName
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            var tvName : TextView = itemView.findViewById(R.id.tvDepartmentName)
            var tvHodName :TextView = itemView.findViewById(R.id.tvHodName)
        }

    }


