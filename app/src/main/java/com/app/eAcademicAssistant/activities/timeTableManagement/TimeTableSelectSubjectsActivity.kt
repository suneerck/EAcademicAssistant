package com.app.eAcademicAssistant.activities.timeTableManagement

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.PeriodSubListDialogueRvAdapter
import com.app.eAcademicAssistant.adapters.SemSubLinkRvMainAdapter
import com.app.eAcademicAssistant.model.DayModel2
import com.app.eAcademicAssistant.model.PeriodModel
import com.app.eAcademicAssistant.model.SemCourseMenuModel
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class TimeTableSelectSubjectsActivity : AppCompatActivity() {

    private lateinit var tvSubmit: TextView
    private lateinit var backBtn: ImageView
    private lateinit var rv: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var pbLoading: ProgressBar
    private var days = ArrayList<DayModel2>()
    private var getUsersList: Call? = null
    lateinit var semId: String
    lateinit var courseId: String
    private var getSubjectsList: Call? = null
    private var subjectsList = ArrayList<SemCourseMenuModel>()
    var subPeriodList = ArrayList<PeriodModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table_select_subjects)

        init()

        days = intent.getParcelableArrayListExtra<DayModel2>("days") ?: ArrayList<DayModel2>()
        semId = intent.getStringExtra("sem_id").toString()
        courseId = intent.getStringExtra("course_id").toString()

        backBtn.setOnClickListener {
            finish()
        }

        tvSubmit.setOnClickListener {
            linkSubjectSemDay()
        }

        fabAdd.setOnClickListener {
            subjectsList.clear()
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)

            val v = LayoutInflater.from(this).inflate(R.layout.layout_dialog_add_semester, null)
            val clParent = v.findViewById<ConstraintLayout>(R.id.clParent)
            val rv1 = v.findViewById<RecyclerView>(R.id.rv)
            val pbLoading1 = v.findViewById<ProgressBar>(R.id.pbLoading)
            val ivSearch = v.findViewById<ImageView>(R.id.ivSearch)
            val ivSearchCancel = v.findViewById<ImageView>(R.id.ivSearchCancel)
            val etSearch = v.findViewById<EditText>(R.id.etSearch)

            getSubjectsList(pbLoading1, rv1, dialog)


            ivSearch.visibility = View.GONE

            etSearch.visibility = View.GONE

            ivSearchCancel.visibility = View.GONE

            clParent.setOnClickListener {
                dialog.cancel()
            }
            dialog.setContentView(v)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }

        listSubjects()

    }

    fun listSubjects() {
        pbLoading.visibility = View.GONE
        rv.visibility = View.VISIBLE
        rv.layoutManager =
            LinearLayoutManager(this@TimeTableSelectSubjectsActivity)
        rv.adapter = SemSubLinkRvMainAdapter(
            this@TimeTableSelectSubjectsActivity,
            subPeriodList
        )
    }

    override fun onResume() {
        super.onResume()
        listSubjects()
    }

    private fun getSubjectsList(
        pbLoading1: ProgressBar,
        rv1: RecyclerView,
        dialog: Dialog
    ) {
        this.pbLoading.visibility = View.VISIBLE
        this.rv.visibility = View.GONE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@TimeTableSelectSubjectsActivity).getUser()?.userId
        )
        jsonObject.put("courseId", courseId)
        jsonObject.put("semId", semId)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getSubjectsList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_SUBJECT_LIST).post(body.build())
            .build()
        getSubjectsList = client.newCall(request)
        getSubjectsList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getSubjectsList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableSelectSubjectsActivity,
                            getString(R.string.no_internet_connection)
                        )
                        this@TimeTableSelectSubjectsActivity.pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableSelectSubjectsActivity,
                            getString(R.string.something_went_wrong)
                        )
                        this@TimeTableSelectSubjectsActivity.pbLoading.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@TimeTableSelectSubjectsActivity.pbLoading.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@TimeTableSelectSubjectsActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getSubjectsList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            subjectsList.clear()
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (j in 0 until list.length()) {
                                val joList = list.getJSONObject(j)
                                val jaSubList = joList.getJSONObject("subjectDetails")
                                val joSemCourseId = joList.getString("_id")
                                val id = jaSubList.getString("_id")
                                val name = jaSubList.getString("_name")
                                val status = jaSubList.getInt("_status")
                                subjectsList.add(
                                    SemCourseMenuModel(
                                        joSemCourseId,
                                        id,
                                        semId,
                                        name,
                                        courseId,
                                        status
                                    )
                                )
                            }
                            runOnUiThread {

                                pbLoading1.visibility = View.GONE
                                rv1.layoutManager =
                                    LinearLayoutManager(this@TimeTableSelectSubjectsActivity)
                                rv1.adapter = PeriodSubListDialogueRvAdapter(
                                    this@TimeTableSelectSubjectsActivity,
                                    subjectsList, dialog, pbLoading1
                                )
                                rv1.visibility = View.VISIBLE

                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getSubjectsList:$message")
                            runOnUiThread {
                                this@TimeTableSelectSubjectsActivity.pbLoading.visibility =
                                    View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@TimeTableSelectSubjectsActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSubjectsList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TimeTableSelectSubjectsActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                this@TimeTableSelectSubjectsActivity.pbLoading.visibility =
                                    View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSubjectsList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableSelectSubjectsActivity,
                            getString(R.string.error_message_server_error)
                        )
                        this@TimeTableSelectSubjectsActivity.pbLoading.visibility = View.GONE
                    }
                }
            }
        })


    }


    private fun linkSubjectSemDay() {
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        val arraySubject = JSONArray()
        val jaSemDayLinkId = JSONArray()
        for (i in 0 until subPeriodList.size) {
            val joSubject = JSONObject()
            joSubject.put(
                "subjectId",
                subPeriodList[i].subId
            )
            joSubject.put(
                "priority",
                i+1
            )
            arraySubject.put(
                joSubject
            )
        }

        for (i in 0 until days.size) {
            if (days[i].isWorkingDay == 3) {
                jaSemDayLinkId.put(
                    days[i].id
                )
            }
        }
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@TimeTableSelectSubjectsActivity).getUser()?.userId
        )
        jsonObject.put("arraySubject", arraySubject)
        jsonObject.put("semDayLinkingIds", jaSemDayLinkId)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getCourseList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_DAY_PERIOD_LINKING).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getCourseList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableSelectSubjectsActivity,
                            getString(R.string.no_internet_connection)
                        )
                        this@TimeTableSelectSubjectsActivity.pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableSelectSubjectsActivity,
                            getString(R.string.something_went_wrong)
                        )
                        this@TimeTableSelectSubjectsActivity.pbLoading.visibility = View.GONE
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        this@TimeTableSelectSubjectsActivity.pbLoading.visibility = View.GONE
                        SnackBarUtils.showSnackBar(
                            this@TimeTableSelectSubjectsActivity,
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

                            runOnUiThread {
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getCourseList:$message")
                            runOnUiThread {
                                this@TimeTableSelectSubjectsActivity.pbLoading.visibility =
                                    View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@TimeTableSelectSubjectsActivity,
                                    message
                                )

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getCourseList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@TimeTableSelectSubjectsActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                this@TimeTableSelectSubjectsActivity.pbLoading.visibility =
                                    View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getCourseList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@TimeTableSelectSubjectsActivity,
                            getString(R.string.error_message_server_error)
                        )
                        this@TimeTableSelectSubjectsActivity.pbLoading.visibility = View.GONE
                    }
                }
            }
        })

    }


    private fun init() {
        rv = findViewById(R.id.rvMain)
        backBtn = findViewById(R.id.backBtn)
        pbLoading = findViewById(R.id.pbLoading)
        tvSubmit = findViewById(R.id.tvSubmit)
        fabAdd = findViewById(R.id.fabAdd)
    }
}