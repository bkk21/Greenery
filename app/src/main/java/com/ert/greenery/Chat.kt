package com.ert.greenery

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.ert.greenery.Retrofit2.APIS
import com.ert.greenery.Retrofit2.PM_Chat
import com.ert.greenery.Retrofit2.PM_Chat_Result
import com.ert.greenery.Retrofit2.PM_Chat_first
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Chat : AppCompatActivity() {

    //api 생성
    val api = APIS.create()

    lateinit var send_history : MutableList<Map<String, String>>

    lateinit var log: LinearLayout
    var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        visit_check()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val home = findViewById<LinearLayout>(R.id.home)
        val camera = findViewById<LinearLayout>(R.id.camera)
        val chat = findViewById<LinearLayout>(R.id.chat)

        var img = findViewById<ImageView>(R.id.img)
        val chat_send = findViewById<LinearLayout>(R.id.chat_send)

        img.visibility = View.INVISIBLE
        var currentPhotoUri = getUriFromSharedPreferences()

        // SharedPreferences 생성 및 사진 주소 저장
        val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
        val data_yes = sharedPreference.getInt("data_yes", 0)

        //사진 찍으면 data_yes = 1로 처리
        //챗봇 중복 방지
        if(data_yes != 0){
            img.setImageURI(currentPhotoUri)
        }

        chat_send.setOnClickListener {
            chat_send()
            createView_user()
        }

        home.setOnClickListener {

            //sharedpreference 생성 및 data 삭제
            val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
            val editor  : SharedPreferences.Editor = sharedPreference.edit()
            editor.putString("data", "")
            editor.putInt("data_yes", 0)
            editor.apply()

            //sharedpreference camera 방문 여부 저장
            val sharedPreference2 = getSharedPreferences("visit", MODE_PRIVATE)
            val editor2  : SharedPreferences.Editor = sharedPreference2.edit()
            editor2.putInt("camera", 1)
            editor2.apply()

            val joinIntent = Intent(this@Chat, MainActivity::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)

            finish()
        }

        camera.setOnClickListener {

            //sharedpreference 생성 및 data 삭제
            val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
            val editor  : SharedPreferences.Editor = sharedPreference.edit()
            editor.putString("data", "")
            editor.putInt("data_yes", 0)
            editor.apply()

            //sharedpreference camera 방문 여부 저장
            val sharedPreference2 = getSharedPreferences("visit", MODE_PRIVATE)
            val editor2  : SharedPreferences.Editor = sharedPreference2.edit()
            editor2.putInt("camera", 1)
            editor2.apply()

            val joinIntent = Intent(this@Chat, Camera::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)

            finish()
        }

        chat.setOnClickListener {

            //sharedpreference 생성 및 data 삭제
            val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
            val editor  : SharedPreferences.Editor = sharedPreference.edit()
            editor.putString("data", "")
            editor.putInt("data_yes", 0)
            editor.apply()

            //sharedpreference camera 방문 여부 저장
            val sharedPreference2 = getSharedPreferences("visit", MODE_PRIVATE)
            val editor2  : SharedPreferences.Editor = sharedPreference2.edit()
            editor2.putInt("camera", 1)
            editor2.apply()

            val joinIntent = Intent(this@Chat, Chat::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)

            finish()
        }
    }

    fun visit_check(){
        //sharedpreference 생성 및 사진 주소 저장
        val sharedPreference2 = getSharedPreferences("visit", MODE_PRIVATE)
        val visit_result = sharedPreference2.getInt("camera",0)

        //sharedpreference 생성 및 data 삭제
        val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
        val data = sharedPreference.getString("data","")
        val data_yes = sharedPreference.getInt("data_yes", 0)

        if(data_yes == 0){
            Log.d("data_yes_photo", "data_yes_photo")
            Toast.makeText(this@Chat, "카메라 인식을 먼저 해주세요", Toast.LENGTH_LONG)
                .show()

            //화면 전환
            val joinIntent = Intent(this@Chat, Camera::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)
            finish()
        }

        if(data == ""){
            Log.d("data", "data")
            Toast.makeText(this@Chat, "카메라 인식을 먼저 해주세요", Toast.LENGTH_LONG)
                .show()

            //화면 전환
            val joinIntent = Intent(this@Chat, Camera::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)
            finish()
        }

        if(visit_result == 0){
            Log.d("visit_result", "visit_result")
            Toast.makeText(this@Chat, "카메라 인식을 먼저 해주세요", Toast.LENGTH_LONG)
                .show()

            //화면 전환
            val joinIntent = Intent(this@Chat, Camera::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)
            finish()
        }

        chat_first()
    }

    fun getUriFromSharedPreferences(): Uri? {
        val sharedPref = getSharedPreferences("photo", MODE_PRIVATE)
        val uriString = sharedPref.getString("photo_url", null)
        return if (uriString != null) Uri.parse(uriString) else null
    }

    fun chat_first(){

        val msg = findViewById<EditText>(R.id.input)
        msg.isEnabled = false

        // SharedPreferences 생성 및 사진 주소 저장
        val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
        var data_text = sharedPreference.getString("data", "")

        Log.d("data", data_text.toString())

        val data = PM_Chat_first(data_text)
        var pb = findViewById<ProgressBar>(R.id.progressBar)
        pb.visibility = View.VISIBLE
        api.chat_send_first(data).enqueue(object : Callback<PM_Chat_Result> {

            override fun onResponse(call: Call<PM_Chat_Result>, response: Response<PM_Chat_Result>) {

                Log.d("log", response.body().toString())

                // 맨 처음 문장 실행
                if(!response.body().toString().isEmpty()){

                    send_history = response.body()?.history!!
                    pb.visibility = View.INVISIBLE

                    var img = findViewById<ImageView>(R.id.img)
                    img.visibility = View.VISIBLE
                    createView_gpt(response.body()?.msg.toString())

                    msg.isEnabled = true
                    send_history = response.body()?.history!!

                    data_text = ""

                }
            }

            override fun onFailure(call: Call<PM_Chat_Result>, t: Throwable) {
                // 실패
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }
        }) //여기까지가 통신 한 묶음
    }

    fun chat_send(){
        val msg = findViewById<EditText>(R.id.input)

        msg.isEnabled = false

        //로딩 시작
        var pb = findViewById<ProgressBar>(R.id.progressBar)
        pb.visibility = View.VISIBLE

        val data = PM_Chat(msg.text.toString(), send_history)

        //통신 관련
        api.chat_send(data).enqueue(object : Callback<PM_Chat_Result> {

            override fun onResponse(call: Call<PM_Chat_Result>, response: Response<PM_Chat_Result>) {
                //Log.d("log",response.toString())
                Log.d("log", response.body().toString())

                // 맨 처음 문장 실행
                if(!response.body().toString().isEmpty()){

                    send_history = response.body()?.history!!
                    pb.visibility = View.INVISIBLE

                    createView_gpt(response.body()?.msg.toString())

                    msg.setText("")
                    msg.isEnabled = true
                    send_history = response.body()?.history!!
                }
            }

            override fun onFailure(call: Call<PM_Chat_Result>, t: Throwable) {
                // 실패
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }


        }) //여기까지가 통신 한 묶음
    }

    private fun createView_user() {

        // 텍스트뷰 생성
        val newtextview: TextView = TextView(applicationContext)

        // 텍스트 뷰 글자 설정
        newtextview.text = findViewById<EditText>(R.id.input).text.toString()

        // 텍스트뷰 글자 크기
        newtextview.textSize = 20f

        // 배경 설정
        val backgroundDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.user_back)
        newtextview.background = backgroundDrawable

        // id 설정
        newtextview.id = num
        num += 1

        // 레이아웃 설정
        val param: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // 상단 마진 설정 (각각 15dp)
        val marginVertical = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
        param.topMargin = marginVertical
        param.bottomMargin = marginVertical
        param.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics).toInt()

        // 오른쪽 정렬 설정
        param.gravity = Gravity.END

        // padding 설정
        val paddingInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20f,
            resources.displayMetrics
        ).toInt()

        newtextview.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels)

        // 글자색 설정
        val color = ContextCompat.getColor(applicationContext, R.color.black)
        newtextview.setTextColor(color)

        // 적용
        newtextview.layoutParams = param

        log.addView(newtextview)

        val scrollView: ScrollView = findViewById(R.id.sc)

        // 콘텐츠가 변경될 때 스크롤뷰를 맨 아래로 이동
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    } //createView_user

    private fun createView_gpt(value:String) {

        log = findViewById(R.id.log)

        // 텍스트뷰 생성
        val newtextview: TextView = TextView(applicationContext)

        // 텍스트 뷰 글자 설정
        newtextview.text = value

        // 텍스트뷰 글자 크기
        newtextview.textSize = 20f

        // 배경 설정
        val backgroundDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.gpt_back)
        newtextview.background = backgroundDrawable

        // id 설정
        newtextview.id = num
        num += 1

        // 레이아웃 설정
        val param: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // 상단 마진 설정 (각각 15dp)
        val marginVertical = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
        param.topMargin = marginVertical
        param.bottomMargin = marginVertical
        param.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics).toInt()

        // 왼쪽 정렬 설정
        param.gravity = Gravity.START

        // padding 설정
        val paddingInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20f,
            resources.displayMetrics
        ).toInt()

        newtextview.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels)

        // 글자색 설정
        val color = ContextCompat.getColor(applicationContext, R.color.black)
        newtextview.setTextColor(color)

        // 적용
        newtextview.layoutParams = param

        log.addView(newtextview)

        val scrollView: ScrollView = findViewById(R.id.sc)

        // 콘텐츠가 변경될 때 스크롤뷰를 맨 아래로 이동
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    } //createView_gpt

}