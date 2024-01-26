package com.ert.greenery

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val camera_btn = findViewById<LinearLayout>(R.id.camera)
        val chat_btn = findViewById<LinearLayout>(R.id.chat)
        val earth = findViewById<ImageView>(R.id.earth)

        earth.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://hci.kku.ac.kr/"))
            startActivity(intent)
        }

        camera_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, Camera::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        chat_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, Chat::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()

        }

    }
}