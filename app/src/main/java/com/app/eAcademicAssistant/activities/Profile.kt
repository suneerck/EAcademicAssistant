package com.app.eAcademicAssistant.activities

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.objects.MenuConstants
import com.app.eAcademicAssistant.utills.DatabaseUtils

class Profile : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var ivLogOut: ImageView
    private lateinit var ivBackBtn: ImageView
    private lateinit var tvEmail: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvUserType: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        init()

        ivBackBtn.setOnClickListener {
            finish()
        }

        val user = DatabaseUtils.getInstance(this@Profile).getUser()
        tvName.text = user?.name
        tvEmail.text = user?.email
        tvGender.text = when (user?.gender) {
            MenuConstants.MALE -> {
                getString(R.string.male)
            }
            MenuConstants.FEMALE -> {
                getString(R.string.female)
            }
            else -> {
                getString(R.string.other)
            }
        }

        tvUserType.text = when (user?.userType){
            MenuConstants.ADMIN -> {
                getString(R.string.admin)
            }
            MenuConstants.DEPARTMENT_INCHARGE -> {
                getString(R.string.department_in_charge)
            }
            MenuConstants.TEACHER -> {
                getString(R.string.teacher)
            }
            else -> {
                getString(R.string.student)
            }
        }

        ivLogOut.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout..!?")
            builder.setMessage(R.string.LogoutDialogue)
            builder.setPositiveButton("Logout") { dialogInterface: DialogInterface, i: Int ->
                DatabaseUtils.getInstance(this@Profile).clearUserTable()
                finish()
            }
            builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun init() {
        tvName = findViewById(R.id.tvName)
        ivBackBtn = findViewById(R.id.ivBackBtn)
        ivLogOut = findViewById(R.id.ivLogOut)
        tvEmail = findViewById(R.id.tvEmail)
        tvGender = findViewById(R.id.tvGender)
        tvUserType = findViewById(R.id.tvUserType)

    }
}