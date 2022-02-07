package com.app.eAcademicAssistant.activities.coursesManagement.courses

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.DepartmentListCourseCreateRvAdapter
import com.app.eAcademicAssistant.model.DepartmentMenuModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class CreateCourseActivity : AppCompatActivity() {
    private var getDepartmentList: Call? = null
    private var createUser: Call? = null
    private lateinit var tvSelectDepartment: TextView
    private lateinit var etUserName: EditText
    private lateinit var etSemCount: EditText
    private lateinit var btnSubmit: TextView
    var selectedDepartmentId = "nil"
    private var departmentList = ArrayList<DepartmentMenuModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_add_course)
        init()
        tvSelectDepartment.setOnClickListener {
            selectDepartment()
        }
        btnSubmit.setOnClickListener {
            val userName = etUserName.text.trim().toString()
            if (userName.isNotEmpty()) {
                if (selectedDepartmentId != "nil") {
                    if(etSemCount.text.trim().toString().isNotEmpty()){
                        createCourse()
                    }else{
                        Toast.makeText(
                            this@CreateCourseActivity,
                            "Please enter sem count ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@CreateCourseActivity,
                        "Please select Department ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@CreateCourseActivity,
                    "Please enter course name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun init() {
        tvSelectDepartment = findViewById(R.id.tvDepartment)
        btnSubmit = findViewById(R.id.btnSubmit)
        etUserName = findViewById(R.id.etUserName)
        etSemCount = findViewById(R.id.etSemCount)
    }

    private fun selectDepartment() {
        departmentList.clear()
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)
        val v = LayoutInflater.from(this)
            .inflate(R.layout.layout_dialog_department, null)
        val clParent = v.findViewById<ConstraintLayout>(R.id.clParent)
        val rv = v.findViewById<RecyclerView>(R.id.rv)
        val pbLoading = v.findViewById<ProgressBar>(R.id.pbLoading)
        getDepartmentList(pbLoading, rv, dialog)
        clParent.setOnClickListener {
            dialog.cancel()
        }
        dialog.setContentView(v)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun getDepartmentList(pbLoading: ProgressBar, rv: RecyclerView, dialog: Dialog) {
        departmentList.clear()
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put("userId",DatabaseUtils.getInstance(this@CreateCourseActivity).getUser()?.userId)
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
        getDepartmentList = client.newCall(request)
        getDepartmentList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CreateCourseActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CreateCourseActivity,
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
                            this@CreateCourseActivity,
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
                                val inChargeUserStatus=jo.has("usertDetails")
                                var inChargeUserId="nil"
                                var inChargeUserName="nil"
                                var inChargeUserEmail="nil"
                                if(inChargeUserStatus){
                                    inChargeUserId=jo.getJSONObject("usertDetails").getString("_id")
                                    inChargeUserName=jo.getJSONObject("usertDetails").getString("_name")
                                    inChargeUserEmail=jo.getJSONObject("usertDetails").getString("_email")
                                }
                                departmentList.add(
                                    DepartmentMenuModel(
                                        id,
                                        name,
                                        inChargeUserId,
                                        inChargeUserName,
                                        inChargeUserEmail,
                                        status
                                    )
                                )
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@CreateCourseActivity)
                                rv.adapter = DepartmentListCourseCreateRvAdapter(
                                    this@CreateCourseActivity,
                                    departmentList,tvSelectDepartment,dialog
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@CreateCourseActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@CreateCourseActivity,
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
                            this@CreateCourseActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun createCourse() {
        btnSubmit.isEnabled = false
        tvSelectDepartment.isEnabled = false
        btnSubmit.text = getString(R.string.loading)

        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@CreateCourseActivity).getUser()?.userId
        )
        jsonObject.put("name", etUserName.text.trim().toString())
        jsonObject.put("departmentId", selectedDepartmentId)
        jsonObject.put("semCount", etSemCount.text.trim().toString().toInt())

        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "createCourse : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_COURSE_CREATE).post(body.build())
            .build()
        createUser = client.newCall(request)
        createUser?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartment.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@CreateCourseActivity,
                            getString(R.string.no_internet_connection)
                        )
                    }
                } else {

                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartment.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@CreateCourseActivity,
                            getString(R.string.something_went_wrong)
                        )
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartment.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@CreateCourseActivity,
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
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                btnSubmit.isEnabled = true
                                tvSelectDepartment.isEnabled = true
                                btnSubmit.text = getString(R.string.create)
                                SnackBarUtils.showSnackBar(this@CreateCourseActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                btnSubmit.isEnabled = true
                                tvSelectDepartment.isEnabled = true
                                btnSubmit.text = getString(R.string.create)
                                SnackBarUtils.showSnackBar(
                                    this@CreateCourseActivity,
                                    getString(R.string.error_message_server_error)
                                )

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "usersList" + e.message)
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartment.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@CreateCourseActivity,
                            getString(R.string.error_message_server_error)
                        )
                    }
                }
            }
        })

    }

}