package com.app.eAcademicAssistant.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.StudentListRvAdapter
import com.app.eAcademicAssistant.adapters.srAdapter
import com.app.eAcademicAssistant.model.TeachersMenuModel
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

class StudentsListActivity : AppCompatActivity() {

    private lateinit var rvStudentsList: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var sCourse: AppCompatSpinner
    private lateinit var sSem: AppCompatSpinner
    private lateinit var pbLoading: ProgressBar
    private val studentsList = ArrayList<TeachersMenuModel>()
    private val cList = ArrayList<srModel>()
    private val sList = ArrayList<srModel>()
    private var getUsersList: Call? = null
    private var selectedCourseId = MenuConstants.NIL
    private var selectedSemId = MenuConstants.NIL
    private var departmentId = ""
    private val userTypeMain =  DatabaseUtils.getInstance(this@StudentsListActivity).getUser()?.userType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_students_list)

        init()
        if (userTypeMain == 1){
            getDepartmentList()
        }

        getCourseList()


        backButton.setOnClickListener {
            finish()
        }

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
                selectedSemId = sList[position].id
                if (selectedSemId != MenuConstants.NIL) {
                    studentsList.clear()
                    getStudentList()
                }
            }
        }


    }

    private fun init() {
        rvStudentsList = findViewById(R.id.rvStudents)
        pbLoading = findViewById(R.id.pbLoading)
        backButton = findViewById(R.id.backBtn)
        sCourse = findViewById(R.id.sCourse)
        sSem = findViewById(R.id.sSem)
    }

    private fun getDepartmentList() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@StudentsListActivity).getUser()?.userId
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
                            this@StudentsListActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsListActivity,
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
                            this@StudentsListActivity,
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

                                val uid = DatabaseUtils.getInstance(this@StudentsListActivity)
                                    .getUser()?.userId

                                if (inChargeUserStatus == 1) {
                                    if (inChargeUserId == uid) {
                                        departmentId = id.toString()
                                    }
                                }

                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE

                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentsListActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentsListActivity,
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
                            this@StudentsListActivity,
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
            DatabaseUtils.getInstance(this@StudentsListActivity).getUser()?.userId
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
                            this@StudentsListActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsListActivity,
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
                            this@StudentsListActivity,
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

                                studentsList.add(
                                    TeachersMenuModel(
                                        name,
                                        mail
                                    )
                                )

                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE

                                rvStudentsList.layoutManager =
                                    LinearLayoutManager(this@StudentsListActivity)
                                rvStudentsList.adapter = StudentListRvAdapter(
                                    this@StudentsListActivity,
                                    studentsList
                                )
                            }

                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentsListActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentsListActivity,
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
                            this@StudentsListActivity,
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
            DatabaseUtils.getInstance(this@StudentsListActivity).getUser()?.userId
        )
        jsonObject.put("courseId", selectedCourseId)
        jsonObject.put("skip", -1)
        jsonObject.put("status", jaStatus)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "semCourseList : $jsonObject")

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
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsListActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsListActivity,
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
                            this@StudentsListActivity,
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
                            sList.add(
                                srModel(
                                    MenuConstants.NIL,
                                    "(Select a semester)"
                                )
                            )
                            val joData = jo.getJSONObject("data")
                            val jaList = joData.getJSONArray("list")
                            for (i in 0 until jaList.length()) {
                                val jo = jaList.getJSONObject(i)
                                val semCourseStatus = jo.getInt("_status")
                                if (semCourseStatus == 1) {
                                    val jaSemList = jo.getJSONArray("semList")
                                    for (j in 0 until jaSemList.length()) {
                                        val joSemList = jaSemList.getJSONObject(j)
                                        val id = joSemList.getString("_id")
                                        val name = joSemList.getString("_name")
                                        val status = joSemList.getInt("_status")
                                        if (status == 1) {
                                            sList.add(
                                                srModel(
                                                    id,
                                                    name
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                sSem.adapter = srAdapter(this@StudentsListActivity, sList)
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentsListActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentsListActivity,
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
                            this@StudentsListActivity,
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
            DatabaseUtils.getInstance(this@StudentsListActivity).getUser()?.userId
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
                            this@StudentsListActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentsListActivity,
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
                            this@StudentsListActivity,
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
                                val departmentId1 = departmentDetails.getString("_id")
                                val departmentStatus = departmentDetails.getInt("_status")
                                var joId = "nil"
                                var joName = "nil"
                                if (departmentStatus == 1) {
                                    if (userTypeMain == 1) {
                                        if (departmentId == departmentId1) {
                                            cList.add(
                                                srModel(
                                                    id,
                                                    name
                                                )
                                            )
                                        }
                                    } else if(userTypeMain == 0){
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
                                sCourse.adapter = srAdapter(this@StudentsListActivity, cList)
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentsListActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentsListActivity,
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
                            this@StudentsListActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }

}