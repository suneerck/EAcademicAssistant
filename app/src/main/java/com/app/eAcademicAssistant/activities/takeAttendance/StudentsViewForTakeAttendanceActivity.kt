package com.app.eAcademicAssistant.activities.takeAttendance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.StudentAttendanceAddRvAdapter
import com.app.eAcademicAssistant.model.StudentAttendanceModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class StudentsViewForTakeAttendanceActivity : AppCompatActivity() {

    private var dayPeriodLinkId = ""
    private var getUsersList: Call? = null
    private var selectedCourseId = ""
    private var selectedSemId = ""
    private lateinit var backBtn : ImageView
    private lateinit var ivSubmit : ImageView
    private lateinit var rv : RecyclerView
    private lateinit var pbLoading : ProgressBar
    val attendanceList = ArrayList<StudentAttendanceModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_view_for_take_attendance)

        init()

        dayPeriodLinkId = intent.getStringExtra("day_period_linking_id").toString()
        selectedCourseId = intent.getStringExtra("course_id").toString()
        selectedSemId = intent.getStringExtra("sem_id").toString()

        getStudentList()

        backBtn.setOnClickListener {
            finish()
        }

        ivSubmit.setOnClickListener {
            addAttendance()
        }

    }

    private fun init() {
        pbLoading = findViewById(R.id.pbLoading)
        backBtn = findViewById(R.id.backBtn)
        rv = findViewById(R.id.rv)
        ivSubmit = findViewById(R.id.ivSubmit)
    }

    private fun addAttendance() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStudentIds = JSONArray()

        for (i in 0 until attendanceList.size){
            val joAttendance = JSONObject()
            joAttendance.put(
                "studentId",
                attendanceList[i].id
            )
            joAttendance.put(
                "attendanceStatus",
                attendanceList[i].attendanceStatus
            )
            jaStudentIds.put(
                joAttendance
            )
        }

        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@StudentsViewForTakeAttendanceActivity).getUser()?.userId
        )
        jsonObject.put("studentIds", jaStudentIds)
        jsonObject.put("dayPeriodLinkingId", dayPeriodLinkId)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getStudentList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_STUDENT_ATTENDANCE_ADD).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsViewForTakeAttendanceActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsViewForTakeAttendanceActivity,
                            getString(R.string.something_went_wrong)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        pbLoading.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@StudentsViewForTakeAttendanceActivity,
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

                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                finish()
                            }

                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentsViewForTakeAttendanceActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentsViewForTakeAttendanceActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "usersList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsViewForTakeAttendanceActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }


    private fun getStudentList() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@StudentsViewForTakeAttendanceActivity).getUser()?.userId
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
                            this@StudentsViewForTakeAttendanceActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsViewForTakeAttendanceActivity,
                            getString(R.string.something_went_wrong)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        pbLoading.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@StudentsViewForTakeAttendanceActivity,
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
                            attendanceList.clear()
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
                                val password = joStudentList.getString("_password")
                                val uid = joStudentList.getString("_uid")

                                attendanceList.add(
                                    StudentAttendanceModel(
                                        id,
                                        name,
                                        1
                                    )
                                )

                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE

                                rv.layoutManager =
                                    LinearLayoutManager(this@StudentsViewForTakeAttendanceActivity)
                                rv.adapter = StudentAttendanceAddRvAdapter(
                                    this@StudentsViewForTakeAttendanceActivity,
                                    attendanceList
                                )
                            }

                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentsViewForTakeAttendanceActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentsViewForTakeAttendanceActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "usersList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsViewForTakeAttendanceActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }

}