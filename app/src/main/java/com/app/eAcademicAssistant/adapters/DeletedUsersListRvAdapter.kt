package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.DeletedUsersActivity
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.app.eAcademicAssistant.model.UsersModel2
import com.app.eAcademicAssistant.model.srModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DeletedUsersListRvAdapter (
    var context : DeletedUsersActivity,
    var menuList : ArrayList<StudentsMenuModel>
):
    RecyclerView.Adapter<DeletedUsersListRvAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletedUsersListRvAdapter.ViewHolder {
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
        holder.tvEmail.text = menuList[position].email
        holder.fabDelete.visibility = View.GONE
        holder.fabEdit.setImageResource(R.drawable.ic_recycle)
        holder.fabEdit.setOnClickListener {
            dialogStatusChange(position)
        }
    }

    private fun dialogStatusChange(position: Int) {
        val builder = AlertDialog.Builder(context)
        val id = menuList[position].id
        val name = menuList[position].name
        builder.setTitle("Delete..!?")
        builder.setMessage("Are you sure to backup $name")
        builder.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
            context.changeUsersStatus(id)
        }
        builder.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvStudentName)
        var tvEmail : TextView = itemView.findViewById(R.id.tvEmail)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
        var fabDelete : FloatingActionButton = itemView.findViewById(R.id.fabView)
    }

}
