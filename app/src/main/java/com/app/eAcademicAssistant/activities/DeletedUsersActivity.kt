package com.app.eAcademicAssistant.activities

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
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.DeletedUsersListRvAdapter
import com.app.eAcademicAssistant.model.StudentsMenuModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class DeletedUsersActivity : AppCompatActivity() {

    private lateinit var ivSearch: ImageView
    private lateinit var etSearch: EditText
    private lateinit var ivSearchCancel: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var rvHodManagementList: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private var usersList1 = ArrayList<StudentsMenuModel>()
    private var getUsersList: Call? = null
    private var doLogin: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_deleted_users)

        init()

        backBtn.setOnClickListener() {
            finish()
        }

        ivSearch.setOnClickListener {
            ivSearch.visibility = View.GONE
            ivSearchCancel.visibility = View.VISIBLE
            etSearch.visibility = View.VISIBLE
        }

        etSearch.addTextChangedListener() {
            getUsersList()
        }

        ivSearchCancel.setOnClickListener {

            etSearch.text.clear()
            ivSearch.visibility = View.VISIBLE
            ivSearchCancel.visibility = View.GONE
            etSearch.visibility = View.GONE

        }
    }

    override fun onResume() {
        super.onResume()
        usersList1.clear()
        getUsersList()
    }

    private fun init() {
        backBtn = findViewById(R.id.ivBackBtn)
        rvHodManagementList = findViewById(R.id.rvStudents)
        pbLoading = findViewById(R.id.pbLoading)
        ivSearch = findViewById(R.id.ivSearch)
        etSearch = findViewById(R.id.etSearch)
        ivSearchCancel = findViewById(R.id.ivSearchCancel)
    }

    private fun getUsersList() {
        pbLoading.visibility = View.VISIBLE
        rvHodManagementList.visibility = View.GONE
        val jsonObject = JSONObject()
        val jsonEmptyArray = JSONArray()
        val status = JSONArray()
        status.put(2)
        val gender = JSONArray()
        val jsonUserTypes = JSONArray()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@DeletedUsersActivity).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("searching_text", etSearch.text.trim().toString())
        jsonObject.put("status", status)
        jsonObject.put("userTypes", jsonUserTypes)
        jsonObject.put("genders", gender)
        jsonObject.put("bloodGroups", jsonEmptyArray)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getUsersList : $jsonObject")

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
                Log.e("___________onFailure", "getUsersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@DeletedUsersActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@DeletedUsersActivity,
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
                            this@DeletedUsersActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getUsersList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            usersList1.clear()
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
                                usersList1.add(
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
                                rvHodManagementList.layoutManager =
                                    LinearLayoutManager(this@DeletedUsersActivity)
                                rvHodManagementList.adapter = DeletedUsersListRvAdapter(
                                    this@DeletedUsersActivity,
                                    usersList1
                                )
                                rvHodManagementList.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getUsersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@DeletedUsersActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getUsersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@DeletedUsersActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getUsersList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@DeletedUsersActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }

    fun changeUsersStatus(id: String) {
        pbLoading.visibility = View.VISIBLE
        rvHodManagementList.visibility = View.GONE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@DeletedUsersActivity).getUser()?.userId
        )
        jsonObject.put("linkingId", id)
        jsonObject.put("status", 1)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "changeUsersStatus : $jsonObject")

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
                Log.e("___________onFailure", "changeUsersStatus")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@DeletedUsersActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@DeletedUsersActivity,
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
                            this@DeletedUsersActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "changeUsersStatus:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {

                            runOnUiThread {
                                Toast.makeText(
                                    this@DeletedUsersActivity,
                                    "Deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                pbLoading.visibility = View.GONE

                                usersList1.clear()
                                getUsersList()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "changeUsersStatus:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@DeletedUsersActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "changeUsersStatus")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@DeletedUsersActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "changeUsersStatus" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@DeletedUsersActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }
}