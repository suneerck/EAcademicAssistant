package com.app.eAcademicAssistant.activities.teacherManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.SemSrAdapter
import com.app.eAcademicAssistant.adapters.SubjectTeacherLinkRvAdapter
import com.app.eAcademicAssistant.adapters.srAdapter
import com.app.eAcademicAssistant.model.SemCourseMenuModel
import com.app.eAcademicAssistant.model.SubjectMenuModel
import com.app.eAcademicAssistant.model.srModel
import com.app.eAcademicAssistant.objects.MenuConstants
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class TeacherSubjectLinkActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var sCourse: AppCompatSpinner
    private lateinit var sSem: AppCompatSpinner
    private lateinit var pbLoading: ProgressBar
    private val cList = ArrayList<srModel>()
    private val sList = ArrayList<SemCourseMenuModel>()
    private val subjectList = ArrayList<SubjectMenuModel>()
    private var getUsersList: Call? = null
    private var selectedCourseId = MenuConstants.NIL
    private var selectedSemId = MenuConstants.NIL
    private var selectedSemCourseId = MenuConstants.NIL
    lateinit var semCourseLinkingId: String
    lateinit var semSubjectId: String
    lateinit var keyTeacherId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_subject_link)

        init()
        getCourseList()

        keyTeacherId = intent.getStringExtra("key_teacher_id").toString()

        sCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedCourseId = cList[position].id
                if (selectedCourseId != MenuConstants.NIL) {
                    sList.clear()
                    getSemList()
                }
            }
        }

        sSem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSemId = sList[position].semId
                if (selectedSemId != MenuConstants.NIL) {
                    selectedSemCourseId = sList[position].semCourseId
                    subjectList.clear()
                    getSubjectList()
                }
            }
        }
    }

    private fun init() {
        rv = findViewById(R.id.rv)
        pbLoading = findViewById(R.id.pbLoading)
        backButton = findViewById(R.id.ivBackBtn)
        sCourse = findViewById(R.id.sCourse)
        sSem = findViewById(R.id.sSem)
    }

    fun linkSubjectTeacher() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStudents = JSONArray()
        jaStudents.put(keyTeacherId)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@TeacherSubjectLinkActivity).getUser()?.userId
        )
        jsonObject.put("userIds", jaStudents)
        jsonObject.put("semSubjectLinkingId", semSubjectId)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "linkSubjectTeacher : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_TEACHER_DUTY_LINKING).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "linkSubjectTeacher")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
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
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "linkSubjectTeacher :$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
//                                SnackBarUtils.showSnackBar(
//                                    this@TeacherSubjectLinkActivity,
//                                    "Linked Successfully"
//                                )
                                Toast.makeText(
                                    this@TeacherSubjectLinkActivity,
                                    "Linked Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "linkSubjectTeacher :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@TeacherSubjectLinkActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "linkSubjectTeacher")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TeacherSubjectLinkActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "linkSubjectTeacher " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }


    private fun getSubjectList() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@TeacherSubjectLinkActivity).getUser()?.userId
        )
        jsonObject.put("courseId", selectedCourseId)
        jsonObject.put("semId", selectedSemId)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getStudentList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_SUBJECT_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getStudentList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
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
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getStudentList :$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (j in 0 until list.length()) {
                                val joList = list.getJSONObject(j)
                                semSubjectId = joList.getString("_id")
                                val joSubjectDetails = joList.getJSONObject("subjectDetails")
                                val subjectId = joSubjectDetails.getString("_id")
                                val name = joSubjectDetails.getString("_name")
                                val status = joSubjectDetails.getInt("_status")

                                subjectList.add(
                                    SubjectMenuModel(
                                        subjectId,
                                        name,
                                        status
                                    )
                                )

                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@TeacherSubjectLinkActivity)
                                rv.adapter = SubjectTeacherLinkRvAdapter(
                                    this@TeacherSubjectLinkActivity,
                                    subjectList
                                )
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getStudentList :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@TeacherSubjectLinkActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getStudentList ")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TeacherSubjectLinkActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getStudentList " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }


    private fun getCourseList() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@TeacherSubjectLinkActivity).getUser()?.userId
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
            .url(URLUtils.URL_COURSE_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getCourseList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
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
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getCourseList :$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            cList.add(
                                srModel(
                                    MenuConstants.NIL, "Please select a course"
                                )
                            )
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (i in 0 until list.length()) {
                                val jo = list.getJSONObject(i)
                                val id = jo.getString("_id")
                                val name = jo.getString("_name")
                                val semCount = jo.getInt("_sem_count")
                                val status = jo.getInt("_status")
                                val departmentDetails = jo.getJSONObject("departmentDetails")
                                val departmentStatus = departmentDetails.getInt("_status")

                                if (departmentStatus == 1) {

                                    if (status == 1) {
                                        cList.add(
                                            srModel(
                                                id,
                                                name
                                            )
                                        )
                                    }
                                }
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                sCourse.adapter = srAdapter(this@TeacherSubjectLinkActivity, cList)
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getCourseList :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@TeacherSubjectLinkActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TeacherSubjectLinkActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getCourseList " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }

    private fun getSemList() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@TeacherSubjectLinkActivity).getUser()?.userId
        )
        jsonObject.put("courseId", selectedCourseId)
        jsonObject.put("skip", -1)
        jsonObject.put("status", jaStatus)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getSemList : $jsonObject")

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
                Log.e("___________onFailure", "getSemList ")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
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
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getSemList :$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            sList.add(
                                SemCourseMenuModel(
                                    MenuConstants.NIL,
                                    MenuConstants.NIL,
                                    MenuConstants.NIL,
                                    "(Please select a semester)",
                                    "",
                                    1
                                )
                            )
                            val joData = jo.getJSONObject("data")
                            val jaList = joData.getJSONArray("list")
                            for (i in 0 until jaList.length()) {
                                val jo = jaList.getJSONObject(i)
                                val semCourseStatus = jo.getInt("_status")
                                if (semCourseStatus == 1) {
                                    semCourseLinkingId = jo.getString("_id")
                                    val jaSemList = jo.getJSONArray("semList")
                                    for (j in 0 until jaSemList.length()) {
                                        val joSemList = jaSemList.getJSONObject(j)
                                        val id = joSemList.getString("_id")
                                        val name = joSemList.getString("_name")
                                        val status = joSemList.getInt("_status")

                                        if (status == 1) {
                                            sList.add(
                                                SemCourseMenuModel(
                                                    semCourseLinkingId,
                                                    id,
                                                    "",
                                                    name,
                                                    "",
                                                    status
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                sSem.adapter = SemSrAdapter(this@TeacherSubjectLinkActivity, sList)
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("getSemList")
                            Log.e("_______resp_error", "getSemList :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@TeacherSubjectLinkActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSemList ")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TeacherSubjectLinkActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", " getSemList " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeacherSubjectLinkActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }


}