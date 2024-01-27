package com.ert.greenery

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.ert.greenery.Retrofit2.APIS
import com.ert.greenery.Retrofit2.PM_file_Result
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

class Camera_Result : AppCompatActivity() {

    val api = APIS.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_result)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val type = findViewById<TextView>(R.id.text1)
        val text = findViewById<TextView>(R.id.text2)

        // SharedPreferences 생성 및 사진 주소 저장
        val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
        val data = sharedPreference.getString("data", "")
        val data_yes = sharedPreference.getInt("data_yes", 0)

        var currentPhotoUri = getUriFromSharedPreferences()

        //사진 찍으면 data_yes = 1로 처리
        //챗봇 중복 방지
        if(data_yes != 0){
            imageView.setImageURI(currentPhotoUri)
        }

        if(data != "") {
            type.setText(data)
            var print_type = data?.let { get_last_text(it) }
            text.setText("$print_type 배출해주세요!\n더 자세한 분리수거 방법은 챗봇에서\n확인해주세요")
        }

        val home = findViewById<LinearLayout>(R.id.home)
        val camera = findViewById<LinearLayout>(R.id.camera)
        val chat = findViewById<LinearLayout>(R.id.chat)

        home.setOnClickListener {

            //sharedpreference 생성 및 data 삭제
            val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
            val editor  : SharedPreferences.Editor = sharedPreference.edit()
            editor.putString("data", "")
            editor.putInt("data_yes", 0)
            editor.apply()

            val joinIntent = Intent(this@Camera_Result, MainActivity::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)
            finish()
        }

        camera.setOnClickListener {
            val joinIntent = Intent(this@Camera_Result, Camera::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)
            finish()
        }

        chat.setOnClickListener {
            val joinIntent = Intent(this@Camera_Result, Chat::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)
            finish()
        }
    }

    fun getUriFromSharedPreferences(): Uri? {
        val sharedPref = getSharedPreferences("photo", MODE_PRIVATE)
        val uriString = sharedPref.getString("photo_url", null)
        return if (uriString != null) Uri.parse(uriString) else null
    }

    fun get_last_text(text: String): String {
        val lastName = text.last()

        val firstValue = "으로"
        val secondValue = "로"

        // 한글의 제일 처음과 끝의 범위 밖일 경우는 오류
        if (lastName < '\uAC00' || lastName > '\uD7A3') {
            return text
        }

        val selectedValue = if ((lastName.toInt() - 0xAC00) % 28 > 0) firstValue else secondValue

        return text + selectedValue
    }
}