package com.ert.greenery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.ert.greenery.Retrofit2.APIS
import com.ert.greenery.Retrofit2.PM_Chat
import com.ert.greenery.Retrofit2.PM_Chat_Result
import com.ert.greenery.Retrofit2.PM_Chat_first
import com.ert.greenery.Retrofit2.PM_file_Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity() {

    //api 생성
    val api = APIS.create()

    lateinit var send_history : MutableList<Map<String, String>>


    lateinit var log: LinearLayout
    var num = 0
    var isfirst = 1 // 처음이면 1 아니면 0
    lateinit var img_result: String

    // storage 권한 처리에 필요한 변수
    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99

    var trimmedString = ""

    var currentPhotoUri: Uri? = null

    private val REQUEST_PERMISSIONS = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        supportActionBar?.setDisplayShowHomeEnabled(true)
        //supportActionBar?.title = "비밀번호 변경"

        // 사용자 정의 레이아웃을 툴바에 설정
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.toolbar_custom, toolbar, false)
        toolbar.addView(view)

        val toolbar_image = view.findViewById<ImageView>(R.id.toolbar_image)

        toolbar_image.setOnClickListener {
            val bottomSheet = BottomSheetFragment()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        //val search_btn = findViewById<LinearLayout>(R.id.search)

        val chat_btn = findViewById<LinearLayout>(R.id.chat)

        //val map_btn = findViewById<LinearLayout>(R.id.map)

        val chat_send = findViewById<LinearLayout>(R.id.chat_send)

        val camera = findViewById<ImageView>(R.id.camera)


        chat_send.setOnClickListener {

            val msg = findViewById<EditText>(R.id.input)
            msg.isEnabled = false
            var data_text = msg.text.toString()

            if (isfirst == 1) {
                chat_first_chat(data_text)
                msg.setText("")
                //chat_send()
                createView_user(data_text)
            }
            else {
                chat_send(data_text)
                msg.setText("")
                createView_user(data_text)
            }
        }

        camera.setOnClickListener {
            CallCamera()
        }

    }

    fun chat_first_chat(data_text:String){
        val data = PM_Chat_first(data_text)
        val msg = findViewById<EditText>(R.id.input)
        msg.isEnabled = false

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
                    //data_text = ""
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
    fun chat_img(){

        val msg = findViewById<EditText>(R.id.input)
        msg.isEnabled = false

        // SharedPreferences 생성 및 사진 주소 저장
        val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
        var data_text = sharedPreference.getString("data", "")

        if (data_text != null) {
            Log.d("data", data_text)
        }

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

    fun chat_send(data_text:String){
        val msg = findViewById<EditText>(R.id.input)

        msg.isEnabled = false

        //로딩 시작
        var pb = findViewById<ProgressBar>(R.id.progressBar)
        pb.visibility = View.VISIBLE

        val data = PM_Chat(data_text, send_history)

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

    private fun createView_user(data_text:String) {

        log = findViewById(R.id.log)

        // 텍스트뷰 생성
        val newtextview: TextView = TextView(applicationContext)

        // 텍스트 뷰 글자 설정
        newtextview.text = data_text

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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_CODE -> {
                for (grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "카메라 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }
            }
            STORAGE_CODE -> {
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "저장소 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun checkPermission(permissions: Array<out String>, type:Int):Boolean{
        var permission = mutableMapOf<String, String>()
        permission["camera"] = Manifest.permission.CAMERA
        permission["storageRead"] = Manifest.permission.READ_EXTERNAL_STORAGE
        permission["storageWrite"] =  Manifest.permission.WRITE_EXTERNAL_STORAGE

        // 현재 권한 상태 검사
        var denied = permission.count { ContextCompat.checkSelfPermission(this, it.value)  == PackageManager.PERMISSION_DENIED }

        // 마시멜로 버전 이후
        if(denied > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission.values.toTypedArray(), REQUEST_PERMISSIONS)
        }

        return true
    }

    // 카메라 호출 함수 수정
    fun CallCamera(){
        if(checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)){
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            currentPhotoUri = createImageFileUri() // URI 생성하고 저장
            itt.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri) // 인텐트에 URI 추가

            //sharedpreference 생성 및 사진 주소 저장
            val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
            val editor  : SharedPreferences.Editor = sharedPreference.edit()
            editor.putString("photo_url", currentPhotoUri.toString())
            editor.putInt("data_yes", 1)
            editor.commit() // data 저장

            startActivityForResult(itt, CAMERA_CODE)
        }
    }


    // 결과 처리 함수 수정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView = findViewById<ImageView>(R.id.imageView)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    currentPhotoUri?.let { uri ->
                        // 파일 경로를 서버에 업로드하는 함수 호출
                        uploadImage(trimmedString)
                        createView_img()

                    }
                }
            }
        }
    }

    // 파일을 서버에 업로드하는 함수
    private fun uploadImage(file_text: String) {
        var pb = findViewById<ProgressBar>(R.id.progressBar)
        pb.visibility = View.VISIBLE

        val file = File(file_text)

        if (!file.exists()) {
            // 파일이 존재하지 않을 경우 에러 처리
            Log.e("File Not Found", "파일을 찾을 수 없습니다.")
            return
        }

        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://csgpu.kku.ac.kr:5123/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // 코루틴을 사용하여 백그라운드 스레드에서 네트워크 호출 수행
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val call = apiService.uploadFile(filePart)
                val response = call.execute()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // 처리할 내용 추가
                    Log.d("결과", "${responseBody?.toString()}")

                    //sharedpreference 생성 및 사진 주소 저장
                    val sharedPreference = getSharedPreferences("photo", MODE_PRIVATE)
                    val editor  : SharedPreferences.Editor = sharedPreference.edit()

                    // shared 저장
                    if (!responseBody?.data.isNullOrEmpty()) {
                        // responseBody?.data가 null이 아니고 비어있지 않으면 첫 번째 요소 저장
                        editor.putString("data", responseBody?.data?.get(0))
                        img_result = responseBody?.data?.get(0).toString()
                    } else {
                        // responseBody?.data가 null이거나 비어있으면 빈 문자열 저장
                        editor.putString("data", "")
                        img_result = ""
                    }
                    editor.apply() // 비동기적으로 data 저장!

                    //sharedpreference 생성 및 사진 주소 저장
                    val sharedPreference2 = getSharedPreferences("visit", MODE_PRIVATE)
                    val editor2  : SharedPreferences.Editor = sharedPreference2.edit()
                    editor2.putInt("camera", 1)
                    editor2.apply()

                    chat_img() // 이미지 채팅 관련 함수 호출
                }
                else {
                    // 실패 처리 로직
                    withContext(Dispatchers.Main) {
                        var pb = findViewById<ProgressBar>(R.id.progressBar)
                        pb.visibility = View.INVISIBLE
                        Log.d("업로드 실패", "${response.code()} ${response.message()}")
                        Toast.makeText(this@MainActivity, "저장소 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }

                }
            } catch (e: Exception) {
                var pb = findViewById<ProgressBar>(R.id.progressBar)
                pb.visibility = View.INVISIBLE
                // 네트워크 요청 중 예외 발생
                withContext(Dispatchers.Main) {
                    // UI 스레드에서 에러 메시지 표시
                    Log.e("Network Error", "서버 연결 실패: ${e.message}")
                    Toast.makeText(this@MainActivity, "저장소 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    interface ApiService {
        @Multipart
        @POST("predict")
        fun uploadFile(@Part file: MultipartBody.Part): Call<PM_file_Result>
    }

    private fun createImageFileUri(): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, "JPEG_${timeStamp}_403359184285733784.jpg")

        trimmedString = imageFile.toString()

        Log.d("real", trimmedString )

        return FileProvider.getUriForFile(this, "com.ert.greenery.fileprovider", imageFile)
    }

//    private fun createView_img() {
//        log = findViewById(R.id.log)
//
//        // 새 ImageView 생성
//        val img = ImageView(applicationContext).apply {
//            // SharedPreferences에서 저장된 URI 사용
//            setImageURI(getUriFromSharedPreferences())
//            scaleType = ImageView.ScaleType.FIT_CENTER // 이미지가 뷰의 중앙에 맞춰지면서 원래 크기 유지
//
//            // ID 설정
//            id = num
//            num += 1
//
//            // 레이아웃 설정
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, // 이미지의 원래 크기에 맞춤
//                LinearLayout.LayoutParams.WRAP_CONTENT  // 이미지의 원래 크기에 맞춤
//            ).apply {
//                gravity = Gravity.END // 오른쪽 정렬
//                topMargin = TypedValue.applyDimension(
//                    TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt() // 상단 마진 설정
//                bottomMargin = topMargin // 하단 마진 동일하게 설정
//            }
//        }
//
//        // 이미지 뷰를 레이아웃에 추가
//        log.addView(img)
//
//        // 스크롤 뷰 설정
//        val scrollView: ScrollView = findViewById(R.id.sc)
//        scrollView.post {
//            scrollView.fullScroll(ScrollView.FOCUS_DOWN) // 콘텐츠가 변경될 때 스크롤뷰를 맨 아래로 이동
//        }

//    }


    private fun createView_img() {
        log = findViewById(R.id.log)

        // 새 ImageView 생성
        val img = ImageView(applicationContext).apply {
            // SharedPreferences에서 저장된 URI 사용
            val uri = getUriFromSharedPreferences()
            setImageURI(uri)
            scaleType = ImageView.ScaleType.FIT_CENTER

            // 화면 너비의 절반 값을 계산
            val displayWidth = resources.displayMetrics.widthPixels
            val width = (displayWidth * 2) / 3

            // 이미지의 실제 크기를 얻어와서 높이를 계산
            val imageSize = uri?.let { getImageSizeFromUri(it) }
            val height = if (imageSize != null) {
                width * imageSize.height / imageSize.width // 너비에 따라 비율에 맞는 높이 계산
            } else {
                LinearLayout.LayoutParams.WRAP_CONTENT // 이미지 크기를 얻을 수 없는 경우
            }

            // 레이아웃 설정
            layoutParams = LinearLayout.LayoutParams(
                width, // 화면 너비의 2/3
                height
            ).apply {
                gravity = Gravity.END // 오른쪽 정렬
                topMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt() // 상단 마진 설정
                bottomMargin = topMargin // 하단 마진 동일하게 설정
            }
        }
        // 이미지 뷰를 레이아웃에 추가
        log.addView(img)

        // 스크롤 뷰 설정
        val scrollView: ScrollView = findViewById(R.id.sc)
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN) // 콘텐츠가 변경될 때 스크롤뷰를 맨 아래로 이동
        }
    }

    // URI로부터 이미지의 크기를 구하는 함수
    private fun getImageSizeFromUri(uri: Uri): Size? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            Size(options.outWidth, options.outHeight)
        } catch (e: Exception) {
            null
        }
    }

    data class Size(val width: Int, val height: Int)





    fun getUriFromSharedPreferences(): Uri? {
        val sharedPref = getSharedPreferences("photo", MODE_PRIVATE)
        val uriString = sharedPref.getString("photo_url", null)
        return if (uriString != null) Uri.parse(uriString) else null
    }

}