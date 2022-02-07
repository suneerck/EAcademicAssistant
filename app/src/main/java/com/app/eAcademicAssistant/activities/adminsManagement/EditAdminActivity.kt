package com.app.eAcademicAssistant.activities.adminsManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.objects.MenuConstants
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class EditAdminActivity : AppCompatActivity() {

    private lateinit var btnSubmit: Button
    private lateinit var backButton: ImageView
    private lateinit var etUserName: TextView
    private lateinit var etPassword: TextView
    private lateinit var etMail: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var pbLoading: ProgressBar
    private var doLogin: Call? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_edit_admin)

        init()

        val keyId = intent.getStringExtra("key_id")
        val keyName = intent.getStringExtra("key_name")
        val keyEmail = intent.getStringExtra("key_email")
        val keyPassword = intent.getStringExtra("key_password")
        val keyUserType = intent.getStringExtra("key_user_type")
        val keyGender = intent.getStringExtra("key_gender")
        val keyStatus = intent.getStringExtra("key_status")

        val genderKey = keyGender?.toInt()
        userId = keyId.toString()

        etUserName.setText(keyName).toString()
        etMail.setText(keyEmail).toString()
        etPassword.setText(keyPassword).toString()

        when (genderKey){
            0 -> rgGender.check(R.id.rbMale)
            1 -> rgGender.check(R.id.rbFemale)
            2 -> rgGender.check(R.id.rbOther)
        }


        backButton.setOnClickListener{
            finish()
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
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length <= 6) {
                Toast.makeText(this, "Please enter valid password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            editUser(username, password, email, gender)

        }
        backButton.setOnClickListener {
            finish()
        }

    }
    private fun editUser(userName: String, password: String, email: String, gender: Int) {
        btnSubmit.isEnabled = false
        btnSubmit.text = ""
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaLocation = JSONArray()
        val location = JSONObject()
        location.put("type","Point")
        location.put("coordinates",jaLocation)
        location.put("_id","605442f2a60ff451293e32ab")
        jaLocation.put(75.125)
        jaLocation.put(25.2145)
        jsonObject.put("userId", userId)
        jsonObject.put("name", userName)
        jsonObject.put("email", email)
        jsonObject.put("password", password)
        jsonObject.put("userType", MenuConstants.ADMIN)
        jsonObject.put("gender", gender)
        jsonObject.put("bloodGroup", 1)
        jsonObject.put("bloodDonatedDate", 0)
        jsonObject.put("dob", 11546)
        jsonObject.put("location", location)
        jsonObject.put("fcmId", "nil")
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "editAdmin : $jsonObject")

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
                            this@EditAdminActivity,
                            getString(R.string.no_internet_connection)
                        )
                        btnSubmit.isEnabled = true
                        pbLoading.visibility = View.GONE
                        btnSubmit.text = "Login"
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditAdminActivity,
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
                            this@EditAdminActivity,
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
                                SnackBarUtils.showSnackBar(this@EditAdminActivity, "Register success")
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
                                SnackBarUtils.showSnackBar(this@EditAdminActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "teacherRegister")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@EditAdminActivity,
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
                            this@EditAdminActivity,
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

    private fun init() {
        backButton = findViewById(R.id.backBtn)
        btnSubmit = findViewById(R.id.btnSubmit)
        etUserName = findViewById(R.id.etUserName)
        etMail = findViewById(R.id.etMail)
        rgGender = findViewById(R.id.rgGender)
        etPassword = findViewById(R.id.etPassword)
        pbLoading = findViewById(R.id.pbLoading)
    }

}