package com.example.seesaw

import android.graphics.Color
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class Report_Monthly : AppCompatActivity(){

    // 데이터 클래스를 정의합니다.
    data class CardData(
        val cardId: String,
        val gender: String,
        val job: String,
        val name: String,
        val position: String,
        val workplace: String
    )

    private val cardDataSet = listOf(
        CardData(
            cardId = "second1",
            gender = "남",
            job = "프로덕트 매니저",
            name = "이도훈",
            position = "프로덕트 리더",
            workplace = "인사이트랩"
        ),
        CardData(
            cardId = "second2",
            gender = "여",
            job = "디자이너",
            name = "윤예지",
            position = "UI/UX 디자이너",
            workplace = "디자인플러스"
        ),
        CardData(
            cardId = "second3",
            gender = "남",
            job = "데이터 사이언티스트",
            name = "박상현",
            position = "데이터 리더",
            workplace = "데이터월드"
        ),
        CardData(
            cardId = "second4",
            gender = "여",
            job = "콘텐츠 마케터",
            name = "김수정",
            position = "콘텐츠 마케팅 전문가",
            workplace = "미디어하우스"
        ),
        CardData(
            cardId = "second5",
            gender = "남",
            job = "소프트웨어 엔지니어",
            name = "정우성",
            position = "프론트엔드 리드",
            workplace = "테크스퀘어"
        ),
        CardData(
            cardId = "second6",
            gender = "여",
            job = "HR 매니저",
            name = "최다빈",
            position = "채용 전문가",
            workplace = "피플서치"
        ),
        CardData(
            cardId = "second7",
            gender = "남",
            job = "전략 컨설턴트",
            name = "한도영",
            position = "컨설팅 매니저",
            workplace = "컨설팅그룹"
        ),
        CardData(
            cardId = "second8",
            gender = "여",
            job = "그래픽 디자이너",
            name = "장서현",
            position = "브랜드 디자인 리더",
            workplace = "브랜드컴퍼니"
        ),
        CardData(
            cardId = "second9",
            gender = "남",
            job = "AI 엔지니어",
            name = "김재민",
            position = "머신러닝 엔지니어",
            workplace = "AI 랩"
        ),
        CardData(
            cardId = "second10",
            gender = "여",
            job = "소셜 미디어 전문가",
            name = "이서연",
            position = "SNS 전략가",
            workplace = "소셜코리아"
        ),
        CardData(
            cardId = "second11",
            gender = "남",
            job = "모바일 개발자",
            name = "황준혁",
            position = "안드로이드 개발자",
            workplace = "모바일 솔루션"
        ),
        CardData(
            cardId = "second12",
            gender = "여",
            job = "경영 컨설턴트",
            name = "조민아",
            position = "경영 컨설턴트",
            workplace = "컨설팅월드"
        ),
        CardData(
            cardId = "second13",
            gender = "남",
            job = "네트워크 엔지니어",
            name = "신우빈",
            position = "네트워크 매니저",
            workplace = "IT 네트웍스"
        ),
        CardData(
            cardId = "second14",
            gender = "여",
            job = "회계사",
            name = "홍은지",
            position = "회계 리더",
            workplace = "회계솔루션"
        ),
        CardData(
            cardId = "second15",
            gender = "남",
            job = "변호사",
            name = "김태영",
            position = "법무 전문가",
            workplace = "법률사무소 태영"
        ),
        CardData(
            cardId = "second16",
            gender = "여",
            job = "재무 분석가",
            name = "이지영",
            position = "재무 분석 매니저",
            workplace = "재무그룹"
        ),
        CardData(
            cardId = "second17",
            gender = "남",
            job = "영업 전문가",
            name = "오진우",
            position = "영업 팀장",
            workplace = "글로벌세일즈"
        ),
        CardData(
            cardId = "second18",
            gender = "여",
            job = "공공 관계 전문가",
            name = "박소정",
            position = "공공관계 리더",
            workplace = "PR코리아"
        ),
        CardData(
            cardId = "second19",
            gender = "남",
            job = "데이터베이스 관리자",
            name = "임동욱",
            position = "DB 관리자",
            workplace = "데이터센터"
        ),
        CardData(
            cardId = "second20",
            gender = "여",
            job = "교육 전문가",
            name = "서미란",
            position = "교육 프로그램 기획자",
            workplace = "교육플랫폼"
        )
    )

    //---------------------------------------------------------------------------------
    // 교류가 많았던 사람들의 데이터를 생성합니다.
    private fun setupTop5InteractionChart() {
        val bubbleChart = findViewById<BubbleChart>(R.id.top5_interaction_chart)

        // 예제 데이터: 이름과 교류 수
        val top5Interactions = listOf(
            Pair("이도훈", 12),
            Pair("윤예지", 8),
            Pair("박상현", 15),
            Pair("김수정", 6),
            Pair("정우성", 10)
        )

        //----------------------------------------------------------------------------------------
        // BubbleEntry 리스트 생성 - X 좌표를 조정하여 원들이 더 많이 겹치도록 설정
        val bubbleEntries = top5Interactions.mapIndexed { index, (name, interactionCount) ->
            val xPosition = index * 0.6f  // X 좌표를 0.6씩 증가시켜 원들이 더 많이 겹치도록 설정
            BubbleEntry(xPosition, 0f, interactionCount.toFloat()).apply {
                data = name  // 이름을 데이터로 저장하여 표시
            }
        }

        // BubbleDataSet 설정
        val bubbleDataSet = BubbleDataSet(bubbleEntries, "이번달 교류 상위 5인").apply {
            color = Color.argb(150, 128, 0, 128)  // 보라색 + 투명도 조정 (Alpha = 150)
            setDrawValues(true)                   // 버블 값 표시
            valueTextSize = 12f                   // 텍스트 크기 설정
        }

        // 이름을 원 안에 표시하기 위해 ValueFormatter 설정
        bubbleDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBubbleLabel(entry: BubbleEntry?): String {
                return entry?.data?.toString() ?: ""
            }
        }

        // BubbleData 생성 및 적용
        val bubbleData = BubbleData(bubbleDataSet)
        bubbleChart.apply {
            data = bubbleData
            description.isEnabled = false        // 설명 비활성화
            setTouchEnabled(true)                // 터치 활성화
            isDragEnabled = true                 // 드래그(슬라이드) 활성화
            setScaleEnabled(false)               // 줌 비활성화
            setVisibleXRangeMinimum(2f)          // 슬라이드할 수 있도록 최소 범위 설정
            setVisibleXRangeMaximum(2.5f)        // 슬라이드할 수 있도록 최대 범위 설정

            // X축 설정 - 최소 및 최대 값 설정으로 화면 잘림 방지
            xAxis.apply {
                axisMinimum = -0.5f               // 첫 번째 버블이 화면에 잘리지 않도록 최소값 조정
                axisMaximum = bubbleEntries.size * 0.6f  // 마지막 버블이 화면에 잘리지 않도록 최대값 조정
                granularity = 1f                 // X축 값 간격 설정
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                isEnabled = false                // X축 레이블 숨김
            }

            axisLeft.isEnabled = false           // Y축 비활성화
            axisRight.isEnabled = false
            legend.isEnabled = false             // 범례 비활성화

            // 클릭 이벤트 리스너 설정
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e is BubbleEntry) {
                        val name = e.data?.toString() ?: ""
                        val interactions = e.size.toInt()  // y 값을 교류 횟수로 사용
                        bubbleDataSet.valueFormatter = object : ValueFormatter() {
                            override fun getBubbleLabel(entry: BubbleEntry?): String {
                                return if (entry == e) "$name\n${interactions}회" else entry?.data?.toString() ?: ""
                            }
                        }
                        bubbleDataSet.setDrawValues(true) // 모든 항목에 값을 표시
                        bubbleChart.invalidate()  // 차트 갱신하여 선택된 원에만 이름과 횟수 표시
                    }
                }

                override fun onNothingSelected() {
                    // 선택이 해제되었을 때는 이름만 표시
                    bubbleDataSet.valueFormatter = object : ValueFormatter() {
                        override fun getBubbleLabel(entry: BubbleEntry?): String {
                            return entry?.data?.toString() ?: "" // 이름만 표시
                        }
                    }
                    bubbleDataSet.setDrawValues(true) // 이름만 표시되도록 설정
                    bubbleChart.invalidate() // 차트 갱신
                }
            })

            invalidate()                         // 차트 갱신
        }
    }

    //---------------------------------------------------------------------------------

    // 누적 막대 차트를 설정하는 함수
    private fun setupStackedBarChart() {

        // 분야별로 데이터 정리
        val categories = listOf("기술 및 엔지니어링", "마케팅 및 콘텐츠", "디자인", "경영 및 컨설팅", "재무 및 법률")
        val fieldMapping = mapOf(
            "기술 및 엔지니어링" to listOf("프로덕트 매니저", "데이터 사이언티스트", "AI 엔지니어", "모바일 개발자", "네트워크 엔지니어", "소프트웨어 엔지니어"),
            "마케팅 및 콘텐츠" to listOf("콘텐츠 마케터", "소셜 미디어 전문가", "공공 관계 전문가"),
            "디자인" to listOf("디자이너", "그래픽 디자이너"),
            "경영 및 컨설팅" to listOf("HR 매니저", "전략 컨설턴트", "경영 컨설턴트", "영업 전문가"),
            "재무 및 법률" to listOf("회계사", "변호사", "재무 분석가")
        )

        // 여성과 남성 색상 정의
        val femaleColor = Color.argb(150, 186, 85, 211)  // 핑크빛이 감도는 보라색 (Alpha = 150)
        val maleColor = Color.argb(150, 0, 128, 192)   // 청록색 + 투명도 (비슷한 톤의 대비되는 색)

        // 각 분야에 대해 남성과 여성 수 계산
        val entries = categories.mapIndexed { index, category ->
            val jobs = fieldMapping[category] ?: listOf()
            val maleCount = cardDataSet.count { it.gender == "남" && it.job in jobs }.toFloat()
            val femaleCount = cardDataSet.count { it.gender == "여" && it.job in jobs }.toFloat()
            BarEntry(index.toFloat(), floatArrayOf(maleCount, femaleCount))
        }

        // 데이터셋 생성
        val dataSet = BarDataSet(entries, "성별 분포").apply {
            setColors(maleColor, femaleColor) // 남성과 여성에 대한 색상 설정
            stackLabels = arrayOf("남성", "여성")
        }

        // BarData 생성 및 차트 설정
        val barData = BarData(dataSet).apply {
            barWidth = 0.3f // 막대 굵기 설정 (기본값보다 얇게)
        }

        val stackedBarChart = findViewById<BarChart>(R.id.stacked_bar_chart)
        stackedBarChart.apply {
            data = barData
            description.isEnabled = false // 설명 비활성화

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(categories) // X축 레이블로 카테고리 설정
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f // 각 막대마다 레이블 표시
            }

            axisLeft.axisMinimum = 0f // Y축 최소값 설정
            axisRight.isEnabled = false
            legend.isEnabled = true

            // 좌우 스크롤을 가능하게 하기 위해 전체 X축 범위를 설정합니다.
            setVisibleXRangeMaximum(3f) // 최대 보이는 막대 수를 설정
            isDragEnabled = true        // 드래그(스크롤) 활성화
            xAxis.isGranularityEnabled = true

            // 차트 여백 조정
            extraBottomOffset = 10f     // X축 레이블 아래에 여백 추가
            invalidate()                // 차트 갱신
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_report)

        // 레이더 차트 참조
        val radarChart = findViewById<RadarChart>(R.id.rader_chart)

        // 직군별 데이터 수 (예제 데이터)
        val values = listOf(6f, 4f, 2f, 5f, 3f)  // 예: 기술 및 엔지니어링(6), 마케팅 및 콘텐츠(4) 등
        val maxValue = values.maxOrNull() ?: 0f // 데이터의 최대값을 찾음

        // 데이터 엔트리 생성
        val entries = values.map { RadarEntry(it) }

        // 데이터셋 생성
        val dataSet = RadarDataSet(entries, "직군 분포").apply {
            color = Color.rgb(128, 0, 128) // 보라색
            fillColor = Color.rgb(186, 85, 211)
            setDrawFilled(true)
            lineWidth = 2f
        }

        // 레이더 데이터 생성 및 적용
        val radarData = RadarData(dataSet)
        radarData.setValueTextSize(12f)
        radarData.setDrawValues(false) // 그래프 상의 값은 숨김
        radarData.setValueTextColor(Color.BLACK)

        // 차트 설정
        radarChart.apply {
            data = radarData
            description.isEnabled = false
            webColor = Color.LTGRAY // 거미줄 색상 밝게
            webColorInner = Color.LTGRAY
            webLineWidth = 1.5f // 거미줄 외곽선 두께
            webLineWidthInner = 1.2f // 내부 거미줄 두께
            webAlpha = 100 // 거미줄 투명도에따라 선을 강조

            // X축 레이블 설정
            xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    //private val labels = arrayOf("기술 및 엔지니어링", "마케팅 및 콘텐츠", "디자인", "경영 및 컨설팅", "재무 및 법률")
                    private val labels = arrayOf(
                        "기술 및 엔지니어링 (${values[0].toInt()})",
                        "마케팅 및 콘텐츠 (${values[1].toInt()})",
                        "디자인 (${values[2].toInt()})",
                        "경영 및 컨설팅 (${values[3].toInt()})",
                        "재무 및 법률 (${values[4].toInt()})"
                    )
                    override fun getFormattedValue(value: Float): String {
                        return labels[value.toInt() % labels.size]
                    }
                }
                textSize = 12f
                textColor = Color.BLACK

                // 레이블 간격 설정 (차트와 레이블 간의 거리 조정)
                yOffset = 25f // 레이블과 차트 사이 간격 설정
            }

            // Y축 설정
            yAxis.apply {
                axisMinimum = 0f
                axisMaximum = maxValue // 최대치 (필요에 따라 조정)
                setDrawLabels(false) // Y축 레이블 숨김
                spaceMin = 0.5f // 중앙에서 시작하는 거리 설정
                spaceTop = 0.5f // 최상단 값과 웹 라인 사이의 거리
                setLabelCount(6, true) // 내부 거미줄의 레벨 개수 증가
            }

            // 범례 설정
            legend.isEnabled = false

            // 차트의 여백을 최소화하여 화면 내 차트 크기 최대화
            setExtraOffsets(0f, 0f, 0f, 0f) // 좌, 상, 우, 하 여백 설정을 최소화

            invalidate() // 차트 갱신
        }


        setupTop5InteractionChart()
        setupStackedBarChart()
    }
}