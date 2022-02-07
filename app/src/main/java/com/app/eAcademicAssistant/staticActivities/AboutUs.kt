package com.app.eAcademicAssistant.staticActivities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.app.eAcademicAssistant.R

class AboutUs : AppCompatActivity() {

    private lateinit var btn1: ImageView
    private lateinit var btn2: ImageView
    private lateinit var btn3: ImageView
    private lateinit var btn4: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_about_us)

        init()

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

    private fun init(){
        btn1 = findViewById(R.id.btn_instagram)
        btn2 = findViewById(R.id.btn_facebook)
        btn3 = findViewById(R.id.btn_gmail)
        btn4 = findViewById(R.id.btn_whatsapp)
    }
}