package com.app.eAcademicAssistant.activities.coursesManagement.semester

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.SnackBarUtils
import com.app.eAcademicAssistant.utills.URLUtils
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class EditSemester : AppCompatActivity() {

    private lateinit var etSemName: EditText
    private lateinit var btnSubmit: Button
    private lateinit var ivBackBtn: ImageView
    private lateinit var ivDelete: ImageView
    private var doLogin: Call? = null
    lateinit var semId: String

    var keySemId = ""
    var keySemName = ""
    var keyStatus = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_edit_semester)

        init()

        keySemId = intent.getStringExtra("key_sem_id").toString()
        keySemName = intent.getStringExtra("key_sem_name").toString()
        keyStatus = intent.getIntExtra("key_status", 1)

        semId = keySemId

        etSemName.setText(keySemName)

        ivDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete..!?")
            builder.setMessage("Are you sure to delete $keySemName .")
            builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                semesterStatusChange()
            }
            builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

        ivBackBtn.setOnClickListener {

            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            intent.putExtra("key_sem_id", keySemId)
            intent.putExtra("key_sem_name", etSemName.text.trim().toString())
            intent.putExtra("key_status", keyStatus)
            finish()
        }

        btnSubmit.setOnClickListener {
            val semesterName = etSemName.text.trim().toString()
            if (semesterName.isEmpty()) {
                Toast.makeText(this, "Please enter semester", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                editSemester()

            }

        }

    }

    private fun init() {
        etSemName = findViewById(R.id.etSemName)
        btnSubmit = findViewById(R.id.btnSubmit)
        ivBackBtn = findViewById(R.id.ivBackBtn)
        ivDelete = findViewById(R.id.ivDelete)
    }

    @SuppressLint("SetTextI18n")
    private fun semesterStatusChange() {
        btnSubmit.isEnabled = false
        btnSubmit.text = "Loading.."

        val jsonObject = JSONObject()
        jsonObject.put("userId", DatabaseUtils.getInstance(this@EditSemester).getUser()?.userId)
        jsonObject.put("linkingId", semId)
        jsonObject.put("status", 2)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "semesterStatusChange : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEMESTER_STATUS_CHANGE).post(body.build())
            .build()
        doLogin = client.newCall(request)
        doLogin?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "semesterStatusChange")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditSemester,
                            getString(R.string.no_internet_connection)
                        )
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Login"
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditSemester,
                            getString(R.string.something_went_wrong)
                        )
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Login"
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Login"
                        SnackBarUtils.showSnackBar(
                            this@EditSemester,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "semesterStatusChange:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@EditSemester,
                                    "Deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Toast.makeText(
                                this@EditSemester,
                                "An error occurred ..!!",
                                Toast.LENGTH_SHORT
                            ).show()

                            Log.e("_______resp_error", "semesterStatusChange:$message")
                            runOnUiThread {
                                btnSubmit.text = "Login"
                                btnSubmit.isEnabled = true
                                SnackBarUtils.showSnackBar(this@EditSemester, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "semesterStatusChange")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@EditSemester,
                                    getString(R.string.error_message_server_error)
                                )
                                btnSubmit.text = "Login"
                                btnSubmit.isEnabled = true

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "semesterStatusChange" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditSemester,
                            getString(R.string.error_message_server_error)
                        )
                        btnSubmit.text = "Login"
                        btnSubmit.isEnabled = true
                    }
                }
            }
        })
    }


    private fun editSemester() {
        btnSubmit.isEnabled = false
        btnSubmit.text = "Loading.."
        val semName = etSemName.text.trim().toString()

        val jsonObject = JSONObject()
        jsonObject.put("userId", DatabaseUtils.getInstance(this@EditSemester).getUser()?.userId)
        jsonObject.put("semId", semId)
        jsonObject.put("name", semName)
        val data = JSONObject()
        data.put("data", jsonObject)
        Log.e("_____________req", "editSemester : $jsonObject")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val client = OkHttpUtils.getOkHttpClient()
        val request = Request.Builder()
            .url(URLUtils.URL_SEM_EDIT).post(body.build())
            .build()
        doLogin = client.newCall(request)
        doLogin?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("___________onFailure", "editSemester")
                if (e is UnknownHostException) {
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditSemester,
                            getString(R.string.no_internet_connection)
                        )
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Login"
                    }
                } else {

                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditSemester,
                            getString(R.string.something_went_wrong)
                        )
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Login"
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled) {
                    runOnUiThread {
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Login"
                        SnackBarUtils.showSnackBar(
                            this@EditSemester,
                            getString(R.string.error_message_connect_error)
                        )

                    }

                }
                val resp = response.body()?.string()
                Log.e("______________resp", "editSemester:$resp")
                try {
                    val jo = JSONObject(resp)
                    when (response.code()) {
                        OkHttpUtils.STATUS_OK -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@EditSemester,
                                    "Registration Successfully Completed",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent()

                                intent.putExtra("key_sem_id", keySemId)
                                intent.putExtra("key_sem_name", etSemName.text.trim().toString())
                                intent.putExtra("key_status", 1)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                        }
                        OkHttpUtils.STATUS_ERROR -> {
                            val message = jo.getString("message")
                            Toast.makeText(
                                this@EditSemester,
                                "An error occured ..!!",
                                Toast.LENGTH_SHORT
                            ).show()

                            Log.e("_______resp_error", "editSemester:$message")
                            runOnUiThread {
                                btnSubmit.text = "Login"
                                btnSubmit.isEnabled = true
                                SnackBarUtils.showSnackBar(this@EditSemester, message)

                            }


                        }
                        else -> {
                            Log.e("_______Internal_error", "editSemester")
                            runOnUiThread {
                                SnackBarUtils.showSnackBar(
                                    this@EditSemester,
                                    getString(R.string.error_message_server_error)
                                )
                                btnSubmit.text = "Login"
                                btnSubmit.isEnabled = true

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("__________exc", "editSemester" + e.message)
                    runOnUiThread {
                        SnackBarUtils.showSnackBar(
                            this@EditSemester,
                            getString(R.string.error_message_server_error)
                        )
                        btnSubmit.text = "Login"
                        btnSubmit.isEnabled = true
                    }
                }
            }
        })
    }

}