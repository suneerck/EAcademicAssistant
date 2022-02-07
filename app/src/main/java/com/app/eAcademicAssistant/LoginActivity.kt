package com.app.eAcademicAssistant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class LoginActivity : AppCompatActivity() {
    private lateinit var tvSkip: TextView
    private lateinit var tvForgetPassword: TextView
    private lateinit var btLogin: Button
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvFirstTimeLogin: TextView
    private lateinit var etUserName: TextView
    private lateinit var etPassword: TextView
    private lateinit var ivShowPassword: ImageView
    private var doLogin: Call? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_login)
        init()

        ivShowPassword.setOnClickListener {
            etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            ivShowPassword.visibility = View.GONE
        }

        tvSkip.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        tvForgetPassword.setOnClickListener {
            intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
        btLogin.setOnClickListener {

            val userName=etUserName.text.toString().trim()
            val password=etPassword.text.toString().trim()
            if (userName.isNullOrEmpty()){
                SnackBarUtils.showSnackBar(this@LoginActivity,"Enter your email as username")
                return@setOnClickListener
            }
            if (password.isNullOrEmpty()){
                SnackBarUtils.showSnackBar(this@LoginActivity,"Enter your password")
                return@setOnClickListener
            }

            doLoginApi(userName,password)
        }


        tvFirstTimeLogin.setOnClickListener {
            intent = Intent(this, FirstTimeLoginUser::class.java)
            startActivity(intent)
        }
    }

    private fun doLoginApi(userName: String, password: String) {
        btLogin.isEnabled=false
        btLogin.text=""
        pbLoading.visibility=View.VISIBLE
        val jsonObject = JSONObject()
        jsonObject.put("email",userName)
        jsonObject.put("password",password)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "loginValidation : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_USER_LOGIN).post(body.build())
            .build()
        doLogin = client.newCall(request)
        doLogin?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "loginValidation")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@LoginActivity,
                            getString(R.string.no_internet_connection)
                        )
                        btLogin.isEnabled=true
                        pbLoading.visibility=View.GONE
                        btLogin.text="Login"
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@LoginActivity,
                            getString(R.string.something_went_wrong)
                        )
                        btLogin.isEnabled=true
                        pbLoading.visibility=View.GONE
                        btLogin.text="Login"
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        btLogin.isEnabled=true
                        pbLoading.visibility=View.GONE
                        btLogin.text="Login"
                        SnackBarUtils.showSnackBar(this@LoginActivity, getString(R.string.error_message_connect_error))

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "loginValidation:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {

                            val joData = jo.getJSONObject("data")
                            val userId=joData.getString("_id")
                            val name=joData.getString("_name")
                            val email=joData.getString("_email")
                            val password=joData.getString("_password")
                            val UID=joData.getString("_uid")
                            val userType=joData.getInt("_user_type")
                            val gender=joData.getInt("_gender")
                            val status=joData.getInt("_status")

                            if (status == 1) {

                                DatabaseUtils.getInstance(this@LoginActivity)
                                    .setUser(userType, name, email, password, gender, UID, userId)
                            }

                            runOnUiThread {
                                btLogin.text="Login"
                                btLogin.isEnabled=true
                                pbLoading.visibility=View.GONE
                                if (status ==1) {
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    SnackBarUtils.showSnackBar(this@LoginActivity, "User not exist")
                                }
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "loginValidation:$message")
                            runOnUiThread {
                                btLogin.text="Login"
                                btLogin.isEnabled=true
                                pbLoading.visibility=View.GONE
                                SnackBarUtils.showSnackBar(this@LoginActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "loginValidation")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@LoginActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                btLogin.text="Login"
                                btLogin.isEnabled=true
                                pbLoading.visibility=View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "loginValidation" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(this@LoginActivity, getString(R.string.error_message_server_error))
                        btLogin.text="Login"
                        btLogin.isEnabled=true
                        pbLoading.visibility=View.GONE
                    }
                }
            }


        })
    }

    override fun onBackPressed() {
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun init() {
        tvSkip = findViewById(R.id.tvSkip)
        tvForgetPassword = findViewById(R.id.tvForgetPassword)
        tvFirstTimeLogin = findViewById(R.id.tvFirstTimeLogin)
        btLogin = findViewById(R.id.btLogin)
        etUserName = findViewById(R.id.etUserName)
        etPassword = findViewById(R.id.etPassword)
        pbLoading = findViewById(R.id.pbLoading)
        ivShowPassword = findViewById(R.id.ivShowPassword)
    }
}