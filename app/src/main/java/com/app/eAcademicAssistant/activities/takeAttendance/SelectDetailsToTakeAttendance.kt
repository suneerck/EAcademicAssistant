package com.app.eAcademicAssistant.activities.takeAttendance

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.timeTableManagement.TimeTableCalendarListActivity
import com.app.eAcademicAssistant.adapters.CourseListTakeAttendanceRvAdapter
import com.app.eAcademicAssistant.adapters.PeriodListToTakeAttendanceDetailsRvAdapter
import com.app.eAcademicAssistant.adapters.SemListTakeAttendanceRvAdapter
import com.app.eAcademicAssistant.model.PeriodSubjectListModel
import com.app.eAcademicAssistant.model.srModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SelectDetailsToTakeAttendance : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var clButton: ConstraintLayout
    private lateinit var tvCourse: TextView
    lateinit var tvPeriod: TextView
    private lateinit var tvMain: TextView
    lateinit var tvSubLabel: TextView
    lateinit var tvSubject: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvSemester: TextView
    lateinit var btnSubmit: Button
    private lateinit var pbLoading1: ProgressBar
    private var getUsersList: Call? = null
    private var cList = ArrayList<srModel>()
    var sList = ArrayList<srModel>()
    private var scList = ArrayList<srModel>()
    var selectedCourseId = ""
    var selectedSemId = ""
    var departmentId = ""
    var semId = ""
    var semId1 = ""
    var selectedDayPeriodLinkingId = ""
    val userType = DatabaseUtils.getInstance(this).getUser()?.userType
    private val myCalendar: Calendar = Calendar.getInstance()
    private var setStartTime: Long = 0
    private var setEndTime: Long = 0
    private val currentDate = Calendar.getInstance().timeInMillis
    private val subList = ArrayList<PeriodSubjectListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_details_to_take_attandance)

        init()

        tvMain.text = "Take Attendance"

        if (userType == 1) {
            getDepartmentList()
        }

//        val startDate =
//            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                myCalendar.set(Calendar.YEAR, year)
//                myCalendar.set(Calendar.MONTH, monthOfYear)
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                updateStartDate()
//            }
//
//        val endDate =
//            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                myCalendar.set(Calendar.YEAR, year)
//                myCalendar.set(Calendar.MONTH, monthOfYear)
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                updateEndDate()
//                tvPeriod.visibility=View.VISIBLE
//            }


//        Log.e("__________t__", "dateToday: $currentDate")

//        tvStartDate.setOnClickListener() {
//            DatePickerDialog(
//                this@SelectDetailsToTakeAttendance, startDate, myCalendar
//                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                myCalendar.get(Calendar.DAY_OF_MONTH)
//            ).show()
//        }
//
//        tvEndDate.setOnClickListener {
//            DatePickerDialog(
//                this@SelectDetailsToTakeAttendance, endDate, myCalendar
//                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                myCalendar.get(Calendar.DAY_OF_MONTH)
//            ).show()
//        }

        backBtn.setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {

            val intent = Intent(this, StudentsViewForTakeAttendanceActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("day_period_linking_id", selectedDayPeriodLinkingId)
            intent.putExtra("sem_id", semId1)
            intent.putExtra("course_id", selectedCourseId)
            this.startActivity(intent)

        }


        tvCourse.setOnClickListener {
            selectCourse()
        }

        tvSemester.setOnClickListener {
            selectSem()
        }

        tvPeriod.setOnClickListener {
            selectPeriod()
        }

    }

    private fun updateStartDate() {
        val format = "dd/MM/yy"
        val sdf = SimpleDateFormat(format, Locale.US)
        tvStartDate.text = sdf.format(myCalendar.time)
        setStartTime = myCalendar.timeInMillis
    }

    private fun updateEndDate() {
        val format = "dd/MM/yy"
        val sdf = SimpleDateFormat(format, Locale.US)
        tvEndDate.text = sdf.format(myCalendar.time)
        setEndTime = myCalendar.timeInMillis
    }

    private fun getSubjects(
        rv: RecyclerView,
        pbLoading: ProgressBar,
        dialog: Dialog
    ) {
        this.pbLoading1.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val arraySemCourseLinkingIdS = JSONArray()
        arraySemCourseLinkingIdS.put(selectedSemId)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@SelectDetailsToTakeAttendance).getUser()?.userId
        )
        jsonObject.put("startTime", currentDate)
        jsonObject.put("endTime", currentDate + 86400000)
        jsonObject.put("arraySemCourseLinkingIds", arraySemCourseLinkingIdS)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getSemCourseList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_DAY_LINKING_LIST_BY_TIME).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getSemCourseList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.no_internet_connection)
                        )
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.something_went_wrong)
                        )
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getSemCourseList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            subList.clear()
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (j in 0 until list.length()) {
                                val joList = list.getJSONObject(j)
                                val dayPeriodLinkingId = joList.getString("_id")
                                val priority = joList.getInt("_priority")
                                val status = joList.getInt("_status")
                                val joSubjectDetails = joList.getJSONObject("subjectDetails")
                                val subName = joSubjectDetails.getString("_name")
                                val subStatus = joSubjectDetails.getInt("_status")
                                val subId = joSubjectDetails.getString("_id")
                                if (subStatus == 1) {
                                    if (status == 1) {
                                        subList.add(
                                            PeriodSubjectListModel(
                                                priority,
                                                dayPeriodLinkingId,
                                                subName,
                                                subId
                                            )
                                        )
                                    }
                                }
                            }
                            runOnUiThread {
                                pbLoading1.visibility = View.GONE
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@SelectDetailsToTakeAttendance)
                                rv.adapter = PeriodListToTakeAttendanceDetailsRvAdapter(
                                    this@SelectDetailsToTakeAttendance,
                                    subList,tvSubject,dialog
                                )
                                rv.visibility = View.VISIBLE
                                pbLoading1.visibility = View.GONE
                                pbLoading.visibility = View.GONE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getSemCourseList:$message")
                            runOnUiThread {
                                this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@SelectDetailsToTakeAttendance,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSemCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@SelectDetailsToTakeAttendance,
                                    getString(R.string.error_message_server_error)
                                )
                                this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSemCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.error_message_server_error)
                        )
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                    }
                }
            }
        })
    }


    private fun selectCourse() {
        cList.clear()
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)
        val v = LayoutInflater.from(this)
            .inflate(R.layout.layout_dialog_department, null)
        val clParent = v.findViewById<ConstraintLayout>(R.id.clParent)
        val rv = v.findViewById<RecyclerView>(R.id.rv)
        val tv = v.findViewById<TextView>(R.id.tvSelectedDate)
        val pbLoading = v.findViewById<ProgressBar>(R.id.pbLoading)
        tv.text = "Courses"
        getCourseList(rv, pbLoading, dialog)
        clParent.setOnClickListener {
            dialog.cancel()
        }
        dialog.setContentView(v)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun selectPeriod() {
        cList.clear()
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)
        val v = LayoutInflater.from(this)
            .inflate(R.layout.layout_dialog_department, null)
        val clParent = v.findViewById<ConstraintLayout>(R.id.clParent)
        val rv = v.findViewById<RecyclerView>(R.id.rv)
        val tv = v.findViewById<TextView>(R.id.tvSelectedDate)
        val pbLoading = v.findViewById<ProgressBar>(R.id.pbLoading)
        tv.text = "Periods"
        getSubjects(rv, pbLoading, dialog)
        clParent.setOnClickListener {
            dialog.cancel()
        }
        dialog.setContentView(v)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun selectSem() {
        scList.clear()
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)
        val v = LayoutInflater.from(this)
            .inflate(R.layout.layout_dialog_department, null)
        val clParent = v.findViewById<ConstraintLayout>(R.id.clParent)
        val rv = v.findViewById<RecyclerView>(R.id.rv)
        val tv = v.findViewById<TextView>(R.id.tvSelectedDate)
        val pbLoading = v.findViewById<ProgressBar>(R.id.pbLoading)
        tv.text = "Semesters"
        getSemCourseList(rv, pbLoading, dialog)
        clParent.setOnClickListener {
            dialog.cancel()
        }
        dialog.setContentView(v)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun init() {
        backBtn = findViewById(R.id.ivBackBtn)
        btnSubmit = findViewById(R.id.btnSubmit)
        pbLoading1 = findViewById(R.id.pbLoading)
        tvCourse = findViewById(R.id.tvCourse)
        tvSemester = findViewById(R.id.tvSemester)
        clButton = findViewById(R.id.clButton)
        tvMain = findViewById(R.id.TextView)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvEndDate = findViewById(R.id.tvEndDate)
        tvPeriod = findViewById(R.id.tvPeriod)
        tvSubject = findViewById(R.id.tvSubject)
        tvSubLabel = findViewById(R.id.tvSubLabel)
    }

    private fun getSemCourseList(
        rv: RecyclerView,
        pbLoading: ProgressBar,
        dialog: Dialog
    ) {
        this.pbLoading1.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@SelectDetailsToTakeAttendance).getUser()?.userId
        )
        jsonObject.put("courseId", selectedCourseId)
        jsonObject.put("skip", -1)
        jsonObject.put("status", jaStatus)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getSemCourseList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_COURSE_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getSemCourseList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.no_internet_connection)
                        )
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.something_went_wrong)
                        )
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getSemCourseList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (j in 0 until list.length()) {
                                val joList = list.getJSONObject(j)
                                val jaSemList = joList.getJSONArray("semList")
                                for (k in 0 until jaSemList.length()) {
                                    val sem = jaSemList.getJSONObject(k)
                                    val semName = sem.getString("_name")
                                    semId = joList.getString("_sem_id")
                                    val semStatus = sem.getInt("_status")
                                    val semCourseStatus = joList.getInt("_status")
                                    if (semCourseStatus == 1) {
                                        if (semStatus == 1) {
                                            val semCourseId = joList.getString("_id")
                                            scList.add(
                                                srModel(
                                                    semCourseId,
                                                    semName
                                                )
                                            )

                                            sList.add(
                                                srModel(
                                                    semId,
                                                    semName
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@SelectDetailsToTakeAttendance)
                                rv.adapter = SemListTakeAttendanceRvAdapter(
                                    this@SelectDetailsToTakeAttendance,
                                    scList, tvSemester, dialog, tvSemester, clButton
                                )
                                rv.visibility = View.VISIBLE
                                pbLoading1.visibility = View.GONE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getSemCourseList:$message")
                            runOnUiThread {
                                this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@SelectDetailsToTakeAttendance,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSemCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@SelectDetailsToTakeAttendance,
                                    getString(R.string.error_message_server_error)
                                )
                                this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSemCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.error_message_server_error)
                        )
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                    }
                }
            }
        })
    }


    private fun getCourseList(
        rv: RecyclerView,
        pbLoading: ProgressBar,
        dialog: Dialog
    ) {
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@SelectDetailsToTakeAttendance).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("searching_text", "")
        jsonObject.put("status", jaStatus)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getCourseList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_COURSE_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getCourseList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.no_internet_connection)
                        )
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.something_went_wrong)
                        )
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getCourseList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (i in 0 until list.length()) {
                                val jo = list.getJSONObject(i)
                                val id = jo.getString("_id")
                                val name = jo.getString("_name")
                                val semCount = jo.getInt("_sem_count")
                                val status = jo.getInt("_status")
                                val joDepartment = jo.getJSONObject("departmentDetails")
                                val departmentId1 = joDepartment.getString("_id")
                                val departmentStatus = joDepartment.getInt("_status")
                                if (status == 1) {
                                    if (departmentStatus == 1) {
                                        if (userType == 0) {
                                            cList.add(
                                                srModel(
                                                    id,
                                                    name
                                                )
                                            )
                                        } else if (userType == 1) {
                                            if (departmentId == departmentId1) {
                                                cList.add(
                                                    srModel(
                                                        id,
                                                        name
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@SelectDetailsToTakeAttendance)
                                rv.adapter = CourseListTakeAttendanceRvAdapter(
                                    this@SelectDetailsToTakeAttendance,
                                    cList, tvCourse, dialog, tvSemester
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getCourseList:$message")
                            runOnUiThread {
                                this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@SelectDetailsToTakeAttendance,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@SelectDetailsToTakeAttendance,
                                    getString(R.string.error_message_server_error)
                                )
                                this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.error_message_server_error)
                        )
                        this@SelectDetailsToTakeAttendance.pbLoading1.visibility = View.GONE
                    }
                }
            }
        })

    }


    private fun getDepartmentList() {
        pbLoading1.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@SelectDetailsToTakeAttendance).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("searching_text", "")
        jsonObject.put("status", jaStatus)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "usersList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_DEPARTMENT_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.something_went_wrong)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "usersList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (i in 0 until list.length()) {
                                val jo = list.getJSONObject(i)
                                val id = jo.getString("_id")
                                val name = jo.getString("_name")
                                val status = jo.getInt("_status")

                                val inChargeUserStatus =
                                    jo.getJSONObject("usertDetails").getInt("_status")
                                val inChargeUserId =
                                    jo.getJSONObject("usertDetails").getString("_id")

                                val uid =
                                    DatabaseUtils.getInstance(this@SelectDetailsToTakeAttendance)
                                        .getUser()?.userId

                                if (inChargeUserStatus == 1) {
                                    if (inChargeUserId == uid) {
                                        departmentId = id.toString()
                                    }
                                }

                            }
                            runOnUiThread {
                                pbLoading1.visibility = View.GONE

                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@SelectDetailsToTakeAttendance,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@SelectDetailsToTakeAttendance,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "usersList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SelectDetailsToTakeAttendance,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                }
            }
        })

    }

}