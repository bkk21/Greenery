package com.ert.greenery

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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

        if (type == "재활용센터")
            view?.findViewById<TextView>(R.id.call_number)?.text = "재활용센터 번호 : " + call_number
        else
            view?.findViewById<TextView>(R.id.call_number)?.text = call_number


    }
}