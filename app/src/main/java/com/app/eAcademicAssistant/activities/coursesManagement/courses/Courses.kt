package com.app.eAcademicAssistant.activities.coursesManagement.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.R.anim
import com.app.eAcademicAssistant.adapters.CourseMenuRvAdapter
import com.app.eAcademicAssistant.model.CourseMenuModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class Courses : AppCompatActivity() {

    private lateinit var ivSearch: ImageView
    private lateinit var etSearch: EditText
    private lateinit var ivSearchCancel: ImageView
    private lateinit var sv: SearchView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabLink: FloatingActionButton
    private lateinit var pbLoading: ProgressBar
    private lateinit var backButton: ImageView
    private lateinit var rvCourses: RecyclerView
    private var coursesList = ArrayList<CourseMenuModel>()
    private var getCoursesList: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(anim.anim_fade_in_activities, anim.anim_fade_out_activities)
        setContentView(R.layout.activity_courses)

        init()

        fabAdd.setOnClickListener {
            val intent = Intent(this, CreateCourseActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        fabLink.hide()

        ivSearch.setOnClickListener {
            ivSearch.visibility = View.GONE
            ivSearchCancel.visibility = View.VISIBLE
            etSearch.visibility = View.VISIBLE
        }

        etSearch.addTextChangedListener() {
            coursesList.clear()
            getCourseList()
        }

        ivSearchCancel.setOnClickListener {

            etSearch.text.clear()
            ivSearch.visibility = View.VISIBLE
            ivSearchCancel.visibility = View.GONE
            etSearch.visibility = View.GONE

        }

        backButton.setOnClickListener {
            finish()
        }

        rvCourses.layoutManager = GridLayoutManager(this, 1)
        rvCourses.adapter = CourseMenuRvAdapter(
            this@Courses,
            coursesList
        )
    }

    private fun getCourseList() {
        pbLoading.visibility = View.VISIBLE
        rvCourses.visibility = View.GONE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@Courses).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("searching_text", etSearch.text.trim().toString())
        jsonObject.put("status", jaStatus)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getCourseList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_COURSE_LIST).post(body.build())
            .build()
        getCoursesList = client.newCall(request)
        getCoursesList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getCourseList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@Courses,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@Courses,
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
                            this@Courses,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getCourseList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            coursesList.clear()
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (i in 0 until list.length()) {
                                val jo = list.getJSONObject(i)
                                val id = jo.getString("_id")
                                val name = jo.getString("_name")
                                val semCount = jo.getInt("_sem_count")
                                val status = jo.getInt("_status")
                                val departmentDetails = jo.getJSONObject("departmentDetails")
                                val departmentStatus = departmentDetails.getInt("_status")
                                var joId = "nil"
                                var joName = "nil"
                                if (departmentStatus == 1) {
                                    joId = departmentDetails.getString("_id")
                                    joName =
                                        departmentDetails.getString("_name")
                                    coursesList.add(
                                        CourseMenuModel(
                                            id,
                                            name,
                                            joName,
                                            joId,
                                            semCount,
                                            status
                                        )
                                    )
                                }
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rvCourses.layoutManager =
                                    LinearLayoutManager(this@Courses)
                                rvCourses.adapter = CourseMenuRvAdapter(
                                    this@Courses,
                                    coursesList
                                )
                                rvCourses.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getCourseList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@Courses, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@Courses,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@Courses,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }

    override fun onResume() {

        //Search
        ivSearchCancel.visibility = View.GONE
        etSearch.text.clear()
        etSearch.visibility = View.GONE

        super.onResume()
        coursesList.clear()
        getCourseList()
    }


    private fun init() {
        fabAdd = findViewById(R.id.fabAdd)
        fabLink = findViewById(R.id.fabLink)
        backButton = findViewById(R.id.backBtn)
        rvCourses = findViewById(R.id.rvCourses)
        pbLoading = findViewById(R.id.pbLoading)
        ivSearch = findViewById(R.id.ivSearch)
        etSearch = findViewById(R.id.etSearch)
        ivSearchCancel = findViewById(R.id.ivSearchCancel)
    }
}