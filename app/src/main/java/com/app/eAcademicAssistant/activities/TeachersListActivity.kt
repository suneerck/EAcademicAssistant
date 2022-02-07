package com.app.eAcademicAssistant.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.StudentListRvAdapter
import com.app.eAcademicAssistant.model.TeachersMenuModel
import com.app.eAcademicAssistant.objects.MenuConstants
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class TeachersListActivity : AppCompatActivity() {

    private lateinit var ivSearch: ImageView
    private lateinit var etSearch: EditText
    private lateinit var ivSearchCancel: ImageView
    private lateinit var rv: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var pbLoading: ProgressBar
    private var studentsList = ArrayList<TeachersMenuModel>()
    private var getUsersList: Call? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teachers_list)

        init()

        backButton.setOnClickListener {
            finish()
        }

        getTeachersList()

        ivSearch.setOnClickListener {
            ivSearch.visibility = View.GONE
            ivSearchCancel.visibility = View.VISIBLE
            etSearch.visibility = View.VISIBLE
        }

        etSearch.addTextChangedListener() {
            getTeachersList()
        }

        ivSearchCancel.setOnClickListener {

            etSearch.text.clear()
            ivSearch.visibility = View.VISIBLE
            ivSearchCancel.visibility = View.GONE
            etSearch.visibility = View.GONE

        }

    }

    private fun init() {
        rv = findViewById(R.id.rv)
        pbLoading = findViewById(R.id.pbLoading)
        backButton = findViewById(R.id.backBtn)
        ivSearch = findViewById(R.id.ivSearch)
        etSearch = findViewById(R.id.etSearch)
        ivSearchCancel = findViewById(R.id.ivSearchCancel)
    }

    private fun getTeachersList() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jsonEmptyArray = JSONArray()
        jsonEmptyArray.put(1)
        val jsonUserTypes = JSONArray()
        jsonUserTypes.put(MenuConstants.TEACHER)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@TeachersListActivity).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("searching_text", etSearch.text.trim().toString())
        jsonObject.put("status", jsonEmptyArray)
        jsonObject.put("userTypes", jsonUserTypes)
        jsonObject.put("genders", jsonEmptyArray)
        jsonObject.put("bloodGroups", jsonEmptyArray)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getTeachersList : $jsonObject")

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
                Log.e("___________onFailure", "getTeachersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeachersListActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeachersListActivity,
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
                            this@TeachersListActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getTeachersList:$resp")
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
                                    TeachersMenuModel(
                                        name,
                                        email
                                    )
                                )

                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE

                                rv.layoutManager =
                                    LinearLayoutManager(this@TeachersListActivity)
                                rv.adapter = StudentListRvAdapter(
                                    this@TeachersListActivity,
                                    studentsList
                                )
                            }

                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getTeachersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@TeachersListActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getTeachersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TeachersListActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getTeachersList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TeachersListActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }
}