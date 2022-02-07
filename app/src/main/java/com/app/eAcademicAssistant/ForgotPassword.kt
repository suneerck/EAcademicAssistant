package com.app.eAcademicAssistant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class ForgotPassword : AppCompatActivity() {
    lateinit var  btnSubmit:Button
    lateinit var  etMail:EditText
    private var getPassword: Call? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities,R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_forgot_password)
        etMail = findViewById(R.id.fpMail)
        btnSubmit = findViewById(R.id.BtnFPassword)
        val backButton = findViewById<ImageView>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()
        }
        btnSubmit.setOnClickListener {
            val email=etMail.text.trim().toString()
            if (!email.matches(emailPattern.toRegex())) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                getPassword(email)
            }
        }

    }
    private fun getPassword(email: String) {
        btnSubmit.isEnabled = false
        btnSubmit.text = "Loading"
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getPassword : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_FORGET_PASSWORD).post(body.build())
            .build()
        getPassword = client.newCall(request)
        getPassword?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getPassword")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ForgotPassword,
                            getString(R.string.no_internet_connection)
                        )
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Get Password"
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ForgotPassword,
                            getString(R.string.something_went_wrong)
                        )
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Get Password"
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Get Password"
                        SnackBarUtils.showSnackBar(
                            this@ForgotPassword,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getPassword:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                Toast.makeText(this@ForgotPassword, "Password send to $email ", Toast.LENGTH_SHORT).show()
                                val intent=Intent(this@ForgotPassword,LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")


                            Log.e("_______resp_error", "getPassword:$message")
                            runOnUiThread {
//                                Toast.makeText(this@ForgotPassword, "An error occured ..!!", Toast.LENGTH_SHORT).show()
                                btnSubmit.text = "Get Password"
                                btnSubmit.isEnabled = true
                                SnackBarUtils.showSnackBar(this@ForgotPassword, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getPassword")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ForgotPassword,
                                    getString(R.string.error_message_server_error)
                                )
                                btnSubmit.text = "Get Password"
                                btnSubmit.isEnabled = true

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getPassword" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ForgotPassword,
                            getString(R.string.error_message_server_error)
                        )
                        btnSubmit.text = "Get Password"
                        btnSubmit.isEnabled = true
                    }
                }
            }
        })
    }
}