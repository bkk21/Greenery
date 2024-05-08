package com.ert.greenery

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ert.greenery.Retrofit2.APIS
import com.ert.greenery.Retrofit2.PM_get_one_trash
import com.ert.greenery.Retrofit2.PM_get_one_trash_Result
import com.ert.greenery.Retrofit2.PM_get_trash
import com.ert.greenery.Retrofit2.PM_get_trash_Result
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnCameraMoveEndListener
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.PolylineLabel
import com.kakao.vectormap.label.Transition
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class location_map : AppCompatActivity() {


    //api 생성
    val api = APIS.create()

    private val duration = 500
    private var kakaoMap: KakaoMap? = null
    private var labelLayer: LabelLayer? = null
    private val moveLabel: Label? = null
    private val polylineLabel: PolylineLabel? = null

    var la = 0.0
    var ln = 0.0

    var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_map)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Greenery 지도"


        my_location()
        //test 위치 코드 더현대
        //la = 37.525387412764935
        //ln = 126.92783852449817

        val mapView = findViewById<MapView>(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {}
            override fun onMapError(e: Exception) {}
        }, object : KakaoMapReadyCallback() {
            override fun getPosition(): LatLng {

                return LatLng.from(la, ln)
            }

            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                labelLayer = kakaoMap!!.getLabelManager()!!.layer
                kakaoMap!!.setOnCameraMoveEndListener(OnCameraMoveEndListener { kakaoMap, cameraPosition, gestureType -> })

                // SharedPreferences에서 위도와 경도 읽어오기
                val sharedPreferences = getSharedPreferences("location", MODE_PRIVATE)
                val latitude = sharedPreferences.getString("latitude", "0.0")?.toDouble()
                val longitude = sharedPreferences.getString("longitude", "0.0")?.toDouble()


                if (latitude != null) {
                    if (longitude != null) {

                        val sharedPreferences2 = getSharedPreferences("intent", MODE_PRIVATE)
                        val value = sharedPreferences2.getInt("value", 0)
                        Log.d("value 값", ""+value)

                        //내 위치 테스트용 더현대
                        //showIconLabel(37.525387412764935, 126.92783852449817)
                        showIconLabel(la, ln)
                        Log.d("결과", "굿")

                        if (value == 1){

                            val sharedPreferences = getSharedPreferences("data1", MODE_PRIVATE)
                            val tmp_latitude = sharedPreferences.getString("lat", "0.0")?.toDouble()
                            val tmp_longitude = sharedPreferences.getString("lng", "0.0")?.toDouble()
                            val tmp_type = sharedPreferences.getString("type", "-")
                            if (tmp_latitude != null) {
                                if (tmp_longitude != null) {
                                    if (tmp_type != null) {
                                        showIconLabel2(num++, tmp_latitude, tmp_longitude, tmp_type)
                                        Log.d("결과", ""+tmp_latitude+tmp_longitude+tmp_type)
                                    }
                                }
                            }
                        }

                        else if (value == 2){

                            val sharedPreferences = getSharedPreferences("data2", MODE_PRIVATE)
                            val tmp_latitude = sharedPreferences.getString("lat", "0.0")?.toDouble()
                            val tmp_longitude = sharedPreferences.getString("lng", "0.0")?.toDouble()
                            val tmp_type = sharedPreferences.getString("type", "-")
                            if (tmp_latitude != null) {
                                if (tmp_longitude != null) {
                                    if (tmp_type != null) {
                                        showIconLabel2(num++, tmp_latitude, tmp_longitude,tmp_type)
                                        Log.d("결과", ""+tmp_latitude+tmp_longitude+tmp_type)
                                    }
                                }
                            }
                        }

                        else{
                            //val pos = LatLng.from(37.525387412764935, 126.92783852449817)
                            //data_get(37.525387412764935, 126.92783852449817)
                            data_get(la, ln)
                        }


                    }
                }

                // Label 클릭 리스너
                kakaoMap!!.setOnLabelClickListener { kakaoMap, layer, label ->
                        var api_la = label.getPosition().latitude.toString()
                        var api_ln = label.getPosition().longitude.toString()
                        Log.d("클릭 위치 위경도", api_la+api_ln)
                        one_data_get(api_la.toDouble(), api_ln.toDouble())
                }
            }


        })



    }

    fun my_location(){
        val sharedPreferences = getSharedPreferences("location", MODE_PRIVATE)

        la = sharedPreferences.getString("latitude", "0.0")?.toDouble()!!
        ln = sharedPreferences.getString("longitude", "0.0")?.toDouble()!!


    }


    fun one_data_get(la:Double, lo:Double){

        val data = PM_get_one_trash(la, lo)

        api.get_one_trash(data).enqueue(object : Callback<PM_get_one_trash_Result> {

            override fun onResponse(call: Call<PM_get_one_trash_Result>, response: Response<PM_get_one_trash_Result>) {

                Log.d("log", response.body().toString())

                // 맨 처음 문장 실행
                if(!response.body().toString().isEmpty()){
                    val sharedPreference1 = getSharedPreferences("one_data", MODE_PRIVATE)
                    val editor1  : SharedPreferences.Editor = sharedPreference1.edit()
                    editor1.putString("addr", response.body()?.addr.toString())
                    editor1.putString("place", response.body()?.place.toString())
                    editor1.putString("type", response.body()?.type.toString())
                    editor1.putString("call_number", response.body()?.call_number.toString())
                    Log.d("주소 테스트", response.body()?.addr.toString())
                    editor1.commit() // data 저장

                    val bottomSheet = detail_bottom()
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }
            }

            override fun onFailure(call: Call<PM_get_one_trash_Result>, t: Throwable) {
                // 실패
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }
        }) //여기까지가 통신 한 묶음
    }

    private fun showIconLabel(la:Double, ln:Double) {
        val pos = LatLng.from(la, ln)

        // 라벨 스타일 생성
        val styles = kakaoMap!!.labelManager
            ?.addLabelStyles(
                LabelStyles.from(
                    LabelStyle.from(R.drawable.red_dot_marker)
                        .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                )
            )

        // 라벨 생성
        labelLayer!!.addLabel(LabelOptions.from(9999999.toString(), pos).setStyles(styles))
        kakaoMap!!.moveCamera(
            CameraUpdateFactory.newCenterPosition(pos, 16),
            CameraAnimation.from(duration)
        )
    }

    private fun showIconLabel2(i:Int, la:Double, ln:Double, type:String) {
        val pos = LatLng.from(la, ln)

        // 라벨 스타일 생성
        var styles = kakaoMap!!.labelManager
            ?.addLabelStyles(
                LabelStyles.from(
                    LabelStyle.from(R.drawable.trash3_marker)
                        .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                )
            )

        if (type == "일반쓰레기"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash3_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "재활용쓰레기"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash2_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "재활용센터"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash1_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "폐형광등"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash4_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "폐건전지"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash5_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "폐형광등/폐건전지"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash5_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        // 라벨 생성
        labelLayer!!.addLabel(LabelOptions.from(i.toString(), pos).setStyles(styles))
        kakaoMap!!.moveCamera(
            CameraUpdateFactory.newCenterPosition(pos, 16),
            CameraAnimation.from(duration)
        )
    }


    private fun showIconLabel3(i:Int, la:Double, ln:Double, type:String) {
        val pos = LatLng.from(la, ln)

        // 라벨 스타일 생성
        var styles = kakaoMap!!.labelManager
            ?.addLabelStyles(
                LabelStyles.from(
                    LabelStyle.from(R.drawable.trash3_marker)
                        .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                )
            )

        if (type == "일반쓰레기"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash3_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "재활용쓰레기"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash2_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "재활용센터"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash1_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "폐형광등"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash4_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "폐건전지"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash5_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }
        if (type == "폐형광등/폐건전지"){
            styles = kakaoMap!!.labelManager
                ?.addLabelStyles(
                    LabelStyles.from(
                        LabelStyle.from(R.drawable.trash5_marker)
                            .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                    )
                )
        }

        // 라벨 생성
        labelLayer!!.addLabel(LabelOptions.from(i.toString(), pos).setStyles(styles))
    }

    fun data_get(la:Double, lo:Double){

        val data = PM_get_trash(la, lo, 500)

        api.get_trash(data).enqueue(object : Callback<PM_get_trash_Result> {

            override fun onResponse(call: Call<PM_get_trash_Result>, response: Response<PM_get_trash_Result>) {

                Log.d("log", response.body().toString())

                // 맨 처음 문장 실행
                if(!response.body().toString().isEmpty()){
                    Log.d("결과테스트",response.body()?.trash_data.toString())

                    var len = response.body()?.count
                    for(i: Int in 0..<len!!){
                        var tmp_lan = response.body()?.trash_data?.get(i)?.get("lat")
                        var tmp_lng = response.body()?.trash_data?.get(i)?.get("lng")
                        var tmp_type = response.body()?.trash_data?.get(i)?.get("type")
                        tmp_lan?.let { tmp_lng?.let { it1 ->
                            if (tmp_type != null) {
                                showIconLabel3(num++, it.toDouble(), it1.toDouble(), tmp_type)
                            }
                        } }
                    }

                }
            }

            override fun onFailure(call: Call<PM_get_trash_Result>, t: Throwable) {
                // 실패
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }
        }) //여기까지가 통신 한 묶음
    }

    override fun onDestroy() {
        super.onDestroy()  // 항상 super 호출을 메소드 시작 부분에서 하세요.

        // SharedPreferences에서 "value" 키의 데이터를 삭제
        val sharedPreferences = getSharedPreferences("intent", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("value")
        editor.apply()

        val sharedPreference1 = getSharedPreferences("one_data", MODE_PRIVATE)
        val editor1 = sharedPreference1.edit()
        editor1.clear()
        editor1.commit()
    }

    //툴바 뒤로가기
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}