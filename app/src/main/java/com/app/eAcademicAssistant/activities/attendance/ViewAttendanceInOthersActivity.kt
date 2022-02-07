package com.app.eAcademicAssistant.activities.attendance

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.model.AttendanceMenuModel
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
import java.util.*

class ViewAttendanceInOthersActivity : AppCompatActivity() {

    var studentId = ""
    var selectedSemCourseId = ""
    private var getUsersList: Call? = null
    private val currentDate = Calendar.getInstance().timeInMillis
    var dayPeriodLinkingId = ArrayList<String>()
    private var attendanceList = ArrayList<AttendanceMenuModel>()
    private lateinit var pbLoading1: ProgressBar
    private lateinit var backBtn: ImageView
    private lateinit var tvStudentName: TextView
    private lateinit var tvPresent: TextView
    private lateinit var tvAbsent: TextView
    private lateinit var tvTotal: TextView
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_attendance_in_others)

        init()

        studentId = intent.getStringExtra("key_student_id").toString()
        val studentName = intent.getStringExtra("key_student_name").toString()
        selectedSemCourseId = intent.getStringExtra("selectedSemCourseId").toString()


        tvStudentName.text = studentName

        backBtn.setOnClickListener {
            finish()
        }

        getDayPeriodLinkings()

    }

    private fun init() {
        backBtn = findViewById(R.id.ivBackBtn)
        pbLoading1 = findViewById(R.id.pbLoading)
        tvStudentName = findViewById(R.id.tvStudentName)
        pieChart = findViewById(R.id.pieChart)
        tvAbsent = findViewById(R.id.tvAbsent)
        tvPresent = findViewById(R.id.tvPresent)
        tvTotal= findViewById(R.id.tvTotal)
    }

    private fun getDayPeriodLinkings() {
        this.pbLoading1.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val arraySemCourseLinkingIdS = JSONArray()
        arraySemCourseLinkingIdS.put(selectedSemCourseId)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@ViewAttendanceInOthersActivity).getUser()?.userId
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
                            this@ViewAttendanceInOthersActivity,
                            getString(R.string.no_internet_connection)
                        )
                        this@ViewAttendanceInOthersActivity.pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceInOthersActivity,
                            getString(R.string.something_went_wrong)
                        )
                        this@ViewAttendanceInOthersActivity.pbLoading1.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@ViewAttendanceInOthersActivity.pbLoading1.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceInOthersActivity,
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
                                this@ViewAttendanceInOthersActivity.pbLoading1.visibility =
                                    View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceInOthersActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSemCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceInOthersActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                this@ViewAttendanceInOthersActivity.pbLoading1.visibility =
                                    View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSemCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceInOthersActivity,
                            getString(R.string.error_message_server_error)
                        )
                        this@ViewAttendanceInOthersActivity.pbLoading1.visibility = View.GONE
                    }
                }
            }
        })
    }

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
        tvTotal.text = attendanceList.size.toString()
        tvPresent.text = pPersent.toString()
        tvAbsent.text = aPersent.toString()
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
            DatabaseUtils.getInstance(this@ViewAttendanceInOthersActivity).getUser()?.userId
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
                            this@ViewAttendanceInOthersActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewAttendanceInOthersActivity,
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
                            this@ViewAttendanceInOthersActivity,
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
                                val studentId1 = jo.getString("_student_id")
                                val attendanceStatus = jo.getInt("_attendance_status")
                                val date = jo.getLong("_created_date")
                                val status = jo.getInt("_status")

                                if (status == 1) {

                                    if (studentId1 == studentId) {
                                        attendanceList.add(
                                            AttendanceMenuModel(
                                                studentId1,
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
                                calculateAttendance()

                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getAttendanceList:$message")
                            runOnUiThread {
                                pbLoading1.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceInOthersActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getAttendanceList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ViewAttendanceInOthersActivity,
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
                            this@ViewAttendanceInOthersActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading1.visibility = View.GONE
                    }
                }
            }
        })

    }
}