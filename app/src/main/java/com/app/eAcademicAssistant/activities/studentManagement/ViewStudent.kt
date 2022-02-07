package com.app.eAcademicAssistant.activities.studentManagement

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class ViewStudent : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvCourse: TextView
    private lateinit var tvDepartment: TextView
    private lateinit var tvSemester: TextView
    private lateinit var tvMail: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvLCourse: TextView
    private lateinit var tvLDepartment: TextView
    private lateinit var tvLSemester: TextView
    private lateinit var tvLMail: TextView
    private lateinit var tvLGender: TextView
    private lateinit var backBtn: ImageView
    private lateinit var ivDelete: ImageView
    private lateinit var ivEdit: ImageView
    private lateinit var pbLoading: ProgressBar
    private var getUsersList: Call? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_view_student)

        init()

        ivEdit.visibility = View.GONE

        val keyId = intent.getStringExtra("key_student_id")
        val keyName = intent.getStringExtra("key_student_name")
        val keyEmail = intent.getStringExtra("key_student_email")
        val keyPassword = intent.getStringExtra("key_password")
        val keyUserType = intent.getStringExtra("key_user_type")
        val keyGender = intent.getStringExtra("key_gender")
        val keyStatus = intent.getStringExtra("key_status")

        backBtn.setOnClickListener() {
            finish()
        }

        ivDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete..!?")
            builder.setMessage("Are you sure to delete $keyName")
            builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                changeStudentStatus(keyId.toString())
            }
            builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

        tvName.text = keyName
        tvMail.text = keyEmail
        tvGender.text = when (keyGender?.toInt()) {
            0 -> "Male"
            1 -> "Female"
            else -> "Other"
        }
        tvCourse.visibility = View.GONE
        tvDepartment.visibility = View.GONE
        tvSemester.visibility = View.GONE
        tvLCourse.visibility = View.GONE
        tvLDepartment.visibility = View.GONE
        tvLSemester.visibility = View.GONE

    }


    private fun init() {
        backBtn = findViewById(R.id.backBtn)
        tvName = findViewById(R.id.tvName)
        tvCourse = findViewById(R.id.tvCourse)
        tvDepartment = findViewById(R.id.tvDepartment)
        tvSemester = findViewById(R.id.tvSemester)
        tvMail = findViewById(R.id.tvMail)
        tvGender = findViewById(R.id.tvGender)
        pbLoading = findViewById(R.id.pbLoading)
        tvLCourse = findViewById(R.id.tvLCourse)
        tvLDepartment = findViewById(R.id.tvLDepartment)
        tvLSemester = findViewById(R.id.tvLSemester)
        tvLMail = findViewById(R.id.tvLMail)
        tvLGender = findViewById(R.id.tvLGender)
        ivDelete = findViewById(R.id.ivDelete)
        ivEdit = findViewById(R.id.ivEdit)
    }

    private fun changeStudentStatus(id: String) {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@ViewStudent).getUser()?.userId
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
                            this@ViewStudent,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@ViewStudent,
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
                            this@ViewStudent,
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
                                    this@ViewStudent,
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
                                SnackBarUtils.showSnackBar(this@ViewStudent, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@ViewStudent,
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
                            this@ViewStudent,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })


    }


}