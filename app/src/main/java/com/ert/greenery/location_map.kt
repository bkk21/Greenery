package com.ert.greenery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.ert.greenery.Retrofit2.APIS
import com.ert.greenery.Retrofit2.PM_Chat_Result
import com.ert.greenery.Retrofit2.PM_Chat_first
import com.ert.greenery.Retrofit2.PM_get_near_trash
import com.ert.greenery.Retrofit2.PM_get_trash
import com.ert.greenery.Retrofit2.PM_get_trash_Result
import com.google.android.gms.location.*
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.Transition
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class location_map : AppCompatActivity() {

    //api 생성
    val api = APIS.create()

    lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null
    private var labelLayer: LabelLayer? = null
    private val duration = 100

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_map)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Greenery 지도"

        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (checkPermissionForLocation(this)) {
            startLocationUpdates()
        }

        KakaoMapSdk.init(this, "34241e07f7686ac80f7daa6ba4ddaac2")

        mapView = findViewById(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {}
            override fun onMapError(error: Exception) {}
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                if (ActivityCompat.checkSelfPermission(this@location_map, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location ->
                        location?.let {

                            // SharedPreferences에서 위도와 경도 읽어오기
                            val sharedPreferences = getSharedPreferences("location", MODE_PRIVATE)
                            val latitude = sharedPreferences.getString("latitude", "0.0")?.toDouble()
                            val longitude = sharedPreferences.getString("longitude", "0.0")?.toDouble()

                            if (latitude != null) {
                                if (longitude != null) {


                                    //테스트용 더현대
                                    showIconLabel1(37.525387412764935, 126.92783852449817)
                                    Log.d("결과", "굿")

                                    val sharedPreferences2 = getSharedPreferences("intent", MODE_PRIVATE)
                                    val value = sharedPreferences2.getInt("value", 0)

                                    if (value == 1){
                                        val sharedPreferences3 = getSharedPreferences("data1", MODE_PRIVATE)
                                        val latitude1 = sharedPreferences3.getString("lat", "0.0")?.toDouble()
                                        val longitude1 = sharedPreferences3.getString("lng", "0.0")?.toDouble()
                                        //showIconLabel2(37.5218052927, 126.9256512442)

                                        if (latitude1 != null) {
                                            if (longitude1 != null) {
                                                showIconLabel2(latitude1, longitude1)
                                                Log.d("선택한 위도", ""+latitude1)
                                                Log.d("선택한 경도", ""+longitude1)
                                            }
                                        }
                                    }
                                    data_get(37.525387412764935, 126.92783852449817)

                                    //showIconLabel1(latitude, longitude)
                                    //Log.d("결과", "굿")
                                    //data_get(latitude, longitude)
                                }
                            }
                            //showIconLabel(it.latitude, it.longitude)
                            Log.d("위경도 결과", "저장 없음")

                        }
                    }
                }
            }
        })
    }

    fun data_get(la:Double, lo:Double){

        val data = PM_get_trash(la, lo, 10000)

        api.get_trash(data).enqueue(object : Callback<PM_get_trash_Result> {

            override fun onResponse(call: Call<PM_get_trash_Result>, response: Response<PM_get_trash_Result>) {

                Log.d("log", response.body().toString())

                // 맨 처음 문장 실행
                if(!response.body().toString().isEmpty()){
                    Log.d("결과테스트",response.body()?.trash_data.toString())
                }
            }

            override fun onFailure(call: Call<PM_get_trash_Result>, t: Throwable) {
                // 실패
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }
        }) //여기까지가 통신 한 묶음
    }

    //일반쓰레기
    fun trash1(){

    }

    //재활용쓰레기
    fun trash2(){

    }

    //재활용센터
    fun trash3(){

    }

    //페건전지
    fun trash4(){

    }

    //폐형광등
    fun trash5(){

    }

    private fun showIconLabel1(la: Double, lo: Double) {
        val pos = LatLng.from(la, lo)
        val styles = kakaoMap?.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(R.drawable.red_marker)
                    .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
            )
        )
        val text = "test 라벨"
        kakaoMap?.let {
            it.labelManager?.layer?.addLabel(LabelOptions.from(text, pos).setStyles(styles))
            it.moveCamera(CameraUpdateFactory.newCenterPosition(pos, 16), CameraAnimation.from(duration))
        }
    }

    private fun showIconLabel2(la: Double, lo: Double) {
        val pos = LatLng.from(la, lo)
        val styles = kakaoMap?.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(R.drawable.blue_marker)
                    .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
            )
        )
        val text = "test 라벨"
        kakaoMap?.let {
            it.labelManager?.layer?.addLabel(LabelOptions.from(text, pos).setStyles(styles))
            //it.moveCamera(CameraUpdateFactory.newCenterPosition(pos, 16), CameraAnimation.from(duration))
        }
    }

    private fun startLocationUpdates() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                showIconLabel1(it.latitude, it.longitude)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    private fun checkPermissionForLocation(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                return false
            }
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
