package com.ert.greenery

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.ert.greenery.Retrofit2.APIS
import com.ert.greenery.Retrofit2.PM_Chat
import com.ert.greenery.Retrofit2.PM_Chat_Result
import com.ert.greenery.Retrofit2.PM_Chat_first
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    //api 생성
    val api = APIS.create()

    lateinit var send_history : MutableList<Map<String, String>>


    lateinit var log: LinearLayout
    var num = 0
    var isfirst = 1 // 처음이면 1 아니면 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val search_btn = findViewById<LinearLayout>(R.id.search)

        val chat_btn = findViewById<LinearLayout>(R.id.chat)

        val map_btn = findViewById<LinearLayout>(R.id.map)

        val chat_send = findViewById<LinearLayout>(R.id.chat_send)

        val camera = findViewById<ImageView>(R.id.camera)


        chat_send.setOnClickListener {

            if (isfirst == 1) {
                chat_first_chat()
                //chat_send()
                createView_user()
            }
            else {
                chat_send()
                createView_user()
            }
        }

        camera.setOnClickListener {

        }

    }

    fun chat_first_chat(){
        val msg = findViewById<EditText>(R.id.input)
        msg.isEnabled = false

//        // SharedPreferences 생성 및 사진 주소 저장
//        val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
//        var data_text = sharedPreference.getString("data", "")

        //Log.d("data", data_text.toString())
        var data_text = msg.text.toString()
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

                    msg.setText("")
                    data_text = ""
                    isfirst = 0

                }
            }

            override fun onFailure(call: Call<PM_Chat_Result>, t: Throwable) {
                // 실패
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }
        }) //여기까지가 통신 한 묶음
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

        log = findViewById(R.id.log)

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

//val camera_btn = findViewById<LinearLayout>(R.id.camera)
//val chat_btn = findViewById<LinearLayout>(R.id.chat)
//val earth = findViewById<ImageView>(R.id.img)
//
//earth.setOnClickListener {
//    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://hci.kku.ac.kr/"))
//    startActivity(intent)
//}
//
//camera_btn.setOnClickListener {
//    val intent = Intent(this@MainActivity, Camera::class.java)
//    startActivity(intent)
//    overridePendingTransition(0, 0)
//    finish()
//}
//
//chat_btn.setOnClickListener {
//    val intent = Intent(this@MainActivity, Chat::class.java)
//    startActivity(intent)
//    overridePendingTransition(0, 0)
//    finish()
//}