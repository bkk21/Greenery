package com.ert.greenery

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kakao.vectormap.MapView

class detail_bottom : BottomSheetDialogFragment() {

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SharedPreferences에서 데이터 로드
        val sharedPreference1 = requireActivity().getSharedPreferences("one_data", AppCompatActivity.MODE_PRIVATE)
        val add = sharedPreference1.getString("addr", "")
        val place = sharedPreference1.getString("place", "-")
        val type = sharedPreference1.getString("type", "")
        val call_number = sharedPreference1.getString("call_number", "")

        // 뷰 찾기 및 데이터 설정
        view?.findViewById<TextView>(R.id.type)?.text = type
        view?.findViewById<TextView>(R.id.dis)?.text = "상세위치 : " + place
        view?.findViewById<TextView>(R.id.location)?.text = "주소 : " + add

        val list = mutableListOf<String>("꽃은 일반쓰레기로 버립니다.", "인공눈물은 일반쓰레기로 버립니다.", "종이는 항상 재활용되지 않습니다.", "거울은 재활용이 안 됩니다.", "은박지는 일반쓰레기입니다.",
            "피자박스는 일반쓰레기입니다.", "책은 커버를 분리해주세요", "치약은 일반쓰레기입니다.", "라이터는 가스를 빼고 일반쓰레기로 버려주세요.", "과일 포장재는 일반쓰레기입니다.", "뽁뽁이는 일반쓰레기입니다.",
            "가게 쿠폰은 일반쓰레기입니다.", "의약품은 약국에 반납해주세요.", "우산은 철만 고철로 버려주세요", "작은 화장품은 일반 쓰레기입니다.", "음식물 쓰레기는 가축이 먹을 수 있어야 합니다.", "고추장은 일반쓰레기입니다.",
            "멀티탭은 일반쓰레기입니다", "샴프의 펌프 스프링은 고철입니다.", "휴대폰케이스는 일반쓰레기입니다.", "비닐은 색상과 무관하게 일반쓰레기입니다.", "배출일을 지켜주세요!", "박스는 납작하게 버려주세요.")

        Log.d("list 길이", list.count().toString())
        Log.d("list 테스트", list.get(0))

        val numberRange = (0..22)
        var random_num =numberRange.random()

        if (type == "재활용센터")
            view?.findViewById<TextView>(R.id.call_number)?.text = "재활용센터 번호 : " + call_number
        else
            view?.findViewById<TextView>(R.id.call_number)?.text = list.get(random_num)

    }
}