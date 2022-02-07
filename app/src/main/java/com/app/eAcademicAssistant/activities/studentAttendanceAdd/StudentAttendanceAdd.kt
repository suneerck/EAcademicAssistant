package com.app.eAcademicAssistant.activities.studentAttendanceAdd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R

class StudentAttendanceAdd : AppCompatActivity() {

    private lateinit var rv : RecyclerView
    private lateinit var backBtn : ImageView
    private lateinit var pbLoading : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_attendance_add)

        init()

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun init() {
        rv = findViewById(R.id.rv)
        backBtn = findViewById(R.id.ivBackBtn)
        pbLoading = findViewById(R.id.pbLoading)
    }
}