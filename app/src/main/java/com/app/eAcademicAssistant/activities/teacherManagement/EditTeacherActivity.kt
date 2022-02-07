package com.app.eAcademicAssistant.activities.teacherManagement

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.app.eAcademicAssistant.R
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

class EditTeacherActivity : AppCompatActivity() {

    private lateinit var btnSubmit: Button
    private lateinit var backButton: ImageView
    private lateinit var ivDelete: ImageView
    private lateinit var etUserName: TextView
    private lateinit var tvChangeUserType: TextView
    private lateinit var etPassword: TextView
    private lateinit var etMail: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var rgUserType: RadioGroup
    private lateinit var pbLoading: ProgressBar
    private var doLogin: Call? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var userId: String
    private var getUsersList: Call? = null
    private val userType1 = DatabaseUtils.getInstance(this@EditTeacherActivity).getUser()?.userType
    private var userType = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_edit_teacher)

        init()

        val keyId = intent.getStringExtra("key_id")
        val keyName = intent.getStringExtra("key_name")
        val keyEmail = intent.getStringExtra("key_email")
        val keyPassword = intent.getStringExtra("key_password")
        val keyUserType = intent.getStringExtra("key_user_type")
        val keyGender = intent.getStringExtra("key_gender")
        val keyStatus = intent.getStringExtra("key_status")

        if (userType1 == 0){
            tvChangeUserType.visibility = View.VISIBLE
        }

        tvChangeUserType.setOnClickListener{
            rgUserType.visibility = View.VISIBLE
            tvChangeUserType.text = "Change to : "
        }

        val genderKey = keyGender?.toInt()
        userId = keyId.toString()

        etUserName.setText(keyName).toString()
        etMail.setText(keyEmail).toString()
        etPassword.setText(keyPassword).toString()

        when (genderKey) {
            0 -> rgGender.check(R.id.rbMale)
            1 -> rgGender.check(R.id.rbFemale)
            2 -> rgGender.check(R.id.rbOther)
        }


        backButton.setOnClickListener {
            finish()
        }

        ivDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val id = keyId.toString()
            builder.setTitle("Delete..!?")
            builder.setMessage("Are you sure to delete $keyName")
            builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                changeTeacherStatus(id)
            }
            builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

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
                Toast.makeText(this, "Password must be 6 character or more", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            registerUser(username, password, email, gender)

        }

    }

    private fun init() {
        backButton = findViewById(R.id.backBtn)
        ivDelete = findViewById(R.id.ivDelete)
        btnSubmit = findViewById(R.id.btnSubmit)
        etUserName = findViewById(R.id.etUserName)
        etMail = findViewById(R.id.etMail)
        rgGender = findViewById(R.id.rgGender)
        rgUserType = findViewById(R.id.rgUserType)
        etPassword = findViewById(R.id.etPassword)
        pbLoading = findViewById(R.id.pbLoading)
        tvChangeUserType = findViewById(R.id.tvChangeUserType)
    }

    private fun registerUser(userName: String, password: String, email: String, gender: Int) {
        btnSubmit.isEnabled = false
        btnSubmit.text = ""
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaLocation = JSONArray()
        val location = JSONObject()
        userType = when (rgUserType.checkedRadioButtonId){
            R.id.rbUser1 -> 0
            R.id.rbUser2 -> 1
            R.id.rbUser3 -> 3
            else -> 2
        }
        location.put("type", "Point")
        location.put("coordinates", jaLocation)
        location.put("_id", "605442f2a60ff451293e32ab")
        jaLocation.put(75.125)
        jaLocation.put(25.2145)
        jsonObject.put("userId", userId)
        jsonObject.put("name", userName)
        jsonObject.put("email", email)
        jsonObject.put("password", password)
        jsonObject.put("userType", userType)
        jsonObject.put("gender", gender)
        jsonObject.put("bloodGroup", 1)
        jsonObject.put("bloodDonatedDate", 0)
        jsonObject.put("dob", 11546)
        jsonObject.put("location", location)
        jsonObject.put("fcmId", "nil")
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "teacherRegister : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_USER_EDIT).post(body.build())
            .build()
        doLogin = client.newCall(request)
        doLogin?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "teacherRegister")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditTeacherActivity,
                            getString(R.string.no_internet_connection)
                        )
                        btnSubmit.isEnabled = true
                        pbLoading.visibility = View.GONE
                        btnSubmit.text = "Login"
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditTeacherActivity,
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
                        btnSubmit.isEnabled = true
                        pbLoading.visibility = View.GONE
                        btnSubmit.text = "Login"
                        SnackBarUtils.showSnackBar(
                            this@EditTeacherActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "teacherRegister:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@EditTeacherActivity,
                                    "Register success"
                                )
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "teacherRegister:$message")
                            runOnUiThread {
                                btnSubmit.text = "Login"
                                btnSubmit.isEnabled = true
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@EditTeacherActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "teacherRegister")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@EditTeacherActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                btnSubmit.text = "Login"
                                btnSubmit.isEnabled = true
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "teacherRegister" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditTeacherActivity,
                            getString(R.string.error_message_server_error)
                        )
                        btnSubmit.text = "Login"
                        btnSubmit.isEnabled = true
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun changeTeacherStatus(id: String) {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@EditTeacherActivity).getUser()?.userId
        )
        jsonObject.put("linkingId", id)
        jsonObject.put("status", 2)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "usersList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_USERS_STATUS_CHANGE).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditTeacherActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditTeacherActivity,
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
                            this@EditTeacherActivity,
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
                                Toast.makeText(
                                    this@EditTeacherActivity,
                                    "Deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                pbLoading.visibility = View.GONE
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@EditTeacherActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@EditTeacherActivity,
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
                            this@EditTeacherActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })


    }

}