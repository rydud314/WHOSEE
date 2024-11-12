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

    // 명함 데이터 클래스
    data class CardData(
        val cardId: String,
        val gender: String,
        val job: String,
        val name: String,
        val position: String,
        val workplace: String,
        val month: Int, // 명함 주고받은 달 (1~12)
        val type: String // "given" 또는 "received"로 명함이 주어진 유형
    )

    // 연간 리포트 데이터 클래스
    data class AnnualData(
        val month: Int,
        var givenCards: Int = 0,
        var receivedCards: Int = 0
    )

    // cardDataSet (명함 데이터셋)
    private val cardDataSet = listOf(
        CardData("first1", "여", "개발자", "김하영", "백엔드 개발자", "테크월드", 7, "received"),
        CardData("first2", "남", "마케터", "박소영", "디지털 마케터", "마케팅코리아", 7, "given"),
        CardData("first3", "남", "데이터 분석가", "최민수", "데이터 분석가", "데이터랩", 7, "received"),
        CardData("first4", "여", "영업 전문가", "정수민", "영업 전문가", "영업솔루션", 8, "given"),
        CardData("first5", "남", "개발자", "홍길동", "시니어 개발자 팀장", "테크월드", 10, "received"),
        CardData("first6", "여", "영업 전문가", "윤서연", "영업 팀장", "세일즈매니지먼트", 7, "given"),
        CardData("first7", "남", "마케터", "권민호", "디지털 마케터", "마케팅코리아", 7, "received"),
        CardData("first8", "여", "디자이너", "김민지", "제품 디자이너 실장", "콘텐츠 하우스", 8, "given"),
        CardData("first9", "남", "데이터 분석가", "이지훈", "데이터 엔지니어", "인사이트 코어", 9, "received"),
        CardData("first10", "여", "마케터", "박서현", "마케팅 전문가 대리", "마케팅코리아", 10, "given"),
        CardData("first11", "남", "개발자", "오준서", "주니어 개발자 사원", "데브 솔루션", 11, "received"),
        CardData("first12", "남", "개발자", "오준남", "주니어 개발자 대리", "데브 솔루션", 11, "received"),
        CardData("first13", "여", "디자이너", "김서영", "웹 디자이너 실장", "데브 솔루션", 8, "given"),
        CardData("first14", "여", "마케터", "김서진", "웹 마케터 실장", "마케팅코리아", 8, "received"),
        CardData("first15", "여", "마케터", "김서현", "웹 마케터 팀장", "마케팅코리아", 8, "received"),
        CardData("first16", "여", "개발자", "김아영", "프론트엔드 개발자 사원", "테크월드", 7, "given"),
        CardData("first17", "남", "개발자", "김아진", "프론트엔드 개발자 사원", "테크월드", 10, "received"),
        CardData("first18", "남", "영업 전문가", "김다준", "영업 전문가 대리", "영업솔루션", 7, "received"),
        CardData("first19", "여", "영업 전문가", "김다영", "영업 전문가 실장", "영업솔루션", 9, "received"),
        CardData("first20", "여", "데이터 분석가", "김지영", "데이터 엔지니어 팀장", "인사이트 코어", 9, "received"),
        // cardId email gender imgaeName introduction job name pofol position sns tel workplace
        )

    // AnnualDataSet (연간 리포트 데이터셋, 1월~12월 초기화)
    private val annualDataSet = List(12) { month -> AnnualData(month + 1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annual_report)

        val barChart = findViewById<BarChart>(R.id.barChart)
        val pieChart = findViewById<PieChart>(R.id.genderPieChart) // 성비 차트
        val jobPieChart = findViewById<PieChart>(R.id.jobPieChart) // 직업군 차트

        //---------------------------------------------
        //barChart
        barChart.fitScreen()

        // 명함 데이터를 연간 리포트 데이터에 반영
        cardDataSet.forEach { card ->
            val monthIndex = card.month - 1
            if (card.type == "given") {
                annualDataSet[monthIndex].givenCards++
            } else if (card.type == "received") {
                annualDataSet[monthIndex].receivedCards++
            }
        }

        // 바 차트 데이터 준비
        val receivedEntries = ArrayList<BarEntry>()
        val givenEntries = ArrayList<BarEntry>()

        annualDataSet.forEachIndexed { index, data ->
            receivedEntries.add(BarEntry(index.toFloat(), data.receivedCards.toFloat()))
            givenEntries.add(BarEntry(index.toFloat(), data.givenCards.toFloat()))
        }

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
        // X축 최소값 및 최대값 설정 (1월부터 12월까지의 정확한 범위 설정)
        barChart.xAxis.axisMinimum = 0f
        barChart.xAxis.axisMaximum = 12f
        xAxis.labelCount = 12        // 12개의 월 레이블 표시
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
        barChart.groupBars(0f, 0.3f, 0.05f)
        barChart.invalidate()

        // 커스텀 마커 설정
        val markerView = CustomMarkerView(this)
        barChart.marker = markerView

        // 범례 설정
        val legend = barChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textSize = 12f

        // 확대 및 드래그 설정
        barChart.setScaleEnabled(true)
        barChart.setPinchZoom(true)
        barChart.setDragEnabled(true)
        barChart.viewPortHandler.setMaximumScaleX(2f) // X축 최대 확대 비율
        barChart.viewPortHandler.setMaximumScaleY(1f) // Y축 최대 확대 비율
        barChart.animateY(1000) // Y축 방향으로 1초 동안 애니메이션
        barChart.animateX(1000) // X축 방향으로 1초 동안 애니메이션

        // 인터랙티브 기능 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true) // 핀치 줌 가능
        barChart.isDoubleTapToZoomEnabled = true // 더블 탭 줌 활성화
        barChart.apply {
            description.isEnabled = false
        }
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
        // PieChart
        // 성비 차트
        val maleCount = cardDataSet.count { it.gender == "남" }.toFloat()
        val femaleCount = cardDataSet.count { it.gender == "여" }.toFloat()

        val genderEntries = listOf(PieEntry(maleCount, "남자"), PieEntry(femaleCount, "여자"))
        val genderDataSet = PieDataSet(genderEntries, "성비").apply {
            colors = listOf(Color.rgb(186, 85, 211), Color.rgb(221, 160, 221))
            sliceSpace = 3f
        }
        val genderPieData = PieData(genderDataSet).apply {
            setValueTextSize(12f)
            setValueTextColor(Color.WHITE)
            setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}개"
                }
            })
        }
//        pieChart.setExtraOffsets(0f, 0f, 0f, -50f) // 차트 아래쪽 여백 줄이기

        pieChart.apply {
            pieChart.data = genderPieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 60f
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            animateY(1000)
            legend.isEnabled = true
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.textSize = 12f
        }

        //---------------------------------------------
        // jobPieChart
        // 직업군 차트
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
//        jobPieChart.setExtraOffsets(0f, 0f, 0f, -50f) // 차트 아래쪽 여백 줄이기

        jobPieChart.apply {
            jobPieChart.data = jobPieData
            description.isEnabled = false
            isDrawHoleEnabled = false
            setEntryLabelTextSize(12f)
            setEntryLabelColor(Color.BLACK)
            animateY(1000)

            // 범례 설정
            legend.isEnabled = true
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.isWordWrapEnabled = true
            legend.textSize = 12f
        }

    }
}