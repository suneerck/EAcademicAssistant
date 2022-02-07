package com.app.eAcademicAssistant.staticActivities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.SplashActivity

class CollegeDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_college_details)

        val admissionDetails = findViewById<TextView>(R.id.admissionDetails)
        admissionDetails.setOnClickListener {
            intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }

        val courseDetails = findViewById<TextView>(R.id.coursesDetails)
        courseDetails.setOnClickListener {
            intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }

        val btn1 = findViewById<ImageView>(R.id.btn_cd_instagram)
        val btn2 = findViewById<ImageView>(R.id.btn_cd_facebook)
        val btn3 = findViewById<ImageView>(R.id.btn_cd_gmail)
        val btn4 = findViewById<ImageView>(R.id.btn_cd_whatsapp)

        btn1.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"))
            startActivity(intent)
        }

        btn2.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"))
            startActivity(intent)
        }


        btn3.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gmail.com"))
            startActivity(intent)
        }


        btn4.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.whatsapp.com"))
            startActivity(intent)
        }

        val btn5 = findViewById<ImageView>(R.id.backBtn)
        btn5.setOnClickListener{
            finish()
        }

    }

    override fun onBackPressed() {
        finish()
    }
}