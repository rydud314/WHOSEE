package com.example.seesaw

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView(context: Context) : MarkerView(context, R.layout.custom_marker_view) {
    private val markerTextView: TextView = findViewById(R.id.marker_text_view)

    // 값 업데이트
    override fun refreshContent(e: Entry, highlight: Highlight?) {
        markerTextView.text = e.y.toInt().toString() // 선택한 막대의 값을 표시
        super.refreshContent(e, highlight)
    }

    // 마커 위치 조정
    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}