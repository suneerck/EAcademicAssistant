package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.hodsManagement.EditHodActivity
import com.app.eAcademicAssistant.activities.hodsManagement.HodManagementListActivity
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HodManagementRvAdapter (
    var context : HodManagementListActivity,
    var menuList : ArrayList<StudentsMenuModel>
):
    RecyclerView.Adapter<HodManagementRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HodManagementRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_user_management, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].name
//        menuList[position].name = "Abcd"
//        notifyDataSetChanged()
        holder.tvEmail.text = menuList[position].email
        holder.fabEdit.setOnClickListener {
            val intent = Intent(context, EditHodActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_hod_id", menuList[position].id)
            intent.putExtra("key_hod_name", menuList[position].name)
            intent.putExtra("key_email", menuList[position].email)
            intent.putExtra("key_password", menuList[position].password)
            intent.putExtra("key_gender", menuList[position].gender.toString())
            intent.putExtra("key_status", menuList[position].status)
            context.startActivity(intent)
        }
        holder.fabDelete.setImageResource(R.drawable.ic_basket_black)
        holder.fabDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val id = menuList[position].id
            val name = menuList[position].name
            builder.setTitle("Delete..!?")
            builder.setMessage("Are you sure to delete $name")
            builder.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                context.changeHodStatus(id)
            }
            builder.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvStudentName)
        var tvEmail : TextView = itemView.findViewById(R.id.tvEmail)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
        var fabDelete : FloatingActionButton = itemView.findViewById(R.id.fabView)
    }

}

