package com.ert.greenery

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.ert.greenery.Retrofit2.APIS
import com.ert.greenery.Retrofit2.PM_file_Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class Camera : AppCompatActivity() {

    // storage 권한 처리에 필요한 변수
    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99

    var trimmedString = ""

    var currentPhotoUri: Uri? = null

    private val REQUEST_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val home = findViewById<LinearLayout>(R.id.home)
        val camera = findViewById<LinearLayout>(R.id.camera)
        val chat = findViewById<LinearLayout>(R.id.chat)
        val earth = findViewById<RelativeLayout>(R.id.earth)

        home.setOnClickListener {
            val joinIntent = Intent(this@Camera, MainActivity::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)
            finish()
        }

        camera.setOnClickListener {
            CallCamera()
        }

        chat.setOnClickListener {
            val joinIntent = Intent(this@Camera, Chat::class.java)
            startActivity(joinIntent)
            overridePendingTransition(0, 0)
            finish()
        }

        earth.setOnClickListener {
            CallCamera()
        }

    }
    // 카메라 권한, 저장소 권한
    // 요청 권한
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

    // 다른 권한 등도 확인이 가능하도록
    /*fun checkPermission(permissions: Array<out String>, type:Int):Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (permission in permissions){
                if(ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }

        return true
    }*/

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
                    }
                }
            }
        }
    }

    // 파일을 서버에 업로드하는 함수
    private fun uploadImage(file_text: String) {
        val img = findViewById<ImageView>(R.id.imageView)
        val text1 = findViewById<TextView>(R.id.text1)
        val text2 = findViewById<TextView>(R.id.text2)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE


        val file = File(file_text)

        if (!file.exists()) {
            // 파일이 존재하지 않을 경우 에러 처리
            Log.e("File Not Found", "파일을 찾을 수 없습니다.")
            return
        }

        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://csgpu.kku.ac.kr:51203/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // 코루틴을 사용하여 백그라운드 스레드에서 네트워크 호출 수행
        GlobalScope.launch(Dispatchers.IO) {
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
                } else {
                    // responseBody?.data가 null이거나 비어있으면 빈 문자열 저장
                    editor.putString("data", "")
                }
                editor.apply() // 비동기적으로 data 저장!

                //화면 전환
                val joinIntent = Intent(this@Camera, Camera_Result::class.java)
                startActivity(joinIntent)
                overridePendingTransition(0, 0)
                finish()
            }
            else {
                Log.d("업로드 실패", "${response.code()} ${response.message()}")
                val img = findViewById<ImageView>(R.id.imageView)
                val text1 = findViewById<TextView>(R.id.text1)
                val text2 = findViewById<TextView>(R.id.text2)
                img.setImageResource(R.drawable.server_error)
                text1.setText("서버 오류 안내")
                text2.setText("서버 연결에 문제가 발생했습니다.\n잠시 후 다시 시도해주세요")
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


}