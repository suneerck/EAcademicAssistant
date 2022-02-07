package com.app.eAcademicAssistant

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.adapters.HomeMenuRvAdapter
import com.app.eAcademicAssistant.model.MainMenuModel
import com.app.eAcademicAssistant.objects.MenuConstants
import com.app.eAcademicAssistant.utills.DatabaseUtils

class MainActivity : AppCompatActivity() {

    private var homeMenuList = ArrayList<MainMenuModel>()
    private lateinit var rvHome: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_main)

        init()

        rvHome.layoutManager = GridLayoutManager(this, 3)


    }

    private fun addMenus() {

        when (DatabaseUtils.getInstance(this@MainActivity).getUser()?.userType) {

            MenuConstants.VISITOR -> {


                //Visitor

                homeMenuList.add(
                    MainMenuModel(
                        "Log In",
                        MenuConstants.LOG_IN,
                        R.drawable.ic_profile
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "College",
                        MenuConstants.COLLEGE,
                        R.drawable.ic_college
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "Map",
                        MenuConstants.MAP,
                        R.drawable.ic_map
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Related Links",
                        MenuConstants.RELATED_LINKS,
                        R.drawable.ic_related_links
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "About Us",
                        MenuConstants.ABOUT_US,
                        R.drawable.ic_about_us
                    )
                )

            }


            //Admin

            MenuConstants.ADMIN -> {
                homeMenuList.add(
                    MainMenuModel(
                        "Profile",
                        MenuConstants.PROFILE,
                        R.drawable.ic_profile
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Day Management",
                        MenuConstants.DAY_MANAGEMENT,
                        R.drawable.ic_day_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Time Table Management",
                        MenuConstants.TIME_TABLE_MANAGEMENT,
                        R.drawable.ic_edit_timetable
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Course Management",
                        MenuConstants.COURSE_MANAGEMENT,
                        R.drawable.ic_course_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Attendance Management",
                        MenuConstants.ATTENDANCE_MANAGEMENT,
                        R.drawable.ic_attendance_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Admin Management",
                        MenuConstants.ADMIN_MANAGEMENT,
                        R.drawable.ic_admin_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "HOD Management",
                        MenuConstants.HOD_MANAGEMENT,
                        R.drawable.ic_hod_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Teachers Management",
                        MenuConstants.TEACHERS_MANAGEMENT,
                        R.drawable.ic_teachers_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Students Management",
                        MenuConstants.STUDENTS_MANAGEMENT,
                        R.drawable.ic_students_management
                    )
                )



                homeMenuList.add(
                    MainMenuModel(
                        "Take Attendance",
                        MenuConstants.TAKE_ATTENDANCE,
                        R.drawable.ic_take_attendance
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Teachers",
                        MenuConstants.TEACHERS,
                        R.drawable.ic_teachers
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Attendance",
                        MenuConstants.ATTENDANCE,
                        R.drawable.ic_attendance
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Time Table",
                        MenuConstants.TIME_TABLE,
                        R.drawable.ic_time_table
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "Students",
                        MenuConstants.STUDENTS,
                        R.drawable.ic_students
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "Deleted Users",
                        MenuConstants.DELETED_USERS,
                        R.drawable.ic_deleted_user
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Notification",
                        MenuConstants.NOTIFICATION,
                        R.drawable.ic_notifications
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "College",
                        MenuConstants.COLLEGE,
                        R.drawable.ic_college
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "Map",
                        MenuConstants.MAP,
                        R.drawable.ic_map
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Related Links",
                        MenuConstants.RELATED_LINKS,
                        R.drawable.ic_related_links
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "About Us",
                        MenuConstants.ABOUT_US,
                        R.drawable.ic_about_us
                    )
                )
            }


            //Hod

            MenuConstants.DEPARTMENT_INCHARGE -> {
                homeMenuList.add(
                    MainMenuModel(
                        "Profile",
                        MenuConstants.PROFILE,
                        R.drawable.ic_profile
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Take Attendance",
                        MenuConstants.TAKE_ATTENDANCE,
                        R.drawable.ic_take_attendance
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Day Management",
                        MenuConstants.DAY_MANAGEMENT,
                        R.drawable.ic_day_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Time Table Management",
                        MenuConstants.TIME_TABLE_MANAGEMENT,
                        R.drawable.ic_edit_timetable
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Teachers Management",
                        MenuConstants.TEACHERS_MANAGEMENT,
                        R.drawable.ic_teachers_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Students Management",
                        MenuConstants.STUDENTS_MANAGEMENT,
                        R.drawable.ic_students_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Attendance Management",
                        MenuConstants.ATTENDANCE_MANAGEMENT,
                        R.drawable.ic_attendance_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Teachers",
                        MenuConstants.TEACHERS,
                        R.drawable.ic_teachers
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Students",
                        MenuConstants.STUDENTS,
                        R.drawable.ic_students
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Time Table",
                        MenuConstants.TIME_TABLE,
                        R.drawable.ic_time_table
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Notification",
                        MenuConstants.NOTIFICATION,
                        R.drawable.ic_notifications
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "College",
                        MenuConstants.COLLEGE,
                        R.drawable.ic_college
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Map",
                        MenuConstants.MAP,
                        R.drawable.ic_map
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Related Links",
                        MenuConstants.RELATED_LINKS,
                        R.drawable.ic_related_links
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "About Us",
                        MenuConstants.ABOUT_US,
                        R.drawable.ic_about_us
                    )
                )

            }


            //Teachers

            MenuConstants.TEACHER -> {
                homeMenuList.add(
                    MainMenuModel(
                        "Profile",
                        MenuConstants.PROFILE,
                        R.drawable.ic_profile
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Take Attendance",
                        MenuConstants.TAKE_ATTENDANCE,
                        R.drawable.ic_take_attendance
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Attendance Management",
                        MenuConstants.ATTENDANCE_MANAGEMENT,
                        R.drawable.ic_attendance_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Students Management",
                        MenuConstants.STUDENTS_MANAGEMENT,
                        R.drawable.ic_students_management
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Teachers",
                        MenuConstants.TEACHERS,
                        R.drawable.ic_teachers
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Time Table",
                        MenuConstants.TIME_TABLE,
                        R.drawable.ic_time_table
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "Students",
                        MenuConstants.STUDENTS,
                        R.drawable.ic_students
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Notification",
                        MenuConstants.NOTIFICATION,
                        R.drawable.ic_notifications
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "College",
                        MenuConstants.COLLEGE,
                        R.drawable.ic_college
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "Map",
                        MenuConstants.MAP,
                        R.drawable.ic_map
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Related Links",
                        MenuConstants.RELATED_LINKS,
                        R.drawable.ic_related_links
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "About Us",
                        MenuConstants.ABOUT_US,
                        R.drawable.ic_about_us
                    )
                )
            }

            //Students

            MenuConstants.STUDENT -> {

                homeMenuList.add(
                    MainMenuModel(
                        "Profile",
                        MenuConstants.PROFILE,
                        R.drawable.ic_profile
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Attendance",
                        MenuConstants.ATTENDANCE,
                        R.drawable.ic_attendance
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Time Table",
                        MenuConstants.TIME_TABLE,
                        R.drawable.ic_time_table
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "College",
                        MenuConstants.COLLEGE,
                        R.drawable.ic_college
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Map",
                        MenuConstants.MAP,
                        R.drawable.ic_map
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Related Links",
                        MenuConstants.RELATED_LINKS,
                        R.drawable.ic_related_links
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Notification",
                        MenuConstants.NOTIFICATION,
                        R.drawable.ic_notifications
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "About Us",
                        MenuConstants.ABOUT_US,
                        R.drawable.ic_about_us
                    )
                )
            }

            else -> {
                homeMenuList.add(
                    MainMenuModel(
                        "Log In",
                        MenuConstants.LOG_IN,
                        R.drawable.ic_profile
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "College",
                        MenuConstants.COLLEGE,
                        R.drawable.ic_college
                    )
                )
                homeMenuList.add(
                    MainMenuModel(
                        "Map",
                        MenuConstants.MAP,
                        R.drawable.ic_map
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "Related Links",
                        MenuConstants.RELATED_LINKS,
                        R.drawable.ic_related_links
                    )
                )

                homeMenuList.add(
                    MainMenuModel(
                        "About Us",
                        MenuConstants.ABOUT_US,
                        R.drawable.ic_about_us
                    )
                )

            }

        }


    }

    private fun init() {
        rvHome = findViewById(R.id.rvHome)
    }

    override fun onResume() {
        super.onResume()
        homeMenuList.clear()
        rvHome.adapter = HomeMenuRvAdapter(
            this@MainActivity,
            homeMenuList
        )
        addMenus()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit..!?")
        builder.setMessage(R.string.QuitDialogue)
        builder.setPositiveButton("Exit"){ dialogInterface: DialogInterface, i: Int ->
            finish()
            finishAffinity()
        }
        builder.setNegativeButton("Cancel"){ dialogInterface: DialogInterface, i: Int ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}