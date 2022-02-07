package com.app.eAcademicAssistant.activities.timeTableManagement

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.dayManagement.calendar.list.CalendarListActivity
import com.app.eAcademicAssistant.activities.hodsManagement.EditHodActivity
import com.app.eAcademicAssistant.adapters.CourseListSemDayLinkRvAdapter
import com.app.eAcademicAssistant.adapters.CourseListTimeTableRvAdapter
import com.app.eAcademicAssistant.adapters.SemListSemDayRvAdapter
import com.app.eAcademicAssistant.adapters.SemListTimeTableRvAdapter
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
import java.util.*
import kotlin.collections.ArrayList

class TimeTableManagementActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var clButton: ConstraintLayout
    private lateinit var tvCourse: TextView
    private lateinit var tvMain: TextView
    private lateinit var tvSemester: TextView
    private lateinit var btnSubmit: Button
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
    val userType = DatabaseUtils.getInstance(this).getUser()?.userType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_management)

        init()

        tvMain.text = "Time Table Management"

        if (userType == 1) {
            getDepartmentList()
        }

        backBtn.setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {

            val intent = Intent(this, TimeTableCalendarListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("sem_course_id", selectedSemId)
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
            DatabaseUtils.getInstance(this@TimeTableManagementActivity).getUser()?.userId
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
                            this@TimeTableManagementActivity,
                            getString(R.string.no_internet_connection)
                        )
                        this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableManagementActivity,
                            getString(R.string.something_went_wrong)
                        )
                        this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@TimeTableManagementActivity,
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
                            scList.clear()
                            sList.clear()
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
                                    LinearLayoutManager(this@TimeTableManagementActivity)
                                rv.adapter = SemListTimeTableRvAdapter(
                                    this@TimeTableManagementActivity,
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
                                this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@TimeTableManagementActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSemCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TimeTableManagementActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSemCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableManagementActivity,
                            getString(R.string.error_message_server_error)
                        )
                        this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
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
            DatabaseUtils.getInstance(this@TimeTableManagementActivity).getUser()?.userId
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
                            this@TimeTableManagementActivity,
                            getString(R.string.no_internet_connection)
                        )
                        this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableManagementActivity,
                            getString(R.string.something_went_wrong)
                        )
                        this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@TimeTableManagementActivity,
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
                                    LinearLayoutManager(this@TimeTableManagementActivity)
                                rv.adapter = CourseListTimeTableRvAdapter(
                                    this@TimeTableManagementActivity,
                                    cList, tvCourse, dialog, tvSemester
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getCourseList:$message")
                            runOnUiThread {
                                this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@TimeTableManagementActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TimeTableManagementActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableManagementActivity,
                            getString(R.string.error_message_server_error)
                        )
                        this@TimeTableManagementActivity.pbLoading1.visibility = View.GONE
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
            DatabaseUtils.getInstance(this@TimeTableManagementActivity).getUser()?.userId
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
                            this@TimeTableManagementActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableManagementActivity,
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
                            this@TimeTableManagementActivity,
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

                                val uid = DatabaseUtils.getInstance(this@TimeTableManagementActivity)
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
                                SnackBarUtils.showSnackBar(this@TimeTableManagementActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TimeTableManagementActivity,
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
                            this@TimeTableManagementActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                }
            }
        })

    }

}