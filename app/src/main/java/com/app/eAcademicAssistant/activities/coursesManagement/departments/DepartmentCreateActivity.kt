package com.app.eAcademicAssistant.activities.coursesManagement.departments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.DepartmentInChargeListRvAdapter
import com.app.eAcademicAssistant.model.UserListModel
import com.app.eAcademicAssistant.objects.MenuConstants
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException


class DepartmentCreateActivity : AppCompatActivity() {
    var usersList = ArrayList<UserListModel>()
    private var getUsersList: Call? = null
    private var createUser: Call? = null
    private lateinit var tvSelectDepartmentInCharge: TextView
    private lateinit var etName: EditText
    private lateinit var btnSubmit: TextView
    private lateinit var backBtn: ImageView
    var selectedDepartmentInCharge = "nil"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_department_create)
        init()

        backBtn.setOnClickListener{
            finish()
        }

        tvSelectDepartmentInCharge.setOnClickListener {
            selectDepartmentInCharge()
        }
        btnSubmit.setOnClickListener {
            val userName = etName.text.trim().toString()
            if (userName.isNotEmpty()) {
                if (selectedDepartmentInCharge != "nil") {
                    createDepartment()
                } else {
                    Toast.makeText(
                        this@DepartmentCreateActivity,
                        "Please select Department in charge",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@DepartmentCreateActivity,
                    "Please enter department name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
    private fun init() {
        tvSelectDepartmentInCharge = findViewById(R.id.tvSelectDepartmentInCharge)
        btnSubmit = findViewById(R.id.btnSubmit)
        etName = findViewById(R.id.etName)
        backBtn = findViewById(R.id.backBtn)
    }

    private fun selectDepartmentInCharge() {
        usersList.clear()
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)
        val v = LayoutInflater.from(this)
            .inflate(R.layout.layout_dialog_department_in_charge, null)
        val clParent = v.findViewById<ConstraintLayout>(R.id.clParent)
        val rv = v.findViewById<RecyclerView>(R.id.rv)
        val pbLoading = v.findViewById<ProgressBar>(R.id.pbLoading)
        getUsersList(pbLoading, rv, dialog)
        clParent.setOnClickListener {
            dialog.cancel()
        }
        dialog.setContentView(v)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun getUsersList(pbLoading: ProgressBar, rv: RecyclerView, dialog: Dialog) {
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        val jsonEmptyArray = JSONArray()
        jsonEmptyArray.put(1)
        val jsonArrayGender = JSONArray()
        jsonArrayGender.put(0)
        jsonArrayGender.put(1)
        jsonArrayGender.put(2)
        val jsonUserTypes = JSONArray()
        jsonUserTypes.put(MenuConstants.DEPARTMENT_INCHARGE)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@DepartmentCreateActivity).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("searching_text", "")
        jsonObject.put("status", jsonEmptyArray)
        jsonObject.put("userTypes", jsonUserTypes)
        jsonObject.put("genders", jsonArrayGender)
        jsonObject.put("bloodGroups", jsonEmptyArray)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "usersList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_USER_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@DepartmentCreateActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@DepartmentCreateActivity,
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
                            this@DepartmentCreateActivity,
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
                                val userId = jo.getString("_id")
                                val name = jo.getString("_name")
                                val email = jo.getString("_email")
                                val password = jo.getString("_password")
                                val UID = jo.getString("_uid")
                                val userType = jo.getInt("_user_type")
                                val gender = jo.getInt("_gender")
                                val status = jo.getInt("_status")
                                usersList.add(
                                    UserListModel(
                                        userId,
                                        name,
                                        email,
                                        password,
                                        UID,
                                        userType,
                                        gender,
                                        status
                                    )
                                )
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@DepartmentCreateActivity)
                                rv.adapter = DepartmentInChargeListRvAdapter(
                                    this@DepartmentCreateActivity,
                                    usersList,
                                    dialog,tvSelectDepartmentInCharge
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@DepartmentCreateActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@DepartmentCreateActivity,
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
                            this@DepartmentCreateActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }

    private fun createDepartment() {
        btnSubmit.isEnabled = false
        tvSelectDepartmentInCharge.isEnabled = false
        btnSubmit.text = getString(R.string.loading)

        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@DepartmentCreateActivity).getUser()?.userId
        )
        jsonObject.put("name", etName.text.trim().toString())
        jsonObject.put("departmentInChargeId", selectedDepartmentInCharge)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "usersList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_DEPARTMENT_CREATE).post(body.build())
            .build()
        createUser = client.newCall(request)
        createUser?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartmentInCharge.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@DepartmentCreateActivity,
                            getString(R.string.no_internet_connection)
                        )
                    }
                } else {

                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartmentInCharge.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@DepartmentCreateActivity,
                            getString(R.string.something_went_wrong)
                        )
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartmentInCharge.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@DepartmentCreateActivity,
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
                                tvSelectDepartmentInCharge.isEnabled = true
                                btnSubmit.text = getString(R.string.create)
                                SnackBarUtils.showSnackBar(this@DepartmentCreateActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                btnSubmit.isEnabled = true
                                tvSelectDepartmentInCharge.isEnabled = true
                                btnSubmit.text = getString(R.string.create)
                                SnackBarUtils.showSnackBar(
                                    this@DepartmentCreateActivity,
                                    getString(R.string.error_message_server_error)
                                )

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "usersList" + e.message)
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        tvSelectDepartmentInCharge.isEnabled = true
                        btnSubmit.text = getString(R.string.create)
                        SnackBarUtils.showSnackBar(
                            this@DepartmentCreateActivity,
                            getString(R.string.error_message_server_error)
                        )
                    }
                }
            }
        })

    }
}

