package com.app.eAcademicAssistant.activities.coursesManagement.semester

import android.app.Activity
import android.content.Intent
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
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.SemesterMenuRvAdapter
import com.app.eAcademicAssistant.model.SemesterMenuModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class Semesters : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var rvSemesters: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private var semestersList = ArrayList<SemesterMenuModel>()
    private var getUsersList: Call? = null
    private lateinit var ivSearch: ImageView
    private lateinit var etSearch: EditText
    private lateinit var ivSearchCancel: ImageView


    companion object {
        const val SEMESTER_EDIT = 1

    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_semesters)

        init()

        fabAdd.setOnClickListener {
            val intent = Intent(this, CreateSemester::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        ivSearch.setOnClickListener {
            ivSearch.visibility = View.GONE
            ivSearchCancel.visibility = View.VISIBLE
            etSearch.visibility = View.VISIBLE
        }

        etSearch.addTextChangedListener(){
            getSemList()
        }

        ivSearchCancel.setOnClickListener {

            etSearch.text.clear()
            ivSearch.visibility = View.VISIBLE
            ivSearchCancel.visibility = View.GONE
            etSearch.visibility = View.GONE

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("____", "___a1")

        when (requestCode) {


            SEMESTER_EDIT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val eKeSemId = data!!.getStringExtra("key_sem_id")
                    val eKeySemName = data!!.getStringExtra("key_sem_name")
                    val eKeyStatus = data!!.getIntExtra("key_status", 1)
                    val position = semestersList.indexOfFirst { it.id == eKeSemId }
                    if (position != -1) {
                        if (eKeyStatus != 2) {
                            semestersList[position].name = eKeySemName!!
                            semestersList[position].status = eKeyStatus

                            rvSemesters.adapter?.notifyItemChanged(position)
                        }
                    }
                }
            }
        }
    }


    private fun getSemList() {
        pbLoading.visibility = View.VISIBLE
        rvSemesters.visibility = View.GONE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@Semesters).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("status", jaStatus)
        jsonObject.put("searching_text", etSearch.text.trim().toString())
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "usersList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "usersList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@Semesters,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@Semesters,
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
                            this@Semesters,
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
                            semestersList.clear()
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (i in 0 until list.length()) {
                                val jo = list.getJSONObject(i)
                                val id = jo.getString("_id")
                                val name = jo.getString("_name")
                                val status = jo.getInt("_status")

                                semestersList.add(
                                    SemesterMenuModel(
                                        id,
                                        name,
                                        status
                                    )
                                )
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rvSemesters.layoutManager =
                                    LinearLayoutManager(this@Semesters)
                                rvSemesters.adapter = SemesterMenuRvAdapter(
                                    this@Semesters,
                                    semestersList, SEMESTER_EDIT
                                )
                                rvSemesters.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "usersList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@Semesters, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "usersList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@Semesters,
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
                            this@Semesters,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })


    }

    private fun init() {
        backButton = findViewById(R.id.backBtn)
        rvSemesters = findViewById(R.id.rvSemesters)
        fabAdd = findViewById(R.id.fabAdd)
        pbLoading = findViewById(R.id.pbLoading)
        ivSearch = findViewById(R.id.ivSearch)
        etSearch = findViewById(R.id.etSearch)
        ivSearchCancel = findViewById(R.id.ivSearchCancel)
    }

    override fun onResume() {
        super.onResume()
        semestersList.clear()
        getSemList()
    }
}