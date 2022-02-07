package com.app.eAcademicAssistant.activities.studentManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.model.srModel
import com.app.eAcademicAssistant.objects.MenuConstants
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class CreateStudentActivity : AppCompatActivity(){

    private lateinit var btnSubmit: Button
    private lateinit var backButton: ImageView
    private lateinit var etUserName: TextView
    private lateinit var etPassword: TextView
    private lateinit var etMail: EditText
    private lateinit var sCourse: Spinner
    private lateinit var sSem: Spinner
    private lateinit var rgGender: RadioGroup
    private lateinit var pbLoading: ProgressBar
    private val cList = ArrayList<srModel>()
    private val sList = ArrayList<srModel>()
    private var getUsersList: Call? = null
    private var selectedCourseId = MenuConstants.NIL
    private var selectedSemId = MenuConstants.NIL
    private var doLogin: Call? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var studentId : String
    lateinit var semCourseLinkId :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_add_student)

        init()
//        getCourseList()

        backButton.setOnClickListener{
            finish()
        }

//        sCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                selectedCourseId = cList[position].id
//                if (selectedCourseId!=MenuConstants.NIL){
//                    getSemList()
//                }
//            }
//        }
//
//        sSem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                selectedSemId = cList[position].id
//            }
//        }

        btnSubmit.setOnClickListener {
            val username = etUserName.text.trim().toString()
            val password = etPassword.text.trim().toString()
            val email = etMail.text.trim().toString()
            val gender = when (rgGender.checkedRadioButtonId) {
                R.id.rbMale -> 0
                R.id.rbFemale -> 1
                R.id.rbOther -> 2
                else -> -1
            }
            if (gender == -1) {
                Toast.makeText(this, "Please choose gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!email.matches(emailPattern.toRegex())) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length <= 5) {
                Toast.makeText(this, "Password must be 6 character or more", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
//
//            if (selectedCourseId == "nil") {
//                Toast.makeText(this, "Please select Course", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            if (selectedSemId == "nil") {
//                Toast.makeText(this, "Please select Semester", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
            registerUser(username, password, email, gender)

        }
    }
    private fun init() {
        backButton = findViewById(R.id.backBtn)
        btnSubmit = findViewById(R.id.btnSubmit)
        etUserName = findViewById(R.id.etUserName)
        etMail = findViewById(R.id.etMail)
        rgGender = findViewById(R.id.rgGender)
        etPassword = findViewById(R.id.etPassword)
        pbLoading = findViewById(R.id.pbLoading)
//        sCourse = findViewById(R.id.sCourse)
//        sSem = findViewById(R.id.sSem)
    }

    private fun registerUser(userName: String, password: String, email: String, gender: Int) {
        btnSubmit.isEnabled = false
        btnSubmit.text = ""
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaLocation = JSONArray()
        jaLocation.put(75.125)
        jaLocation.put(25.2145)
        jsonObject.put("userId", "nil")
        jsonObject.put("name", userName)
        jsonObject.put("email", email)
        jsonObject.put("password", password)
        jsonObject.put("gender", gender)
        jsonObject.put("userType", 3)
        jsonObject.put("bloodGroup", 1)
        jsonObject.put("bloodDonatedDate", 0)
        jsonObject.put("dob", 11546)
        jsonObject.put("location", jaLocation)
        jsonObject.put("fcmId", "nil")
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "studentRegister : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_USER_REGISTER).post(body.build())
            .build()
        doLogin = client.newCall(request)
        doLogin?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "studentRegister")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CreateStudentActivity,
                            getString(R.string.no_internet_connection)
                        )
                        btnSubmit.isEnabled = true
                        pbLoading.visibility = View.GONE
                        btnSubmit.text = "Login"
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CreateStudentActivity,
                            getString(R.string.something_went_wrong)
                        )
                        btnSubmit.isEnabled = true
                        pbLoading.visibility = View.GONE
                        btnSubmit.text = "Login"
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        pbLoading.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@CreateStudentActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }

                val resp = response.body()?.string()
                Log.e("______________resp", "studentRegister:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            val joData = jo.getJSONObject("data")

                            val id = joData.getString("_id")
                            val name = joData.getString("_name")
                            val status = joData.getInt("_status")

                            studentId = id

//                            studentSemCourseLink()

                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@CreateStudentActivity,
                                    "Registration success"
                                )
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "studentRegister:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@CreateStudentActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "studentRegister")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@CreateStudentActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "studentRegister " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CreateStudentActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }


//    private fun getCourseList() {
//        pbLoading.visibility = View.VISIBLE
//        val jsonObject = JSONObject()
//        val jaStatus = JSONArray()
//        jaStatus.put(1)
//        jaStatus.put(2)
//        jsonObject.put(
//            "userId",
//            DatabaseUtils.getInstance(this@CreateStudentActivity).getUser()?.userId
//        )
//        jsonObject.put("skip", -1)
//        jsonObject.put("searching_text", "")
//        jsonObject.put("status", jaStatus)
//        val data = JSONObject()
//        data.put("data", jsonObject)
//        Log.e("_____________req", "usersList : $jsonObject")
//
//        val body = MultipartBody.Builder()
//        body.setType(MultipartBody.FORM)
//        body.addFormDataPart("json_data", data.toString())
//
//        val client = OkHttpUtils.getOkHttpClient()
//        val request = Request.Builder()
//            .url(URLUtils.URL_COURSE_LIST).post(body.build())
//            .build()
//        getUsersList = client.newCall(request)
//        getUsersList?.enqueue(object : Callback {
//            override fun onFailure(call: Call?, e: IOException?) {
//                Log.e("___________onFailure", "getCourseList")
//                if (e is UnknownHostException) {
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.no_internet_connection)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                } else {
//
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.something_went_wrong)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                }
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                if (call.isCanceled) {
//                    runOnUiThread {
//                        pbLoading.visibility = View.GONE
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.error_message_connect_error)
//                        )
//
//                    }
//
//                }
//                val resp = response.body()?.string()
//                Log.e("______________resp", "usersList:$resp")
//                try {
//                    val jo = JSONObject(resp)
//                    when (response.code()) {
//                        OkHttpUtils.STATUS_OK -> {
//                            cList.add(
//                                srModel(
//                                    MenuConstants.NIL,"Please select a course"
//                                )
//                            )
//                            val joData = jo.getJSONObject("data")
//                            val list = joData.getJSONArray("list")
//                            for (i in 0 until list.length()) {
//                                val jo = list.getJSONObject(i)
//                                val id = jo.getString("_id")
//                                val name = jo.getString("_name")
//                                val semCount = jo.getInt("_sem_count")
//                                val status = jo.getInt("_status")
//
//                                cList.add(
//                                    srModel(
//                                        id,
//                                        name
//                                    )
//                                )
//                            }
//                            runOnUiThread {
//                                pbLoading.visibility = View.GONE
//                                sCourse.adapter = srAdapter(this@CreateStudentActivity, cList)
//                            }
//                        }
//                        OkHttpUtils.STATUS_ERROR -> {
//                            val message = jo.getString("message")
//                            Log.e("_______resp_error", "usersList:$message")
//                            runOnUiThread {
//                                pbLoading.visibility = View.GONE
//                                SnackBarUtils.showSnackBar(this@CreateStudentActivity, message)
//
//                            }
//
//
//                        }
//                        else -> {
//                            Log.e("_______Internal_error", "usersList")
//                            runOnUiThread {
//                                SnackBarUtils.showSnackBar(
//                                    this@CreateStudentActivity,
//                                    getString(R.string.error_message_server_error)
//                                )
//                                pbLoading.visibility = View.GONE
//
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.e("__________exc", "usersList" + e.message)
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.error_message_server_error)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                }
//            }
//        })
//
//    }

//    private fun getSemList() {
//        pbLoading.visibility = View.VISIBLE
//        val jsonObject = JSONObject()
//        val jaStatus = JSONArray()
//        jaStatus.put(1)
//        jaStatus.put(2)
//        jsonObject.put(
//            "userId",
//            DatabaseUtils.getInstance(this@CreateStudentActivity).getUser()?.userId
//        )
//        jsonObject.put("courseId", selectedCourseId)
//        val data = JSONObject()
//        data.put("data", jsonObject)
//        Log.e("_____________req", "usersList : $jsonObject")
//
//        val body = MultipartBody.Builder()
//        body.setType(MultipartBody.FORM)
//        body.addFormDataPart("json_data", data.toString())
//
//        val client = OkHttpUtils.getOkHttpClient()
//        val request = Request.Builder()
//            .url(URLUtils.URL_SEM_COURSE_LIST).post(body.build())
//            .build()
//        getUsersList = client.newCall(request)
//        getUsersList?.enqueue(object : Callback {
//            override fun onFailure(call: Call?, e: IOException?) {
//                Log.e("___________onFailure", "getCourseList")
//                if (e is UnknownHostException) {
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.no_internet_connection)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                } else {
//
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.something_went_wrong)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                }
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                if (call.isCanceled) {
//                    runOnUiThread {
//                        pbLoading.visibility = View.GONE
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.error_message_connect_error)
//                        )
//
//                    }
//
//                }
//                val resp = response.body()?.string()
//                Log.e("______________resp", "usersList:$resp")
//                try {
//                    val jo = JSONObject(resp)
//                    when (response.code()) {
//                        OkHttpUtils.STATUS_OK -> {
//                            cList.add(
//                                srModel(
//                                    MenuConstants.NIL,"Please select a course"
//                                )
//                            )
//                            val joData = jo.getJSONObject("data")
//                            val jaList = joData.getJSONArray("list")
//                            for (i in 0 until jaList.length()) {
//                                val jo = jaList.getJSONObject(i)
//                                val scId = jo.getString("_id")
//                                semCourseLinkId = scId
//                                val jaSemList = jo.getJSONArray("semList")
//                                for (j in 0 until jaSemList.length()) {
//                                    val joSemList = jaSemList.getJSONObject(j)
//                                    val id = joSemList.getString("_id")
//                                    val name = joSemList.getString("_name")
//                                    val status = joSemList.getInt("_status")
//
//                                    sList.add(
//                                        srModel(
//                                            id,
//                                            name
//                                        )
//                                    )
//                                }
//                            }
//                            runOnUiThread {
//                                pbLoading.visibility = View.GONE
//                                sSem.adapter = srAdapter(this@CreateStudentActivity, sList)
//                            }
//                        }
//                        OkHttpUtils.STATUS_ERROR -> {
//                            val message = jo.getString("message")
//                            Log.e("_______resp_error", "usersList:$message")
//                            runOnUiThread {
//                                pbLoading.visibility = View.GONE
//                                SnackBarUtils.showSnackBar(this@CreateStudentActivity, message)
//
//                            }
//
//
//                        }
//                        else -> {
//                            Log.e("_______Internal_error", "usersList")
//                            runOnUiThread {
//                                SnackBarUtils.showSnackBar(
//                                    this@CreateStudentActivity,
//                                    getString(R.string.error_message_server_error)
//                                )
//                                pbLoading.visibility = View.GONE
//
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.e("__________exc", "usersList" + e.message)
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.error_message_server_error)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                }
//            }
//        })
//
//    }




//    private fun studentSemCourseLink(){
//        pbLoading.visibility = View.VISIBLE
//        val jsonObject = JSONObject()
//        val jaUserIds = JSONArray()
//        jaUserIds.put(studentId)
//        jsonObject.put(
//            "userId",
//            DatabaseUtils.getInstance(this@CreateStudentActivity).getUser()?.userId
//        )
//        jsonObject.put("semCourseLinkingId", semCourseLinkId)
//        jsonObject.put("userIds", jaUserIds)
//        val data = JSONObject()
//        data.put("data", jsonObject)
//        Log.e("_____________req", "studentSemCourseLink : $jsonObject")
//
//        val body = MultipartBody.Builder()
//        body.setType(MultipartBody.FORM)
//        body.addFormDataPart("json_data", data.toString())
//
//        val client = OkHttpUtils.getOkHttpClient()
//        val request = Request.Builder()
//            .url(URLUtils.URL_STUDENT_SEM_COURSE_LINKING).post(body.build())
//            .build()
//        getUsersList = client.newCall(request)
//        getUsersList?.enqueue(object : Callback {
//            override fun onFailure(call: Call?, e: IOException?) {
//                Log.e("___________onFailure", "getCourseList")
//                if (e is UnknownHostException) {
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.no_internet_connection)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                } else {
//
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.something_went_wrong)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                }
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                if (call.isCanceled) {
//                    runOnUiThread {
//                        pbLoading.visibility = View.GONE
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.error_message_connect_error)
//                        )
//
//                    }
//
//                }
//                val resp = response.body()?.string()
//                Log.e("______________resp", "usersList:$resp")
//                try {
//                    val jo = JSONObject(resp)
//                    when (response.code()) {
//                        OkHttpUtils.STATUS_OK -> {
//                            runOnUiThread {
//                                Toast.makeText(this@CreateStudentActivity, "Successfully Created", Toast.LENGTH_SHORT).show()
//                                pbLoading.visibility = View.GONE
//                                sSem.adapter = srAdapter(this@CreateStudentActivity, cList)
//                                finish()
//                            }
//                        }
//                        OkHttpUtils.STATUS_ERROR -> {
//                            val message = jo.getString("message")
//                            Log.e("_______resp_error", "usersList:$message")
//                            runOnUiThread {
//                                pbLoading.visibility = View.GONE
//                                SnackBarUtils.showSnackBar(this@CreateStudentActivity, message)
//
//                            }
//
//
//                        }
//                        else -> {
//                            Log.e("_______Internal_error", "usersList")
//                            runOnUiThread {
//                                SnackBarUtils.showSnackBar(
//                                    this@CreateStudentActivity,
//                                    getString(R.string.error_message_server_error)
//                                )
//                                pbLoading.visibility = View.GONE
//
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.e("__________exc", "usersList" + e.message)
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CreateStudentActivity,
//                            getString(R.string.error_message_server_error)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                }
//            }
//        })
//
//    }

}