package com.app.eAcademicAssistant.activities.coursesManagement.courses

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.SemSubjectLinkSubjectMenuRvAdapter
import com.app.eAcademicAssistant.adapters.SemSubjectLinkSubjectsListRvAdapter
import com.app.eAcademicAssistant.model.SemCourseMenuModel
import com.app.eAcademicAssistant.model.SemesterMenuModel
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

@Suppress("DEPRECATION")
class SemSubjectLinkingActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var rv: RecyclerView
    private lateinit var tvCourseName: TextView
    private var subjectsList = ArrayList<SemCourseMenuModel>()
    private var newSubjectList = ArrayList<SemesterMenuModel>()
    private lateinit var tvSemName: TextView
    private lateinit var pbLoading: ProgressBar
    private var getSubjectsList: Call? = null
    var linkSemSubject: Call? = null
    lateinit var semId: String
    lateinit var courseId: String
    lateinit var courseName: String
    var selectedSubjectId = "nil"
    var selectedSubjectName = "nil"
    var semCourseLinkId = "nil"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sem_subject_linking)

        init()

        backButton.setOnClickListener {
            finish()
        }

        val keySemCourseId = intent.getStringExtra("key_sem_course_id")
        val keySemId = intent.getStringExtra("key_sem_id")
        val keySemName = intent.getStringExtra("key_sem_name")
        val keyCourseId = intent.getStringExtra("key_course_id")
        val keyCourseName = intent.getStringExtra("key_course_name")
        val keyStatus = intent.getStringExtra("key_status")

        semId = keySemId.toString()
        courseId = keyCourseId.toString()
        courseName = keyCourseName.toString()
        val semName = keySemName.toString()
        semCourseLinkId = keySemCourseId.toString()

        tvCourseName.text = courseName
        tvSemName.text = semName

        getSubjectsList()

        fabAdd.setOnClickListener {
            newSubjectList.clear()
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)

            val v = LayoutInflater.from(this).inflate(R.layout.layout_dialog_add_semester, null)
            val clParent = v.findViewById<ConstraintLayout>(R.id.clParent)
            val rv = v.findViewById<RecyclerView>(R.id.rv)
            val pbLoading = v.findViewById<ProgressBar>(R.id.pbLoading)
            val ivSearch = v.findViewById<ImageView>(R.id.ivSearch)
            val ivSearchCancel = v.findViewById<ImageView>(R.id.ivSearchCancel)
            val etSearch = v.findViewById<EditText>(R.id.etSearch)

            var sText = ""
            getNewSubjectList(pbLoading, rv, dialog, sText)


            ivSearch.setOnClickListener {
                ivSearch.visibility = View.GONE
                ivSearchCancel.visibility = View.VISIBLE
                etSearch.visibility = View.VISIBLE
            }

            etSearch.addTextChangedListener() {
                sText = etSearch.text.trim().toString()
                getNewSubjectList(pbLoading, rv, dialog, sText)
            }

            ivSearchCancel.setOnClickListener {
                etSearch.text.clear()
                ivSearch.visibility = View.VISIBLE
                ivSearchCancel.visibility = View.GONE
                etSearch.visibility = View.GONE
            }

            clParent.setOnClickListener {
                dialog.cancel()
            }
            dialog.setContentView(v)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }

    }

    private fun init() {
        backButton = findViewById(R.id.backBtn)
        rv = findViewById(R.id.rv)
        fabAdd = findViewById(R.id.fabAdd)
        pbLoading = findViewById(R.id.pbLoading)
        tvCourseName = findViewById(R.id.tvCourseName)
        tvSemName = findViewById(R.id.tvSemName)
    }

    private fun getNewSubjectList(
        pbLoading: ProgressBar,
        rv: RecyclerView,
        dialog: Dialog,
        sText: String
    ) {
        newSubjectList.clear()
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@SemSubjectLinkingActivity).getUser()?.userId
        )
        jsonObject.put("skip", -1)
        jsonObject.put("status", jaStatus)
        jsonObject.put("searching_text", sText)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getNewSubjectList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SUBJECT_LIST).post(body.build())
            .build()
        getSubjectsList = client.newCall(request)
        getSubjectsList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getNewSubjectList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
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
                            this@SemSubjectLinkingActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }

                val resp = response.body()?.string()
                Log.e("______________resp", "getNewSubjectList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            newSubjectList.clear()
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (i in 0 until list.length()) {
                                val jo = list.getJSONObject(i)
                                val id = jo.getString("_id")
                                val name = jo.getString("_name")
                                val status = jo.getInt("_status")

                                newSubjectList.add(
                                    SemesterMenuModel(
                                        id,
                                        name,
                                        status
                                    )
                                )

                            }

                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@SemSubjectLinkingActivity)
                                rv.adapter = SemSubjectLinkSubjectMenuRvAdapter(
                                    this@SemSubjectLinkingActivity,
                                    newSubjectList, dialog, pbLoading
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getNewSubjectList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@SemSubjectLinkingActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getNewSubjectList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@SemSubjectLinkingActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getNewSubjectList " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }

    fun getSubjectsList() {
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@SemSubjectLinkingActivity).getUser()?.userId
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
                            this@SemSubjectLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
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
                            this@SemSubjectLinkingActivity,
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
                                        courseId,
                                        name,
                                        courseName,
                                        status
                                    )
                                )
                            }
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@SemSubjectLinkingActivity)
                                rv.adapter = SemSubjectLinkSubjectsListRvAdapter(
                                    this@SemSubjectLinkingActivity,
                                    subjectsList
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getSubjectsList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@SemSubjectLinkingActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSubjectsList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@SemSubjectLinkingActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSubjectsList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })


    }

    fun changeSubjectStatus(idSemSub: String) {
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@SemSubjectLinkingActivity).getUser()?.userId
        )
        jsonObject.put("linkingId", idSemSub)
        jsonObject.put("status", 2)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getSubjectsList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEMESTER_SUBJECT_LINKING_STATUS_CHANGE).post(body.build())
            .build()
        getSubjectsList = client.newCall(request)
        getSubjectsList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getSubjectsList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
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
                            this@SemSubjectLinkingActivity,
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

                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                Toast.makeText(
                                    this@SemSubjectLinkingActivity,
                                    "Deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                subjectsList.clear()
                                getSubjectsList()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getSubjectsList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@SemSubjectLinkingActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSubjectsList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@SemSubjectLinkingActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSubjectsList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })


    }


    fun linkSemSubject() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val a = JSONArray()
        a.put(selectedSubjectId)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@SemSubjectLinkingActivity).getUser()?.userId
        )
        jsonObject.put("semCourseLinkingId", semCourseLinkId)
        jsonObject.put("subjectIds", a)

        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "addSem : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_SUBJECT_ID_LINKING).post(body.build())
            .build()

        linkSemSubject = client.newCall(request)
        linkSemSubject?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "linkSemSubject")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
                            getString(R.string.no_internet_connection)
                        )
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
                            getString(R.string.something_went_wrong)
                        )
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "linkSemSubject:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@SemSubjectLinkingActivity,
                                    getString(R.string.linking_successfully)
                                )
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "linkSemSubject:$message")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(this@SemSubjectLinkingActivity, message)
                                pbLoading.visibility = View.GONE
                            }

                        }
                        else -> {
                            Log.e("_______Internal_error", "linkSemSubject")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@SemSubjectLinkingActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "linkSemSubject " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@SemSubjectLinkingActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
        subjectsList.clear()
    }


}