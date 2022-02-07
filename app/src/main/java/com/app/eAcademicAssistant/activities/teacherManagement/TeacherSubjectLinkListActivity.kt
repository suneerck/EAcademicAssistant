package com.app.eAcademicAssistant.activities.teacherManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.SubjectTeacherListRvAdapter
import com.app.eAcademicAssistant.model.TeachersSubjectListModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class TeacherSubjectLinkListActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var rv: RecyclerView
    private lateinit var tvTeacherName: TextView
    private lateinit var pbLoading: ProgressBar
    lateinit var teacherId: String
    private var getUsersList: Call? = null
    private var teachersSubjectList = ArrayList<TeachersSubjectListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_add_subject_to_teacher)

        init()

        backButton.setOnClickListener {
            finish()
        }

        fabAdd.setOnClickListener {
            val intent = Intent(this, TeacherSubjectLinkActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("key_teacher_id", teacherId)
            startActivity(intent)
        }

        val keyId = intent.getStringExtra("key_id")
        val keyName = intent.getStringExtra("key_name")
        val keyEmail = intent.getStringExtra("key_email")
        val keyPassword = intent.getStringExtra("key_password")
        val keyUserType = intent.getStringExtra("key_user_type")
        val keyGender = intent.getStringExtra("key_gender")
        val keyStatus = intent.getStringExtra("key_status")

        teacherId = keyId.toString()

        tvTeacherName.text = keyName.toString()
    }

    private fun init() {
        backButton = findViewById(R.id.backBtn)
        rv = findViewById(R.id.rv)
        fabAdd = findViewById(R.id.fabAdd)
        pbLoading = findViewById(R.id.pbLoading)
        tvTeacherName = findViewById(R.id.tvTeacherName)
    }

    override fun onResume() {
        super.onResume()
        teachersSubjectList.clear()
        getSubjectList()
    }

    private fun getSubjectList() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@TeacherSubjectLinkListActivity).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("status", jaStatus)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getSubjectList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_TEACHERS_DUTY_LINKING_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getSubjectList ")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkListActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkListActivity,
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
                            this@TeacherSubjectLinkListActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getSubjectList :$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (i in 0 until list.length()) {
                                val joList = list.getJSONObject(i)
                                val joUserDetails = joList.getJSONObject("userDetails")

                                val userType = joUserDetails.getInt("_user_type")
                                val userId = joUserDetails.getString("_id")
                                if (userType == 2) {

                                    if (userId.equals(teacherId)) {

                                        val joSemSubjectLinkingDetails =
                                            joList.getJSONObject("semSubjectLinkingDetails")

                                        if (joSemSubjectLinkingDetails.isNull(null)) {
                                            break
                                        }else{
                                            val joSubjectDetails =
                                                joSemSubjectLinkingDetails.getJSONObject("subjectDetails")
                                            val subName = joSubjectDetails.getString("_name")
                                            val subId = joSubjectDetails.getString("_id")
                                            val status = joSubjectDetails.getInt("_status")
                                            val joSemCourseDetails =
                                                joSemSubjectLinkingDetails.getJSONObject("semCourseLinkingDetails")
                                            val semDetails =
                                                joSemCourseDetails.getJSONObject("semDetails")
                                            val courseDetails =
                                                joSemCourseDetails.getJSONObject("courseDetails")
                                            val semName = semDetails.getString("_name")
                                            val courseName = courseDetails.getString("_name")

                                            Log.e("__________", "subject : $subName")
                                            Log.e("__________", "uid : $userId")
                                            Log.e("__________", "userType : $userType")

                                            teachersSubjectList.add(
                                                TeachersSubjectListModel(
                                                    subName,
                                                    courseName,
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
                                    LinearLayoutManager(this@TeacherSubjectLinkListActivity)
                                rv.adapter = SubjectTeacherListRvAdapter(
                                    this@TeacherSubjectLinkListActivity,
                                    teachersSubjectList
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getSubjectList :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@TeacherSubjectLinkListActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSubjectList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TeacherSubjectLinkListActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSubjectList " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkListActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }

}