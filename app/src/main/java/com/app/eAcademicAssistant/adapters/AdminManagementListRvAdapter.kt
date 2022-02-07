package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.adminsManagement.AdminManagementActivity
import com.app.eAcademicAssistant.activities.adminsManagement.EditAdminActivity
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminManagementListRvAdapter (
    var context : AdminManagementActivity,
    var menuList : ArrayList<StudentsMenuModel>
):
    RecyclerView.Adapter<AdminManagementListRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminManagementListRvAdapter.ViewHolder {
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
        holder.fabEdit.setOnClickListener{
            val intent = Intent(context, EditAdminActivity::class.java)
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
        holder.fabView.setImageResource(R.drawable.ic_basket_black)
        holder.fabView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val id = menuList[position].id
            val name = menuList[position].name
            builder.setTitle("Delete..!?")
            builder.setMessage("Are you sure to delete department of $name")
            builder.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                context.changeAdminStatus(id)
            }
            builder.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvStudentName : TextView = itemView.findViewById(R.id.tvStudentName)
        var tvEmail : TextView = itemView.findViewById(R.id.tvEmail)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
        var fabView : FloatingActionButton = itemView.findViewById(R.id.fabView)
    }

}