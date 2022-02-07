package com.app.eAcademicAssistant.activities.attendance

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.CourseListViewAttendanceRvAdapter
import com.app.eAcademicAssistant.adapters.SemListViewAttendanceRvAdapter
import com.app.eAcademicAssistant.adapters.StudentListRvAdapter
import com.app.eAcademicAssistant.adapters.StudentListViewInOthersRvAdapter
import com.app.eAcademicAssistant.model.AttendanceMenuModel
import com.app.eAcademicAssistant.model.UserModel1
import com.app.eAcademicAssistant.model.srModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ViewAttendanceActivity : AppCompatActivity() {

    private lateinit var pbLoading1: ProgressBar
    private lateinit var backBtn: ImageView
    private lateinit var tvCourse: TextView
    private lateinit var rv: RecyclerView
    private lateinit var tvSemester: TextView
    private lateinit var rlPieChart: RelativeLayout
    private var getUsersList: Call? = null
    private var cList = ArrayList<srModel>()
    var dayPeriodLinkingId = ArrayList<String>()
    private var scList = ArrayList<UserModel1>()
    private var studentsList = ArrayList<srModel>()
    private var studentAdapterList = ArrayList<srModel>()
    private var attendanceList = ArrayList<AttendanceMenuModel>()
    private var sortedStudentAttendanceList = ArrayList<AttendanceMenuModel>()
    var selectedCourseId = ""
    var selectedSemId = ""
    var selectedSemCourseId = ""
    private lateinit var pieChart: PieChart
    var departmentId = ""
    var semId = ""
    val userTypeMain = DatabaseUtils.getInstance(this).getUser()?.userType
    val userIdMain = DatabaseUtils.getInstance(this).getUser()?.userId
    private var setStartTime: Long = 0
    private var setEndTime: Long = 0
    private val currentDate = Calendar.getInstance().timeInMillis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_view)

        init()

        backBtn.setOnClickListener {
            finish()
        }

        tvCourse.setOnClickListener {
            selectCourse()
        }

        tvSemester.setOnClickListener {
            selectSem()
        }

    }

//    private fun setStudentDetails(){
//        sortedStudentAttendanceList.clear()
//        for (i in 0 until attendanceList.size){
//            for (j in 0 until studentsList.size){
//                if (attendanceList[i].StudentId == studentsList[j].id){
//                    sortedStudentAttendanceList.add(
//                        AttendanceMenuModel(
//                            attendanceList[i].StudentId,
//                            studentsList[j].name,
//                            attendanceList[i].AttendanceStatus,
//                            attendanceList[i].Date
//                        )
//                    )
//                }
//            }
//        }
//
//        for (i in 0 until sortedStudentAttendanceList.size){
//            for (j in 0 until studentAdapterList.size){
//                if (sortedStudentAttendanceList[i].StudentId != studentAdapterList[j].id){
//                    studentAdapterList.add(
//                        srModel(
//                            sortedStudentAttendanceList[i].StudentId,
//                            sortedStudentAttendanceList[i].DayPeriodLinkId
//                        )
//                    )
//                }
//            }
//        }
//
//        Log.e("___________","__SMenu: ${studentAdapterList.size}")
//        Log.e("___________","__SMenuStudents: ${studentsList.size}")
//        Log.e("___________","__SMenuAttendance: ${sortedStudentAttendanceList.size}")
//        Log.e("___________","__SMenuAttendance2: ${attendanceList.size}")
//
//    }

    private fun calculateAttendance() {
        var p = 0
        var a = 0
        for (i in 0 until attendanceList.size) {
            if (attendanceList[i].AttendanceStatus == 1) {
                p += 1
            } else if (attendanceList[i].AttendanceStatus == 2) {
                a += 1
            }
        }

        val pPersent = p
        val aPersent = a


        pieChartSetData(pPersent, aPersent)
    }

    private fun pieChartSetData(pPersent: Int, aPersent: Int) {

        rlPieChart.visibility = View.VISIBLE

        pieChart.addPieSlice(
            PieModel(
                "Present", pPersent.toFloat(),
                Color.parseColor("#3E8541")
            )
        )

        pieChart.addPieSlice(
            PieModel(
                "Absent", aPersent.toFloat(),
                Color.parseColor("#C11818")
            )
        )

        pieChart.startAnimation()
    }

    private fun init() {
        backBtn = findViewById(R.id.ivBackBtn)
        pbLoading1 = findViewById(R.id.pbLoading)
        tvCourse = findViewById(R.id.tvCourse)
        tvSemester = findViewById(R.id.tvSemester)
        pieChart = findViewById(R.id.pieChart)
        rlPieChart = findViewById(R.id.rlPieChart)
        rv = findViewById(R.id.rv)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        val formatter = SimpleDateFormat(dateFormat)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private fun getAttendanceList() {
        pbLoading1.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaDayPeriodLinkingId = JSONArray()
        for (i in 0 until dayPeriodLinkingId.size) {
            jaDayPeriodLinkingId.put(dayPeriodLinkingId[i])
        }
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@ViewAttendanceActivity).getUser()?.userId
        )
        jsonObject.put("arrayDayPeriodLinkingId", jaDayPeriodLinkingId)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getAttendanceList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_STUDENT_ATTENDANCE_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getAttendanceList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
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
                            this@ViewAttendanceActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getAttendanceList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            attendanceList.clear()
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (i in 0 until list.length()) {
                                val jo = list.getJSONObject(i)
                                val id = jo.getString("_id")
                                val dayPeriodLinkId1 = jo.getString("_day_period_linking_id")
                                val studentId = jo.getString("_student_id")
                                val attendanceStatus = jo.getInt("_attendance_status")
                                val date = jo.getLong("_created_date")
                                val status = jo.getInt("_status")

                                if (status == 1) {
                                    if (userTypeMain == 3) {
                                        if (studentId == userIdMain) {
                                            attendanceList.add(
                                                AttendanceMenuModel(
                                                    studentId,
                                                    dayPeriodLinkId1,
                                                    attendanceStatus,
                                                    date
                                                )
                                            )
                                        }
                                    } else {
                                        attendanceList.add(
                                            AttendanceMenuModel(
                                                studentId,
                                                dayPeriodLinkId1,
                                                attendanceStatus,
                                                date
                                            )
                                        )
                                    }
                                }
                            }
                            runOnUiThread {
                                pbLoading1.visibility = View.GONE

                                if (userTypeMain == 3)
                                    calculateAttendance()
//                                else
//                                    setStudentDetails()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getAttendanceList:$message")
                            runOnUiThread {
                                pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getAttendanceList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getAttendanceList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                }
            }
        })

    }


    private fun getDayPeriodLinkings() {
        this.pbLoading1.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val arraySemCourseLinkingIdS = JSONArray()
        arraySemCourseLinkingIdS.put(selectedSemCourseId)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@ViewAttendanceActivity).getUser()?.userId
        )
        jsonObject.put("startTime", currentDate - 86400000)
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
                            this@ViewAttendanceActivity,
                            getString(R.string.no_internet_connection)
                        )
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
                            getString(R.string.something_went_wrong)
                        )
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
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
                            dayPeriodLinkingId.clear()
//                            subList.clear()
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (j in 0 until list.length()) {
                                val joList = list.getJSONObject(j)
                                val dayPeriodLinkId = joList.getString("_id")
                                val priority = joList.getInt("_priority")
                                val status = joList.getInt("_status")
                                val joSubjectDetails = joList.getJSONObject("subjectDetails")
                                val subName = joSubjectDetails.getString("_name")
                                val subStatus = joSubjectDetails.getInt("_status")
                                val subId = joSubjectDetails.getString("_id")
                                if (subStatus == 1) {
                                    if (status == 1) {
                                        dayPeriodLinkingId.add(dayPeriodLinkId)
//                                        subList.add(
//                                            PeriodSubjectListModel(
//                                                priority,
//                                                dayPeriodLinkingId,
//                                                subName,
//                                                subId
//                                            )
//                                        )
                                    }
                                }
                            }
                            runOnUiThread {
                                pbLoading1.visibility = View.GONE
                                getAttendanceList()
//                                rv.layoutManager =
//                                    LinearLayoutManager(this@SelectDetailsToTakeAttendance)
//                                rv.adapter = PeriodListToTakeAttendanceDetailsRvAdapter(
//                                    this@SelectDetailsToTakeAttendance,
//                                    subList,tvSubject,dialog
//                                )
//                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getSemCourseList:$message")
                            runOnUiThread {
                                this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSemCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSemCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
                            getString(R.string.error_message_server_error)
                        )
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
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

    fun getStudentsList() {
        pbLoading1.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            ""
        )
        jsonObject.put("semId", selectedSemId)
        jsonObject.put("courseId", selectedCourseId)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getStudentList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_GET_STUDENT_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
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
                            this@ViewAttendanceActivity,
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
                            studentsList.clear()
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (j in 0 until list.length()) {
                                val joList = list.getJSONObject(j)
                                val joStudentList = joList.getJSONObject("userDetails")
                                val id = joStudentList.getString("_id")
                                val name = joStudentList.getString("_name")
                                val mail = joStudentList.getString("_email")
                                val gender = joStudentList.getInt("_gender")
                                val userType = joStudentList.getInt("_user_type")
                                val status = joStudentList.getInt("_status")
                                val uid = joStudentList.getString("_uid")

                                if (status == 1) {
                                    if (userTypeMain == 3) {
                                        if (id == userIdMain) {
                                            getDayPeriodLinkings()
                                        }
                                    } else {
                                        studentsList.add(
                                            srModel(
                                                id,
                                                name
                                            )
                                        )
                                    }
                                }

                            }

                            runOnUiThread {
                                pbLoading1.visibility = View.GONE
                                if (userTypeMain != 3){
                                    rv.visibility =View.VISIBLE
                                    rv.layoutManager =
                                        LinearLayoutManager(this@ViewAttendanceActivity)
                                    rv.adapter = StudentListViewInOthersRvAdapter(
                                        this@ViewAttendanceActivity,
                                        studentsList
                                    )
                                }
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
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
                            this@ViewAttendanceActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading1.visibility = View.GONE
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
            DatabaseUtils.getInstance(this@ViewAttendanceActivity).getUser()?.userId
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
                            this@ViewAttendanceActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
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
                            this@ViewAttendanceActivity,
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
                                    DatabaseUtils.getInstance(this@ViewAttendanceActivity)
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
                                    this@ViewAttendanceActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
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
                            this@ViewAttendanceActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                }
            }
        })

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
            DatabaseUtils.getInstance(this@ViewAttendanceActivity).getUser()?.userId
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
                            this@ViewAttendanceActivity,
                            getString(R.string.no_internet_connection)
                        )
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
                            getString(R.string.something_went_wrong)
                        )
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
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
                                    semId = sem.getString("_id")
                                    val semStatus = sem.getInt("_status")
                                    val semCourseStatus = joList.getInt("_status")
                                    if (semCourseStatus == 1) {
                                        if (semStatus == 1) {
                                            val semCourseId = joList.getString("_id")
                                            scList.add(
                                                UserModel1(
                                                    semId,
                                                    semName,
                                                    semCourseId
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@ViewAttendanceActivity)
                                rv.adapter = SemListViewAttendanceRvAdapter(
                                    this@ViewAttendanceActivity,
                                    scList, tvSemester, dialog, tvSemester
                                )
                                rv.visibility = View.VISIBLE
                                pbLoading1.visibility = View.GONE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getSemCourseList:$message")
                            runOnUiThread {
                                this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSemCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSemCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
                            getString(R.string.error_message_server_error)
                        )
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
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
            DatabaseUtils.getInstance(this@ViewAttendanceActivity).getUser()?.userId
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
                            this@ViewAttendanceActivity,
                            getString(R.string.no_internet_connection)
                        )
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
                            getString(R.string.something_went_wrong)
                        )
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
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
                                        cList.add(
                                            srModel(
                                                id,
                                                name
                                            )
                                        )
//                                        if (userTypeMain == 0) {
//                                            cList.add(
//                                                srModel(
//                                                    id,
//                                                    name
//                                                )
//                                            )
//                                        } else if (userTypeMain == 1) {
//                                            if (departmentId == departmentId1) {
//                                                cList.add(
//                                                    srModel(
//                                                        id,
//                                                        name
//                                                    )
//                                                )
//                                            }
//                                        }
                                    }
                                }
                            }

                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@ViewAttendanceActivity)
                                rv.adapter = CourseListViewAttendanceRvAdapter(
                                    this@ViewAttendanceActivity,
                                    cList, tvCourse, dialog, tvSemester
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getCourseList:$message")
                            runOnUiThread {
                                this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceActivity,
                            getString(R.string.error_message_server_error)
                        )
                        this@ViewAttendanceActivity.pbLoading1.visibility = View.GONE
                    }
                }
            }
        })

    }


}