package com.app.eAcademicAssistant.activities.coursesManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.CourseManagementRvAdapter
import com.app.eAcademicAssistant.model.MainMenuModel
import com.app.eAcademicAssistant.objects.MenuConstants

class CourseManagement : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var rvCourseManagement : RecyclerView
    private var courseManagementMenuList = ArrayList<MainMenuModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_course_management)

        init()
        addMenus()

        backBtn.setOnClickListener(){
            finish()
        }

        rvCourseManagement.layoutManager = GridLayoutManager(this, 2)
        rvCourseManagement.adapter = CourseManagementRvAdapter(
            this,
            courseManagementMenuList
        )

    }

    private fun addMenus() {
        courseManagementMenuList.add(
            MainMenuModel(
                "Department",
                MenuConstants.DEPARTMENT,
                R.drawable.ic_department

            )
        )

        courseManagementMenuList.add(
            MainMenuModel(
                "semesters",
                MenuConstants.SEMESTER,
                R.drawable.ic_semester

            )
        )

        courseManagementMenuList.add(
            MainMenuModel(
                "Courses",
                MenuConstants.COURSE,
                R.drawable.ic_courses

            )
        )

        courseManagementMenuList.add(
            MainMenuModel(
                "Subjects",
                MenuConstants.SUBJECT,
                R.drawable.ic_subjects

            )
        )
    }

    private fun init() {
        backBtn = findViewById(R.id.ivBackBtn)
        rvCourseManagement = findViewById(R.id.rvCourseManagement)
    }
}