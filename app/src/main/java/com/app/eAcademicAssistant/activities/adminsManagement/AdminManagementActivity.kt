package com.app.eAcademicAssistant.activities.adminsManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.AdminManagementListRvAdapter
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.app.eAcademicAssistant.objects.MenuConstants
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class AdminManagementActivity : AppCompatActivity() {

    private lateinit var ivSearch: ImageView
    private lateinit var etSearch: EditText
    private lateinit var ivSearchCancel: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var rvStudentsManagement: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private var studentsList = ArrayList<StudentsMenuModel>()
    private var getUsersList: Call? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_management_list)
        init()

        fabAdd.setOnClickListener {
            val intent = Intent(this, CreateAdmin::class.java)
            startActivity(intent)
        }

        backBtn.setOnClickListener() {
            finish()
        }

        ivSearch.setOnClickListener {
            ivSearch.visibility = View.GONE
            ivSearchCancel.visibility = View.VISIBLE
            etSearch.visibility = View.VISIBLE
        }

        etSearch.addTextChangedListener() {
            getAdminList()
        }

        ivSearchCancel.setOnClickListener {

            etSearch.text.clear()
            ivSearch.visibility = View.VISIBLE
            ivSearchCancel.visibility = View.GONE
            etSearch.visibility = View.GONE

        }

    }

    fun changeAdminStatus(id: String) {
        pbLoading.visibility = View.VISIBLE
        rvStudentsManagement.visibility = View.GONE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@AdminManagementActivity).getUser()?.userId
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
                            this@AdminManagementActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@AdminManagementActivity,
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
                            this@AdminManagementActivity,
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
                                    this@AdminManagementActivity,
                                    "Deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                pbLoading.visibility = View.GONE

                                studentsList.clear()
                                getAdminList()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@AdminManagementActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@AdminManagementActivity,
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
                            this@AdminManagementActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })


    }

    private fun getAdminList() {
        pbLoading.visibility = View.VISIBLE
        rvStudentsManagement.visibility = View.GONE
        val jsonObject = JSONObject()
        val jsonEmptyArray = JSONArray()
        val gender = JSONArray()
        jsonEmptyArray.put(1)
        val jsonUserTypes = JSONArray()
        jsonUserTypes.put(MenuConstants.ADMIN)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@AdminManagementActivity).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("searching_text", etSearch.text.trim().toString())
        jsonObject.put("status", jsonEmptyArray)
        jsonObject.put("userTypes", jsonUserTypes)
        jsonObject.put("genders", gender)
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
                            this@AdminManagementActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@AdminManagementActivity,
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
                            this@AdminManagementActivity,
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
                            studentsList.clear()
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
                                studentsList.add(
                                    StudentsMenuModel(
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
                                rvStudentsManagement.layoutManager =
                                    LinearLayoutManager(this@AdminManagementActivity)
                                rvStudentsManagement.adapter = AdminManagementListRvAdapter(
                                    this@AdminManagementActivity,
                                    studentsList
                                )
                                rvStudentsManagement.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@AdminManagementActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@AdminManagementActivity,
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
                            this@AdminManagementActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })


    }

    override fun onResume() {
        super.onResume()
        studentsList.clear()
        getAdminList()
    }

    private fun init() {
        backBtn = findViewById(R.id.ivBackBtn)
        rvStudentsManagement = findViewById(R.id.rvStudents)
        fabAdd = findViewById(R.id.fabAdd)
        pbLoading = findViewById(R.id.pbLoading)
        ivSearch = findViewById(R.id.ivSearch)
        etSearch = findViewById(R.id.etSearch)
        ivSearchCancel = findViewById(R.id.ivSearchCancel)
    }
}