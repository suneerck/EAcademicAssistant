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
import com.app.eAcademicAssistant.activities.coursesManagement.subjects.EditSubject
import com.app.eAcademicAssistant.activities.coursesManagement.subjects.Subjects
import com.app.eAcademicAssistant.model.SubjectMenuModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SubjectMenuRvAdapter (

    var context : Subjects,
    var menuList : ArrayList<SubjectMenuModel>
):
    RecyclerView.Adapter<SubjectMenuRvAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectMenuRvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_subjects, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SubjectMenuRvAdapter.ViewHolder, position: Int) {
        holder.tvName.text = menuList[position].name

        holder.fabEdit.setOnClickListener(){
            val intent = Intent(context, EditSubject::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_sub_id", menuList[position].id)
            intent.putExtra("key_sub_name", menuList[position].name)
            intent.putExtra("key_status", menuList[position].status)
            context.startActivity(intent)
        }

        holder.fabDelete.setOnClickListener(){

            val subId = menuList[position].id
            val name = menuList[position].name

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete..!?")
            builder.setMessage("Are you sure to delete $name")
            builder.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                context.subjectStatusChange(subId)
            }
            builder.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName : TextView = itemView.findViewById(R.id.tvSubName)
        var fabEdit : FloatingActionButton = itemView.findViewById(R.id.fabEdit)
        var fabDelete : FloatingActionButton = itemView.findViewById(R.id.fabDelete)
    }
}