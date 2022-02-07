package com.app.eAcademicAssistant.activities.coursesManagement.subjects

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class EditSubject : AppCompatActivity() {

    private lateinit var name : EditText
    private lateinit var btnSubmit : Button
    private lateinit var backBtn : ImageView
    private lateinit var etSubjectName : EditText
    private var doLogin: Call? = null
    lateinit var subId : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_edit_subject)

        init()

        btnSubmit.setOnClickListener {
            val semesterName = name.text.trim().toString()
            if (semesterName.isEmpty()) {
                Toast.makeText(this, "Please enter semester", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                editSubject(semesterName)

            }

        }


        backBtn.setOnClickListener{
            finish()
        }

        val keySubId = intent.getStringExtra("key_sub_id")
        val keySubName = intent.getStringExtra("key_sub_name")
        val status = intent.getStringExtra("key_status")

        subId = keySubId.toString()

        name.setText(keySubName)

    }

    @SuppressLint("CutPasteId")
    private fun init() {
        name = findViewById(R.id.etSubjectName)
        btnSubmit = findViewById(R.id.btnSubmit)
        backBtn = findViewById(R.id.ivBackBtn)
        etSubjectName = findViewById(R.id.etSubjectName)
    }

    private fun editSubject(semesterName: String) {
        btnSubmit.isEnabled = false
        btnSubmit.text = "Loading.."
        val subName = etSubjectName.text.trim().toString()

        val jsonObject = JSONObject()
        jsonObject.put("userId", DatabaseUtils.getInstance(this@EditSubject).getUser()?.userId)
        jsonObject.put("name", subName)
        jsonObject.put("subjectId", subId)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "semCreate : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SUBJECT_EDIT).post(body.build())
            .build()
        doLogin = client.newCall(request)
        doLogin?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "semCreate")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditSubject,
                            getString(R.string.no_internet_connection)
                        )
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Login"
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditSubject,
                            getString(R.string.something_went_wrong)
                        )
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Login"
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Login"
                        SnackBarUtils.showSnackBar(
                            this@EditSubject,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "semCreate:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                Toast.makeText(this@EditSubject, "Registration Successfully Completed", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Toast.makeText(this@EditSubject, "An error occured ..!!", Toast.LENGTH_SHORT).show()

                            Log.e("_______resp_error", "semCreate:$message")
                            runOnUiThread {
                                btnSubmit.text = "Login"
                                btnSubmit.isEnabled = true
                                SnackBarUtils.showSnackBar(this@EditSubject, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "semCreate")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@EditSubject,
                                    getString(R.string.error_message_server_error)
                                )
                                btnSubmit.text = "Login"
                                btnSubmit.isEnabled = true

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "semCreate" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditSubject,
                            getString(R.string.error_message_server_error)
                        )
                        btnSubmit.text = "Login"
                        btnSubmit.isEnabled = true
                    }
                }
            }
        })
    }
}