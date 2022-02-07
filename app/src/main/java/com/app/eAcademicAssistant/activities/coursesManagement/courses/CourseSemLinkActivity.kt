package com.app.eAcademicAssistant.activities.coursesManagement.courses

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
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.adapters.SemCourseLinkMenuRvAdapter
import com.app.eAcademicAssistant.adapters.SemCourseLinkSemesterMenuRvAdapter
import com.app.eAcademicAssistant.model.SemCourseMenuModel
import com.app.eAcademicAssistant.model.SemesterMenuModel
import com.app.eAcademicAssistant.model.srModel
import com.app.eAcademicAssistant.objects.MenuConstants
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

class CourseSemLinkActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var rv : RecyclerView
    private lateinit var tvCourseName : TextView
    private lateinit var pbLoading: ProgressBar
    private var semestersList = ArrayList<SemCourseMenuModel>()
    private var newSemesterList = ArrayList<SemesterMenuModel>()
    private var getUsersList: Call? = null
    private val srList = ArrayList<srModel>()
    private var selectedCourseId = MenuConstants.NIL
    private var getNewSemesterList: Call? = null
    private var linkSemCourse: Call? = null
    var selectedSemId = "nil"
    var selectedSemName = "nil"
    lateinit var courseId :String
    lateinit var courseName :String
    lateinit var semCourseId :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_sem_link)

        init()


        val keyCourseId =  intent.getStringExtra("key_course_id")
        val keyCourseName=  intent.getStringExtra("key_course_name")
        var keyDepartmentId=  intent.getStringExtra("key_department_id")
        val keyDepartmentName=  intent.getStringExtra("key_department_name")
        val keySemCount =  intent.getStringExtra("key_sem_count")
        var keyStatus=  intent.getStringExtra("key_status")

        selectedCourseId = keyCourseId.toString()
        tvCourseName.text = keyCourseName

        courseId = keyCourseId.toString()
        courseName = keyCourseName.toString()


//        sCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//
//                selectedCourseId = srList[position].id
//                if (selectedCourseId!= MenuConstants.NIL){
//                    semestersList.clear()
//                    getSemList()
//                    fabAdd.visibility= View.VISIBLE
//                }
//
//
//            }
//        }

        fabAdd.setOnClickListener{
            newSemesterList.clear()
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)
            val v = LayoutInflater.from(this)
                .inflate(R.layout.layout_dialog_add_semester, null)
            val clParent = v.findViewById<ConstraintLayout>(R.id.clParent)
            val rv = v.findViewById<RecyclerView>(R.id.rv)
            val pbLoading = v.findViewById<ProgressBar>(R.id.pbLoading)
            val ivSearch = v.findViewById<ImageView>(R.id.ivSearch)
            val ivSearchCancel = v.findViewById<ImageView>(R.id.ivSearchCancel)
            val etSearch = v.findViewById<EditText>(R.id.etSearch)
            var sText = ""
            getNewSemList(pbLoading, rv, dialog, sText)

            ivSearch.setOnClickListener {
                ivSearch.visibility = View.GONE
                ivSearchCancel.visibility = View.VISIBLE
                etSearch.visibility = View.VISIBLE
            }

            etSearch.addTextChangedListener() {
                sText = etSearch.text.trim().toString()
                getNewSemList(pbLoading, rv, dialog, sText)
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


        backButton.setOnClickListener{
            finish()
        }
    }

    private fun getNewSemList(pbLoading: ProgressBar, rv: RecyclerView, dialog: Dialog, sText : String) {
        newSemesterList.clear()
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put("userId", DatabaseUtils.getInstance(this@CourseSemLinkActivity).getUser()?.userId)
        jsonObject.put("skip", -1)
        jsonObject.put("searching_text", sText)
        jsonObject.put("status", jaStatus)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getNewSemList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_LIST).post(body.build())
            .build()
        getNewSemesterList = client.newCall(request)
        getNewSemesterList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getNewSemList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
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
                            this@CourseSemLinkActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }

                val resp = response.body()?.string()
                Log.e("______________resp", "getNewSemList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            newSemesterList.clear()
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (i in 0 until list.length()) {
                                val jo = list.getJSONObject(i)
                                val id = jo.getString("_id")
                                val name = jo.getString("_name")
                                val status = jo.getInt("_status")

                                newSemesterList.add(
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
                                    LinearLayoutManager(this@CourseSemLinkActivity)
                                rv.adapter = SemCourseLinkSemesterMenuRvAdapter(
                                    this@CourseSemLinkActivity,
                                    newSemesterList, dialog, pbLoading
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getNewSemList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@CourseSemLinkActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getNewSemList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@CourseSemLinkActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getNewSemList " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }

//    override fun onResume() {
//        super.onResume()
//        srList.clear()
//        semestersList.clear()
////        getCourseList()
//    }

    fun linkSemCourse() {
        pbLoading.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val a= JSONArray()
        a.put(selectedSemId)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@CourseSemLinkActivity).getUser()?.userId
        )
        jsonObject.put("semId", a)
        jsonObject.put("courseId", selectedCourseId)

        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "addSem : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_COURSE_LINKING).post(body.build())
            .build()

        linkSemCourse = client.newCall(request)
        linkSemCourse?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "addSem")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
                            getString(R.string.no_internet_connection)
                        )
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
                            getString(R.string.something_went_wrong)
                        )
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "addSem:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(
                                    this@CourseSemLinkActivity,
                                    getString(R.string.linking_successfully)
                                )
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "addSem:$message")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(this@CourseSemLinkActivity, message)
                                pbLoading.visibility = View.GONE
                            }

                        }
                        else -> {
                            Log.e("_______Internal_error", "addSem")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@CourseSemLinkActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "addSem " + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
        semestersList.clear()
    }

//    private fun getCourseList() {
//        pbLoading.visibility = View.VISIBLE
//        rv.visibility = View.GONE
//        val jsonObject = JSONObject()
//        val jaStatus = JSONArray()
//        jaStatus.put(1)
//        jaStatus.put(2)
//        jsonObject.put(
//            "userId",
//            DatabaseUtils.getInstance(this@CourseSemLinkActivity).getUser()?.userId
//        )
//        jsonObject.put("skip", -1)
//        jsonObject.put("searching_text", "")
//        jsonObject.put("status", jaStatus)
//        val data = JSONObject()
//        data.put("data", jsonObject)
//        Log.e("_____________req", "usersList : $jsonObject")
//
//        val body = MultipartBody.Builder()
//        body.setType(MultipartBody.FORM)
//        body.addFormDataPart("json_data", data.toString())
//
//        val client = OkHttpUtils.getOkHttpClient()
//        val request = Request.Builder()
//            .url(URLUtils.URL_COURSE_LIST).post(body.build())
//            .build()
//        getUsersList = client.newCall(request)
//        getUsersList?.enqueue(object : Callback {
//            override fun onFailure(call: Call?, e: IOException?) {
//                Log.e("___________onFailure", "getCourseList")
//                if (e is UnknownHostException) {
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CourseSemLinkActivity,
//                            getString(R.string.no_internet_connection)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                } else {
//
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CourseSemLinkActivity,
//                            getString(R.string.something_went_wrong)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                }
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                if (call.isCanceled) {
//                    runOnUiThread {
//                        pbLoading.visibility = View.GONE
//                        SnackBarUtils.showSnackBar(
//                            this@CourseSemLinkActivity,
//                            getString(R.string.error_message_connect_error)
//                        )
//
//                    }
//
//                }
//                val resp = response.body()?.string()
//                Log.e("______________resp", "usersList:$resp")
//                try {
//                    val jo = JSONObject(resp)
//                    when (response.code()) {
//                        OkHttpUtils.STATUS_OK -> {
//                            srList.add(
//                                srModel(
//                                    MenuConstants.NIL,"Please select a course"
//                                )
//                            )
//                            val joData = jo.getJSONObject("data")
//                            val list = joData.getJSONArray("list")
//                            for (i in 0 until list.length()) {
//                                val jo = list.getJSONObject(i)
//                                val id = jo.getString("_id")
//                                val name = jo.getString("_name")
//                                val semCount = jo.getInt("_sem_count")
//                                val status = jo.getInt("_status")
//
//                                srList.add(
//                                    srModel(
//                                        id,
//                                        name
//                                    )
//                                )
//                            }
//                            runOnUiThread {
//                                pbLoading.visibility = View.GONE
////                                sCourse.adapter = srAdapter(this@CourseSemLinkActivity, srList)
//                            }
//                        }
//                        OkHttpUtils.STATUS_ERROR -> {
//                            val message = jo.getString("message")
//                            Log.e("_______resp_error", "usersList:$message")
//                            runOnUiThread {
//                                pbLoading.visibility = View.GONE
//                                SnackBarUtils.showSnackBar(this@CourseSemLinkActivity, message)
//
//                            }
//
//
//                        }
//                        else -> {
//                            Log.e("_______Internal_error", "usersList")
//                            runOnUiThread {
//                                SnackBarUtils.showSnackBar(
//                                    this@CourseSemLinkActivity,
//                                    getString(R.string.error_message_server_error)
//                                )
//                                pbLoading.visibility = View.GONE
//
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.e("__________exc", "usersList" + e.message)
//                    runOnUiThread {
//                        SnackBarUtils.showSnackBar(
//                            this@CourseSemLinkActivity,
//                            getString(R.string.error_message_server_error)
//                        )
//                        pbLoading.visibility = View.GONE
//                    }
//                }
//            }
//        })
//
//    }

    fun semStatusChange(semCourseId2: String) {
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@CourseSemLinkActivity).getUser()?.userId
        )
        jsonObject.put("linkingId", semCourseId2)
        jsonObject.put("status", 2)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "semStatusChange : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_COURSE_LINKING_STATUS_CHANGE).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "semStatusChange")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
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
                            this@CourseSemLinkActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "semStatusChange:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            SnackBarUtils.showSnackBar(
                                this@CourseSemLinkActivity,
                                "Deleted Successfully"
                            )
                            semestersList.clear()
                            getSemList()
                            runOnUiThread {
                                Log.e("______________resp", "semStatusChange : Success")
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "semStatusChange:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@CourseSemLinkActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "semStatusChange")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@CourseSemLinkActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "semStatusChange" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
                            getString(R.string.error_message_server_error)
                        )
                        pbLoading.visibility = View.GONE
                    }
                }
            }
        })
    }


    fun getSemList() {
        pbLoading.visibility = View.VISIBLE
        rv.visibility = View.GONE
        val jsonObject = JSONObject()
        val jaStatus = JSONArray()
        jaStatus.put(1)
        jsonObject.put(
            "userId",
            DatabaseUtils.getInstance(this@CourseSemLinkActivity).getUser()?.userId
        )
        jsonObject.put("courseId", selectedCourseId)
        jsonObject.put("skip", -1)
        jsonObject.put("status", jaStatus)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "getSemList : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_COURSE_LIST).post(body.build())
            .build()
        getUsersList = client.newCall(request)
        getUsersList?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "getSemList")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
                            getString(R.string.no_internet_connection)
                        )
                        pbLoading.visibility = View.GONE
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
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
                            this@CourseSemLinkActivity,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "getSemList:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            val joData = jo.getJSONObject("data")
                            val list = joData.getJSONArray("list")
                            for (j in 0 until list.length()) {
                                val joList = list.getJSONObject(j)
                                val jaSemList = joList.getJSONArray("semList")
                                val semCourseStatus = joList.getInt("_status")
                                if (semCourseStatus == 1) {
                                    semCourseId = joList.getString("_id")
                                    for (i in 0 until jaSemList.length()) {
                                        val joSemList = jaSemList.getJSONObject(i)
                                        val id = joSemList.getString("_id")
                                        val name = joSemList.getString("_name")
                                        val status = joSemList.getInt("_status")
                                        if (status == 1) {
                                            semestersList.add(
                                                SemCourseMenuModel(
                                                    semCourseId,
                                                    id,
                                                    courseId,
                                                    name,
                                                    courseName,
                                                    semCourseStatus
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            runOnUiThread {
                                Log.e("______________resp", "getSemList :$")
                                pbLoading.visibility = View.GONE
                                rv.layoutManager =
                                    LinearLayoutManager(this@CourseSemLinkActivity)
                                rv.adapter = SemCourseLinkMenuRvAdapter(
                                    this@CourseSemLinkActivity,
                                    semestersList
                                )
                                rv.visibility = View.VISIBLE
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Log.e("_______resp_error", "getSemList:$message")
                            runOnUiThread {
                                pbLoading.visibility = View.GONE
                                SnackBarUtils.showSnackBar(this@CourseSemLinkActivity, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "getSemList")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@CourseSemLinkActivity,
                                    getString(R.string.error_message_server_error)
                                )
                                pbLoading.visibility = View.GONE

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "getSemList" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@CourseSemLinkActivity,
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
        rv = findViewById(R.id.rv)
        fabAdd = findViewById(R.id.fabAdd)
        pbLoading = findViewById(R.id.pbLoading)
        tvCourseName = findViewById(R.id.tvCourseName)
//        sCourse = findViewById(R.id.sCourse)
    }

    override fun onResume() {
        super.onResume()
        semestersList.clear()
        getSemList()
    }
}