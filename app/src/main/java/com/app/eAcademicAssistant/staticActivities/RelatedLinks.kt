package com.app.eAcademicAssistant.staticActivities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.app.eAcademicAssistant.R

class RelatedLinks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_fade_in_activities, R.anim.anim_fade_out_activities)
        setContentView(R.layout.activity_related_links)

        val btnBack = findViewById<ImageView>(R.id.backBtn)
        btnBack.setOnClickListener{
            finish()
        }


        val btn1 = findViewById<Button>(R.id.btnUC)
        val btn2 = findViewById<Button>(R.id.btnDA)
        val btn3 = findViewById<Button>(R.id.btnPB)
        val btn4 = findViewById<Button>(R.id.btnUR)
        val btn5 = findViewById<Button>(R.id.btnCW)
        val btn6 = findViewById<Button>(R.id.btnEC)

        btn1.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uoc.ac.in/"))
            startActivity(intent)
        }
        btn2.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cuonline.ac.in/"))
            startActivity(intent)
        }
        btn3.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pareekshabhavan.uoc.ac.in/"))
            startActivity(intent)
        }
        btn4.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://results.uoc.ac.in/"))
            startActivity(intent)
        }
        btn5.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://masc.majliscomplex.org/"))
            startActivity(intent)
        }
        btn6.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.majliscomplex.org/"))
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        finish()
    }
}