package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.*
import com.app.eAcademicAssistant.activities.*
import com.app.eAcademicAssistant.activities.adminsManagement.AdminManagementActivity
import com.app.eAcademicAssistant.activities.attendance.ViewAttendanceActivity
import com.app.eAcademicAssistant.activities.studentManagement.StudentManagementListActivity
import com.app.eAcademicAssistant.activities.coursesManagement.CourseManagement
import com.app.eAcademicAssistant.activities.dayManagement.DayManagementActivity
import com.app.eAcademicAssistant.activities.hodsManagement.HodManagementListActivity
import com.app.eAcademicAssistant.activities.takeAttendance.SelectDetailsToTakeAttendance
import com.app.eAcademicAssistant.activities.teacherManagement.TeachersManagementListActivity
import com.app.eAcademicAssistant.activities.timeTableManagement.TimeTableManagementActivity
import com.app.eAcademicAssistant.model.MainMenuModel
import com.app.eAcademicAssistant.objects.MenuConstants
import com.app.eAcademicAssistant.staticActivities.AboutUs
import com.app.eAcademicAssistant.staticActivities.CollegeDetails
import com.app.eAcademicAssistant.staticActivities.RelatedLinks
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeMenuRvAdapter(
    var context: Context,
    var menuList: ArrayList<MainMenuModel>
) :
    RecyclerView.Adapter<HomeMenuRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.btn_item_main_menu, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fab.setImageResource(menuList[position].drawable)
        holder.tvLabel.text = menuList[position].name
        when (menuList[position].code) {

            MenuConstants.LOG_IN ->{
                holder.itemView.setOnClickListener{
                    if (holder.adapterPosition == -1){
                        return@setOnClickListener
                    }
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }
            MenuConstants.PROFILE -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, Profile::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.COURSE_MANAGEMENT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, CourseManagement::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.DAY_MANAGEMENT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, DayManagementActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.TIME_TABLE_MANAGEMENT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, TimeTableManagementActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.ATTENDANCE_MANAGEMENT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, AttendanceManagement::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.ADMIN_MANAGEMENT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, AdminManagementActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.HOD_MANAGEMENT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, HodManagementListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.TEACHERS_MANAGEMENT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, TeachersManagementListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.STUDENTS_MANAGEMENT -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, StudentManagementListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.TAKE_ATTENDANCE -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, SelectDetailsToTakeAttendance::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.TEACHERS -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, TeachersListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.ATTENDANCE -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, ViewAttendanceActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.TIME_TABLE -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
//                    val intent = Intent(context, TimeTableSelectCourseActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    context.startActivity(intent)
                }
            }

            MenuConstants.STUDENTS -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent=Intent(context,StudentsListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)

                }
            }

            MenuConstants.NOTIFICATION -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                }
            }

            MenuConstants.MAP -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }

                }
            }

            MenuConstants.COLLEGE -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, CollegeDetails::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.RELATED_LINKS -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, RelatedLinks::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.ABOUT_US -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, AboutUs::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            MenuConstants.DELETED_USERS -> {
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == -1) {
                        return@setOnClickListener
                    }
                    val intent = Intent(context, DeletedUsersActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvLabel: TextView = itemView.findViewById(R.id.iconTextMain)
        var fab: FloatingActionButton = itemView.findViewById(R.id.iconMain)
    }
}
