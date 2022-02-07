package com.app.eAcademicAssistant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class UserMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities,R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_user_main)

        val skip = findViewById<TextView>(R.id.umSkip)
        skip.setOnClickListener{
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val login = findViewById<Button>(R.id.umLogin)
        login.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val fUser = findViewById<Button>(R.id.umFUser)
        fUser.setOnClickListener {
            intent = Intent(this, FirstTimeLoginUser::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}