package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.view.MotionEvent
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class Report_Annual : AppCompatActivity() {
    private var isZoomedIn = false // 줌 상태를 추적하는 변수

    // 데이터 클래스를 정의합니다.
    data class CardData(
        val cardId: String,
        val gender: String,
        val job: String,
        val name: String,
        val position: String,
        val workplace: String
    )

    // cardDataSet을 정의합니다.
    private val cardDataSet = listOf(
        CardData(
            cardId = "first1",
            gender = "여",
            job = "개발자",
            name = "김하영",
            position = "백엔드 개발자",
            workplace = "테크월드"

        ),
        CardData(
            cardId = "first2",
            gender = "남",
            job = "마케터",
            name = "박소영",
            position = "디지털 마케터",
            workplace = "마케팅코리아"
        ),
        CardData(
            cardId = "first3",
            gender = "남",
            job = "데이터 분석가",
            name = "최민수",
            position = "데이터 분석가",
            workplace = "데이터랩"
        ),
        CardData(
            cardId = "first4",
            gender = "여",
            job = "영업 전문가",
            name = "정수민",
            position = "영업 전문가",
            workplace = "영업솔루션"
        ),
        CardData(
            cardId = "first5",
            gender = "남",
            job = "개발자",
            name = "홍길동",
            position = "시니어 개발자 팀장",
            workplace = "테크월드"
        ),
        CardData(
            cardId = "first6",
            gender = "여",
            job = "영업 전문가",
            name = "윤서연",
            position = "영업 팀장",
            workplace = "세일즈매니지먼트"
        ),
        CardData(
            cardId = "first7",
            gender = "남",
            job = "마케터",
            name = "권민호",
            position = "디지털 마케터",
            workplace = "마케팅코리아"
        ),
        CardData(
            cardId = "first8",
            gender = "여",
            job = "디자이너",
            name = "김민지",
            position = "제품 디자이너 실장",
            workplace = "콘텐츠 하우스"
        ),
        CardData(
            cardId = "first9",
            gender = "남",
            job = "데이터 분석가",
            name = "이지훈",
            position = "데이터 엔지니어",
            workplace = "인사이트 코어"
        ),
        CardData(
            cardId = "first10",
            gender = "여",
            job = "미케터",
            name = "박서현",
            position = "마케팅 전문가 대리",
            workplace = "마케팅 코리아"
        ),
        CardData(
            cardId = "first11",
            gender = "남",
            job = "개발자",
            name = "오준서",
            position = "주니어 개발자 사원",
            workplace = "데브 솔루션"
        ),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annual_report)


        val barChart = findViewById<BarChart>(R.id.barChart)

        //---------------------------------------------
        //barChart
        barChart.fitScreen()

        // 하드코딩된 데이터 예제 (월별로 받은 명함과 준 명함)
        val receivedCards = listOf(0, 0, 0, 0, 0, 0, 2, 3, 2, 1, 3, 0)
        val givenCards = listOf(0, 0, 0, 0, 0, 1, 3, 7, 5, 2, 2, 0)

        // 데이터 준비
        val receivedEntries = ArrayList<BarEntry>()
        val givenEntries = ArrayList<BarEntry>()

        for (i in receivedCards.indices) {
            receivedEntries.add(BarEntry(i.toFloat(), receivedCards[i].toFloat())) // Int를 Float로 변환
            givenEntries.add(BarEntry(i.toFloat(), givenCards[i].toFloat()))       // Int를 Float로 변환
        }
        //Color.rgb(128, 0, 128)
        //Color.rgb(153, 50, 204)
        //Color.rgb(186, 85, 211)
        //Color.rgb(221, 160, 221)

        val receivedDataSet = BarDataSet(receivedEntries, "받은 명함").apply {
            color = Color.rgb(128, 0, 128) // 짙은 보라색
        }
        val givenDataSet = BarDataSet(givenEntries, "준 명함").apply {
            color = Color.rgb(221, 160, 221) // 연한 보라색
        }


        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                var valueY = e?.y!!.toInt().toString()
                // 사용자가 특정 값을 선택했을 때의 동작
                Toast.makeText(this@Report_Annual, "선택된 값: ${valueY}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected() {
                // 선택이 해제되었을 때의 동작
            }
        })

        // X축 설정
        val xAxis = barChart.xAxis
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.labelRotationAngle = -45f // 레이블 회전
        // X축 레이블을 월별로 설정
        xAxis.valueFormatter = object : ValueFormatter() {
            private val months =
                arrayOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")

            override fun getFormattedValue(value: Float): String {
                return months.getOrNull(value.toInt()) ?: value.toString()
            }
        }

        // Y축 설정
        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        barChart.axisRight.isEnabled = false

        // 데이터 준비 및 바 간격 설정
        val data = BarData(receivedDataSet, givenDataSet)
        data.barWidth = 0.3f
        // Y값을 정수로 표시하도록 설정
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // 소수점 없이 정수로 표시
            }
        })
        barChart.data = data
        barChart.groupBars(0f, 0.4f, 0.1f)
        barChart.invalidate()


        // 확대 및 드래그 설정
        barChart.setScaleEnabled(true)
        barChart.setPinchZoom(true)
        barChart.setDragEnabled(true)
        barChart.viewPortHandler.setMaximumScaleX(2f) // X축 최대 확대 비율
        barChart.viewPortHandler.setMaximumScaleY(1f) // Y축 최대 확대 비율
        barChart.animateY(1000) // Y축 방향으로 1초 동안 애니메이션
        barChart.animateX(1000) // X축 방향으로 1초 동안 애니메이션

        // 그래프 데이터 적용
        barChart.data = data
        barChart.groupBars(0f, 0.4f, 0.1f) // 그룹 간격 설정
        barChart.invalidate()

        // 커스텀 마커 설정
        val markerView = CustomMarkerView(this)
        barChart.marker = markerView

        // 범례 설정
        val legend = barChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

        // 인터랙티브 기능 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true) // 핀치 줌 가능
        barChart.isDoubleTapToZoomEnabled = true // 더블 탭 줌 활성화
        // 더블 탭 줌 인/줌 아웃 이벤트 설정
        barChart.setOnChartGestureListener(object : OnChartGestureListener {
            override fun onChartDoubleTapped(me: MotionEvent?) {
                if (isZoomedIn) {
                    // 줌 아웃
                    barChart.fitScreen()

                } else {
                    // 줌 인
                    barChart.zoom(1.8f, 1.8f, me?.x ?: 0f, me?.y ?: 0f)
                }
                // 줌 상태 변경
                isZoomedIn = !isZoomedIn
            }

            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
            }

            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
            }

            override fun onChartLongPressed(me: MotionEvent?) {}
            override fun onChartSingleTapped(me: MotionEvent?) {}
            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
        })

        //---------------------------------------------
        // PieChart 데이터 설정
        // 성비 카운트 계산
        val pieChart = findViewById<PieChart>(R.id.genderPieChart) // 추가한 PieChart

        val maleCount = cardDataSet.count { it.gender == "남" }.toFloat()
        val femaleCount = cardDataSet.count { it.gender == "여" }.toFloat()


        val genderEntries = ArrayList<PieEntry>()
        genderEntries.add(PieEntry(maleCount, "남자"))
        genderEntries.add(PieEntry(femaleCount, "여자"))

        val genderDataSet = PieDataSet(genderEntries, "성비").apply {
            colors = listOf(Color.rgb(186, 85, 211), Color.rgb(221, 160, 221)) // 보라색 계열 색상
            sliceSpace = 3f // 각 조각 간의 간격
        }

        val pieData = PieData(genderDataSet).apply {
            setValueTextSize(12f)
            setValueTextColor(Color.WHITE)

            // 값 포맷터 설정
            setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}개" // "개" 단위를 붙여서 정수로 표시
                }
            })
        }

        pieChart.apply {
            pieChart.data = pieData
            description.isEnabled = false // 설명 비활성화
            isDrawHoleEnabled = true // 도넛 모양으로 설정
            holeRadius = 60f // 가운데 구멍의 크기 설정
            setEntryLabelColor(Color.BLACK) // 항목 레이블 색상
            setEntryLabelTextSize(12f) // 항목 레이블 크기
            animateY(1000) // 애니메이션 효과
            legend.isEnabled = true // 범례 활성화
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.textSize = 12f
        }

        //---------------------------------------------
        // PieChart 데이터 설정
        // 직업군 분포도 계산
        val jobPieChart = findViewById<PieChart>(R.id.jobPieChart)

        val jobCounts = cardDataSet.groupingBy { it.job }.eachCount()
        val jobEntries = jobCounts.map { PieEntry(it.value.toFloat(), it.key) }
        val jobDataSet = PieDataSet(jobEntries, "직업군에 따른 명함 분포도").apply {
            colors = listOf(
                Color.rgb(128, 0, 128),
                Color.rgb(153, 50, 204),
                Color.rgb(186, 85, 211),
                Color.rgb(221, 160, 221),
                Color.rgb(173, 127, 168)
            )
            sliceSpace = 3f
        }
        val jobPieData = PieData(jobDataSet).apply {
            setValueTextSize(12f)
            setValueTextColor(Color.WHITE)
            setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}개"
                }
            })
        }

        jobPieChart.apply {
            jobPieChart.data = jobPieData
            description.isEnabled = false
            isDrawHoleEnabled = false
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            animateY(1000)
            legend.isEnabled = true
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.textSize = 12f
        }
    }
}