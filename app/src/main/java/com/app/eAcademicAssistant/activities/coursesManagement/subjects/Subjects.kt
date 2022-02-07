package com.app.eAcademicAssistant.activities.coursesManagement.subjects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.SubjectMenuRvAdapter
import com.app.eAcademicAssistant.model.SubjectMenuModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class Subjects : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var rvSubjects : RecyclerView
    private var subjectList = ArrayList<SubjectMenuModel>()
    private lateinit var pbLoading: ProgressBar
    private lateinit var fabAdd: FloatingActionButton
    private var getUsersList: Call? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_subjects)

        init()

        backButton.setOnClickListener{
            finish()
        }
        fabAdd.setOnClickListener {
            val intent= Intent(this@Subjects, AddSubject::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        rvSubjects.layoutManager = GridLayoutManager(this, 1)
        rvSubjects.adapter = SubjectMenuRvAdapter(
            this@Subjects,
            subjectList
        )
    }

   fun subjectStatusChange(subId: String) {
        pbLoading.visibility = View.VISIBLE
        rvSubjects.visibility = View.GONE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@Subjects).getUser()?.userId
        )
        jsonObject.put("linkingId", subId)
        jsonObject.put("status", 2)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "usersList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SUBJECT_STATUS_CHANGE).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@Subjects,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@Subjects,
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
                            this@Subjects,
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
                                pbLoading.visibility = View.GONE
                                Toast.makeText(
                                    this@Subjects,
                                    "Deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                subjectList.clear()
                                getSubjectsList()
                            }
                        }

                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@Subjects, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@Subjects,
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
                            this@Subjects,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }

    private fun getSubjectsList() {
        pbLoading.visibility = View.VISIBLE
        rvSubjects.visibility = View.GONE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@Subjects).getUser()?.userId
        )
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
            .url(URLUtils.URL_SUBJECT_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@Subjects,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@Subjects,
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
                            this@Subjects,
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

                                subjectList.add(
                                    SubjectMenuModel(
                                        id,
                                        name,status
                                    )
                                )
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rvSubjects.layoutManager =
                                    LinearLayoutManager(this@Subjects)
                                rvSubjects.adapter = SubjectMenuRvAdapter(
                                    this@Subjects,
                                    subjectList
                                )
                                rvSubjects.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@Subjects, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@Subjects,
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
                            this@Subjects,
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
        subjectList.clear()
        getSubjectsList()
    }

    private fun init() {
        backButton = findViewById(R.id.backBtn)
        rvSubjects = findViewById(R.id.rvSubjects)
        pbLoading = findViewById(R.id.pbLoading)
        fabAdd = findViewById(R.id.fabAdd)
    }
}