package com.ert.greenery
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.GLES20
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SharedPreferences에서 데이터 로드
        val sharedPreference1 = requireActivity().getSharedPreferences("data1", AppCompatActivity.MODE_PRIVATE)
        val add1 = sharedPreference1.getString("addr", "")
        val place1 = sharedPreference1.getString("place", "-")
        val type1 = sharedPreference1.getString("type", "")

        val sharedPreference2 = requireActivity().getSharedPreferences("data2", AppCompatActivity.MODE_PRIVATE)
        val add2 = sharedPreference2.getString("addr", "")
        val place2 = sharedPreference2.getString("place", "-")
        val type2 = sharedPreference2.getString("type", "")

        // 뷰 찾기 및 데이터 설정
        view.findViewById<TextView>(R.id.type1)?.text = type1
        view.findViewById<TextView>(R.id.type2)?.text = type2
        view.findViewById<TextView>(R.id.dis1)?.text = place1
        view.findViewById<TextView>(R.id.dis2)?.text = place2
        view.findViewById<TextView>(R.id.location1)?.text = add1
        view.findViewById<TextView>(R.id.location2)?.text = add2
        // 기타 필요한 설정도 여기에서 수행

        var go1 = view.findViewById<TextView>(R.id.map1)
        var go2 = view.findViewById<TextView>(R.id.map2)

        val sharedPreference = requireActivity().getSharedPreferences("intent", AppCompatActivity.MODE_PRIVATE)
        val editor  : SharedPreferences.Editor = sharedPreference.edit()

        go1.setOnClickListener {
            editor.putInt("value", 1)
            editor.commit() // data 저장
            val intent = Intent(requireContext(), location_map::class.java)
            startActivity(intent)
        }

        go2.setOnClickListener {
            editor.putInt("value", 2)
            editor.commit() // data 저장
            val intent = Intent(requireContext(), location_map::class.java)
            startActivity(intent)
        }

    }
}
