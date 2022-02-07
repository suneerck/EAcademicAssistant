package com.app.eAcademicAssistant.activities.studentAttendanceAdd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.app.eAcademicAssistant.R

class StudentAttendanceAddHome : AppCompatActivity() {

    private lateinit var tvCourse : TextView
    private lateinit var datePicker : DatePicker
    private lateinit var tvSemester : TextView
    private lateinit var tvPeriod : TextView
    private lateinit var backBtn : ImageView
    private lateinit var btnSubmit : Button
    private lateinit var pbLoading : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_attendance_add_home)

        init()

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun init() {
        tvCourse = findViewById(R.id.tvCourse)
        tvSemester = findViewById(R.id.tvSemester)
        tvPeriod = findViewById(R.id.tvSelectPeriod)
        datePicker = findViewById(R.id.datePicker)
        backBtn = findViewById(R.id.ivBackBtn)
        pbLoading = findViewById(R.id.pbLoading)
        btnSubmit = findViewById(R.id.btnSubmit)
    }
}