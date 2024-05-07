package com.ert.greenery

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ert.greenery.Retrofit2.APIS
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_map)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Greenery 지도"


        val mapView = findViewById<MapView>(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {}
            override fun onMapError(e: Exception) {}
        }, object : KakaoMapReadyCallback() {
            override fun getPosition(): LatLng {
                return LatLng.from(37.393865, 127.115795)
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

                        data_get(37.525387412764935, 126.92783852449817)

                        //테스트용 더현대
                        showIconLabel(37.525387412764935, 126.92783852449817)
                        Log.d("결과", "굿")
                    }
                }
            }


        })



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
        labelLayer!!.addLabel(LabelOptions.from("테스트", pos).setStyles(styles))
        kakaoMap!!.moveCamera(
            CameraUpdateFactory.newCenterPosition(pos, 15),
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
                                showIconLabel2(i, it.toDouble(), it1.toDouble(), tmp_type)
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
}