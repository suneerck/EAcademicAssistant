package com.app.eAcademicAssistant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.app.eAcademicAssistant.utills.DatabaseUtils

class SplashActivity : AppCompatActivity() {

    private lateinit var spLogo: ImageView
    private lateinit var spTxt1: TextView
    private lateinit var spTxt2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        init()
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in)
        val spinAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_spin)

        Handler(Looper.getMainLooper()).postDelayed({
            Thread {
                if (DatabaseUtils.getInstance(this@SplashActivity).getUser()?.userId != null) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, UserMain::class.java)
                    startActivity(intent)
                    finish()
                }
            }.start()
        }, 1800)

        spLogo.startAnimation(spinAnimation)
        spTxt1.startAnimation(fadeInAnimation)
        spTxt2.startAnimation(fadeInAnimation)

    }

    private fun init() {
        spLogo = findViewById(R.id.sp_logo)
        spTxt1 = findViewById(R.id.txt_eAcademic)
        spTxt2 = findViewById(R.id.txt_assistant)
    }

    override fun onBackPressed() {

    }

}