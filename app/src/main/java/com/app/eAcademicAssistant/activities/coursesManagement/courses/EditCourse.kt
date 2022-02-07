package com.app.eAcademicAssistant.activities.coursesManagement.courses

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.DepartmentListCourseEditRvAdapter
import com.app.eAcademicAssistant.model.DepartmentMenuModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class EditCourse : AppCompatActivity() {

    private var getDepartmentList: Call? = null
    private var createUser: Call? = null
    private lateinit var etCourseName: EditText
    private lateinit var etSemCount: EditText
    private lateinit var tvSelectDepartment: TextView
    private lateinit var backBtn: ImageView
    private lateinit var ivDelete: ImageView
    private lateinit var btnSubmit: Button
    var selectedDepartmentId = "nil"
    private var departmentList = ArrayList<DepartmentMenuModel>()
    private lateinit var courseId: String
    var semCount: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_edit_course)
        init()

        val keyCourseId = intent.getStringExtra("key_course_id")
        val keyCourseName = intent.getStringExtra("key_course_name")
        var keyDepartmentId = intent.getStringExtra("key_department_id")
        val keyDepartmentName = intent.getStringExtra("key_department_name")
        val keySemCount = intent.getStringExtra("key_sem_count")
        var keyStatus = intent.getStringExtra("key_status")


        courseId = keyCourseId.toString()

        etCourseName.setText(keyCourseName)
        etSemCount.setText(keySemCount)

        tvSelectDepartment.text = "$keyDepartmentName  (Tap to change)"

        backBtn.setOnClickListener {
            finish()
        }

        tvSelectDepartment.setOnClickListener {
            selectDepartment()
        }

        ivDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete..!?")
            builder.setMessage("Are you sure to delete department of $keyDepartmentName")
            builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                changeCourseStatus()
            }
            builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }


        btnSubmit.setOnClickListener {
            val userName = etCourseName.text.trim().toString()
            val eSemCount = etSemCount.text.trim().toString()
            if (userName.isNotEmpty()) {
                if (selectedDepartmentId != "nil") {
                    if (etSemCount.text.trim().toString().isNotEmpty()) {
                        semCount = eSemCount.toInt()
                        editCourse()
                    } else {
                        Toast.makeText(
                            this@EditCourse,
                            "Please enter sem count ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@EditCourse,
                        "Please select Department ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@EditCourse,
                    "Please enter course name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun init() {
        tvSelectDepartment = findViewById(R.id.tvDepartment)
        etCourseName = findViewById(R.id.etName)
        etSemCount = findViewById(R.id.etSemCount)
        btnSubmit = findViewById(R.id.btnSubmit)
        backBtn = findViewById(R.id.backBtn)
        ivDelete = findViewById(R.id.ivDelete)
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
        jsonObject.put("userId", DatabaseUtils.getInstance(this@EditCourse).getUser()?.userId)
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
                            this@EditCourse,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditCourse,
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
                            this@EditCourse,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "editCourse: $resp")
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
                                val inChargeUserStatus = jo.has("usertDetails")
                                var inChargeUserId = "nil"
                                var inChargeUserName = "nil"
                                var inChargeUserEmail = "nil"
                                if (inChargeUserStatus) {
                                    inChargeUserId =
                                        jo.getJSONObject("usertDetails").getString("_id")
                                    inChargeUserName =
                                        jo.getJSONObject("usertDetails").getString("_name")
                                    inChargeUserEmail =
                                        jo.getJSONObject("usertDetails").getString("_email")
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
                                    LinearLayoutManager(this@EditCourse)
                                rv.adapter = DepartmentListCourseEditRvAdapter(
                                    this@EditCourse,
                                    departmentList, tvSelectDepartment, dialog
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@EditCourse, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@EditCourse,
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
                            this@EditCourse,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun editCourse() {
        btnSubmit.isEnabled = false
        tvSelectDepartment.isEnabled = false
        btnSubmit.text = getString(R.string.loading)

        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@EditCourse).getUser()?.userId
        )
        jsonObject.put("name", etCourseName.text)
        jsonObject.put("departmentId", selectedDepartmentId)
        jsonObject.put("semCount", semCount)
        jsonObject.put("courseId", courseId)

        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "editCourse : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_COURSE_EDIT).post(body.build())
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
                            this@EditCourse,
                            getString(R.string.no_internet_connection)
                        )
                    }
                } else {

                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartment.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@EditCourse,
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
                            this@EditCourse,
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
                                SnackBarUtils.showSnackBar(this@EditCourse, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                btnSubmit.isEnabled = true
                                tvSelectDepartment.isEnabled = true
                                btnSubmit.text = getString(R.string.create)
                                SnackBarUtils.showSnackBar(
                                    this@EditCourse,
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
                            this@EditCourse,
                            getString(R.string.error_message_server_error)
                        )
                    }
                }
            }
        })

    }

    private fun changeCourseStatus() {
        btnSubmit.isEnabled = false
        tvSelectDepartment.isEnabled = false
        btnSubmit.text = getString(R.string.loading)

        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@EditCourse).getUser()?.userId
        )
        jsonObject.put("linkingId", courseId)
        jsonObject.put("status", 2)

        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "editCourse : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_COURSE_STATUS_CHANGE).post(body.build())
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
                            this@EditCourse,
                            getString(R.string.no_internet_connection)
                        )
                    }
                } else {

                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartment.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@EditCourse,
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
                            this@EditCourse,
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
                                    this@EditCourse,
                                    "Deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                                SnackBarUtils.showSnackBar(this@EditCourse, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                btnSubmit.isEnabled = true
                                tvSelectDepartment.isEnabled = true
                                btnSubmit.text = getString(R.string.create)
                                SnackBarUtils.showSnackBar(
                                    this@EditCourse,
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
                            this@EditCourse,
                            getString(R.string.error_message_server_error)
                        )
                    }
                }
            }
        })

    }


}