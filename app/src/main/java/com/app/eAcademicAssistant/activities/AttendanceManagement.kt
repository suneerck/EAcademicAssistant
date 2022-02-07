package com.app.eAcademicAssistant.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.AttendanceManagementRvAdapter
import com.app.eAcademicAssistant.model.StudentsMenuModel

class AttendanceManagement : AppCompatActivity() {

    private lateinit var backBtn : ImageView
    private lateinit var rvAttendanceManagement: RecyclerView
    private var attendanceManagementMenuList = ArrayList<StudentsMenuModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_attendance_management)

        init()

        backBtn.setOnClickListener(){
            finish()
        }

        rvAttendanceManagement.layoutManager = GridLayoutManager(this, 1)
        rvAttendanceManagement.adapter = AttendanceManagementRvAdapter(
            this@AttendanceManagement,
            attendanceManagementMenuList
        )
    }


    private fun init() {
        backBtn = findViewById(R.id.ivBackBtn)
        rvAttendanceManagement = findViewById(R.id.rvAttendanceManagement)
    }
}