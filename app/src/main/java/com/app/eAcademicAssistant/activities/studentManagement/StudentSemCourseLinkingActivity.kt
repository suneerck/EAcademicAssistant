package com.app.eAcademicAssistant.activities.studentManagement

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.SemSrAdapter
import com.app.eAcademicAssistant.adapters.SemStudentMenuRvAdapter
import com.app.eAcademicAssistant.adapters.StudentSemListRvAdapter
import com.app.eAcademicAssistant.adapters.srAdapter
import com.app.eAcademicAssistant.model.SemCourseMenuModel
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.app.eAcademicAssistant.model.UserModel1
import com.app.eAcademicAssistant.model.srModel
import com.app.eAcademicAssistant.objects.MenuConstants
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

class StudentSemCourseLinkingActivity : AppCompatActivity() {

    private lateinit var rvStudentsList: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var sCourse: AppCompatSpinner
    private lateinit var sSem: AppCompatSpinner
    private lateinit var pbLoading: ProgressBar
    private val cList = ArrayList<srModel>()
    private val sList = ArrayList<SemCourseMenuModel>()
    private val studentsList = ArrayList<StudentsMenuModel>()
    private val newStudentList = ArrayList<UserModel1>()
    private var getUsersList: Call? = null
    private var selectedCourseId = MenuConstants.NIL
    private var selectedSemId = MenuConstants.NIL
    private var selectedSemCourseId = MenuConstants.NIL
    private var getNewStudentList: Call? = null
    lateinit var selectedStudentName : String
    lateinit var selectedStudentId : String
    lateinit var semCourseLinkingId : String
    lateinit var studentLinkingId : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_sem_course_linking)

        init()
        getCourseList()

        backButton.setOnClickListener {
            finish()
        }

        fabAdd.setOnClickListener{
            newStudentList.clear()
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)
            val v = LayoutInflater.from(this)
                .inflate(R.layout.layout_dialog_add_semester, null)
            val clParent = v.findViewById<ConstraintLayout>(R.id.clParent)
            val rv = v.findViewById<RecyclerView>(R.id.rv)
            val pbLoading = v.findViewById<ProgressBar>(R.id.pbLoading)
            val textView = v.findViewById<TextView>(R.id.TextView)
            textView.text = "Students List"
            newStudentList(pbLoading, rv, dialog)
            clParent.setOnClickListener {
                dialog.cancel()
            }
            dialog.setContentView(v)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
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
                if (selectedCourseId!=MenuConstants.NIL){
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
                if (selectedSemId!=MenuConstants.NIL){
                    selectedSemCourseId = sList[position].semCourseId
                    studentsList.clear()
                    getStudentList()
                    fabAdd.visibility=View.VISIBLE
                }
            }
        }
    }

    fun linkSemStudent() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStudents = JSONArray()
        jaStudents.put(selectedStudentId)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@StudentSemCourseLinkingActivity).getUser()?.userId
        )
        jsonObject.put("semCourseLinkingId", selectedSemCourseId)
        jsonObject.put("userIds", jaStudents)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "linkSemStudent : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_STUDENT_SEM_COURSE_LINKING).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "linkSemStudent")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
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
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "linkSemStudent :$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@StudentSemCourseLinkingActivity,
                                    "Linked Successfully"
                                )
                                studentsList.clear()
                                getStudentList()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "linkSemStudent :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentSemCourseLinkingActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "linkSemStudent")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentSemCourseLinkingActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "linkSemStudent " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }

    fun linkSemStudentStatusChange() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@StudentSemCourseLinkingActivity).getUser()?.userId
        )
        jsonObject.put("linkingId", studentLinkingId)
        jsonObject.put("status", 2)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "linkSemStudent : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_STUDENT_SEM_COURSE_LINKING_STATUS_CHANGE).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "linkSemStudent")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
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
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "linkSemStudent :$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@StudentSemCourseLinkingActivity,
                                    "Unlinked Successfully"
                                )
                                studentsList.clear()
                                getStudentList()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "linkSemStudent :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentSemCourseLinkingActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "linkSemStudent")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentSemCourseLinkingActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "linkSemStudent " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }


    private fun newStudentList(pbLoading: ProgressBar, rv: RecyclerView, dialog: Dialog) {
        newStudentList.clear()
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        val jsonEmptyArray = JSONArray()
        val status = JSONArray()
        status.put(1)
        val jsonUserTypes = JSONArray()
        jsonUserTypes.put(MenuConstants.STUDENT)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@StudentSemCourseLinkingActivity).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("searching_text", "")
        jsonObject.put("status", status)
        jsonObject.put("userTypes", jsonUserTypes)
        jsonObject.put("genders", jsonEmptyArray)
        jsonObject.put("bloodGroups", jsonEmptyArray)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "newStudentList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_USER_LIST).post(body.build())
            .build()
        getNewStudentList = client.newCall(request)
        getNewStudentList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "newStudentList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
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
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }

                val resp = response.body()?.string()
                Log.e("______________resp", "newStudentList :$resp")
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
                                val email = jo.getString("_email")
                                val password = jo.getString("_password")
                                val UID = jo.getString("_uid")
                                val userType = jo.getInt("_user_type")
                                val gender = jo.getInt("_gender")
                                val status = jo.getInt("_status")

                                newStudentList.add(
                                    UserModel1(
                                        id,
                                        name,
                                        email
                                    )
                                )

                            }

                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@StudentSemCourseLinkingActivity)
                                rv.adapter = SemStudentMenuRvAdapter(
                                    this@StudentSemCourseLinkingActivity,
                                    newStudentList, dialog, pbLoading
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "newStudentList :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentSemCourseLinkingActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "newStudentList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentSemCourseLinkingActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "newStudentList " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
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
            DatabaseUtils.getInstance(this@StudentSemCourseLinkingActivity).getUser()?.userId
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
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
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
                            this@StudentSemCourseLinkingActivity,
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
                                    MenuConstants.NIL,"Please select a course"
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
                                var joId = "nil"
                                var joName = "nil"
                                if (departmentStatus == 1) {
                                    cList.add(
                                        srModel(
                                            id,
                                            name
                                        )
                                    )
                                }
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                sCourse.adapter = srAdapter(this@StudentSemCourseLinkingActivity, cList)
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getCourseList :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentSemCourseLinkingActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentSemCourseLinkingActivity,
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
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }

    fun dialogStudentStatusChange(name: String) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete..!?")
            builder.setMessage("Are you sure to unlink $name")
            builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                linkSemStudentStatusChange()
            }
            builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
    }

    private fun getStudentList() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@StudentSemCourseLinkingActivity).getUser()?.userId
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
                Log.e("___________onFailure", "getStudentList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
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
                            this@StudentSemCourseLinkingActivity,
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
                            for (j in 0 until list.length()){
                                val joList=list.getJSONObject(j)
                                val id = joList.getString("_id")
                                val uid = joList.getString("_user_id")
                                val linkedStatus = joList.getInt("_status")
                                val joStudentList=joList.getJSONObject("userDetails")
                                    val name = joStudentList.getString("_name")
                                    val mail = joStudentList.getString("_email")
                                    val gender = joStudentList.getInt("_gender")
                                    val userType = joStudentList.getInt("_user_type")
                                    val status = joStudentList.getInt("_status")
                                    val password = joStudentList.getString("_password")

                                if (linkedStatus == 1) {
                                    if (status == 1) {

                                        studentsList.add(
                                            StudentsMenuModel(
                                                id,
                                                name,
                                                mail,
                                                password,
                                                uid,
                                                userType,
                                                gender,
                                                status
                                            )
                                        )
                                    }
                                }

                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rvStudentsList.layoutManager =
                                    LinearLayoutManager(this@StudentSemCourseLinkingActivity)
                                rvStudentsList.adapter = StudentSemListRvAdapter(
                                    studentsList,
                                    this@StudentSemCourseLinkingActivity
                                )
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getStudentList :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentSemCourseLinkingActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getStudentList ")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentSemCourseLinkingActivity,
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
                            this@StudentSemCourseLinkingActivity,
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
            DatabaseUtils.getInstance(this@StudentSemCourseLinkingActivity).getUser()?.userId
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
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@StudentSemCourseLinkingActivity,
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
                            this@StudentSemCourseLinkingActivity,
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
                                semCourseLinkingId = jo.getString("_id")
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
                                sSem.adapter = SemSrAdapter(this@StudentSemCourseLinkingActivity, sList)
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("getSemList")
                            Log.e("_______resp_error", "getSemList :$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@StudentSemCourseLinkingActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSemList ")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@StudentSemCourseLinkingActivity,
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
                            this@StudentSemCourseLinkingActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }


    private fun init() {
        fabAdd = findViewById(R.id.fabAdd)
        rvStudentsList = findViewById(R.id.rvStudents)
        pbLoading = findViewById(R.id.pbLoading)
        backButton = findViewById(R.id.ivBackBtn)
        sCourse = findViewById(R.id.sCourse)
        sSem = findViewById(R.id.sSem)
    }
}