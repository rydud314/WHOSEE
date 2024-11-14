package com.example.seesaw

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.model.ApiClient
import com.example.seesaw.model.ChatGPTRequest
import com.example.seesaw.model.ChatGPTResponse
import com.example.seesaw.model.ChatMsg
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Report_Monthly : AppCompatActivity(){

    private val chatMsgList: MutableList<ChatMsg> = mutableListOf()
    private var progressBar: ProgressBar? = null
    private lateinit var contentLayout: View

    // 명함 데이터 클래스
    data class CardData(
        val cardId: String,
        val gender: String,
        val job: String,
        val name: String,
        val position: String,
        val workplace: String,
        val tel: String,       // 전화번호 필드 추가
        val email: String, // 이메일 필드 추가
        val sns: String,       // SNS 필드 추가
        val pofol: String,      // Portfolio 필드 추가
        val introduction: String, // 소개 필드 추가

        val month: Int, // 명함 주고받은 달 (1~12)
        val type: String // "given" 또는 "received"로 명함이 주어진 유형
    )

    // cardDataSet (명함 데이터셋)
    private val cardDataSet = listOf(
        CardData("first1", "여", "백엔드 개발자", "김하영", "백엔드 개발자", "테크월드", "010-9798-8609", "kimhayoung@gmail.com", "@kimhayoung", "https://portfolio.com/kimhayoung", "서버 및 데이터베이스 관리에 주로 집중하며, 안정적이고 효율적인 백엔드 시스템을 구축하는 데 기여하고 있습니다.", 11, "received"),
        CardData("first1", "여", "백엔드 개발자", "김하영", "백엔드 개발자", "테크월드", "010-9798-8609", "kimhayoung@gmail.com", "@kimhayoung", "https://portfolio.com/kimhayoung", "서버 및 데이터베이스 관리에 주로 집중하며, 안정적이고 효율적인 백엔드 시스템을 구축하는 데 기여하고 있습니다.", 7, "given"),
        CardData("first1", "여", "백엔드 개발자", "김하영", "백엔드 개발자", "테크월드", "010-9798-8609", "kimhayoung@gmail.com", "@kimhayoung", "https://portfolio.com/kimhayoung", "서버 및 데이터베이스 관리에 주로 집중하며, 안정적이고 효율적인 백엔드 시스템을 구축하는 데 기여하고 있습니다.", 11, "received"),
        CardData("first1", "여", "백엔드 개발자", "김하영", "백엔드 개발자", "테크월드", "010-9798-8609", "kimhayoung@gmail.com", "@kimhayoung", "https://portfolio.com/kimhayoung", "서버 및 데이터베이스 관리에 주로 집중하며, 안정적이고 효율적인 백엔드 시스템을 구축하는 데 기여하고 있습니다.", 7, "received"),

        CardData("first2", "남", "디지털 마케터", "박소영", "디지털 마케터", "마케팅코리아", "010-7430-3929", "parksoyoung@naver.com", "@parksoyoung", "https://portfolio.com/parksoyoung", "온라인 마케팅 캠페인을 기획하고 분석하여 브랜드 가치를 높이고 고객과의 접점을 확대하고 있습니다.", 7, "given"),
        CardData("first2", "남", "디지털 마케터", "박소영", "디지털 마케터", "마케팅코리아", "010-7430-3929", "parksoyoung@naver.com", "@parksoyoung", "https://portfolio.com/parksoyoung", "온라인 마케팅 캠페인을 기획하고 분석하여 브랜드 가치를 높이고 고객과의 접점을 확대하고 있습니다.", 11, "given"),
        CardData("first2", "남", "디지털 마케터", "박소영", "디지털 마케터", "마케팅코리아", "010-7430-3929", "parksoyoung@naver.com", "@parksoyoung", "https://portfolio.com/parksoyoung", "온라인 마케팅 캠페인을 기획하고 분석하여 브랜드 가치를 높이고 고객과의 접점을 확대하고 있습니다.", 7, "received"),
        CardData("first2", "남", "디지털 마케터", "박소영", "디지털 마케터", "마케팅코리아", "010-7430-3929", "parksoyoung@naver.com", "@parksoyoung", "https://portfolio.com/parksoyoung", "온라인 마케팅 캠페인을 기획하고 분석하여 브랜드 가치를 높이고 고객과의 접점을 확대하고 있습니다.", 7, "given"),

        CardData("first3", "남", "데이터 분석가", "최민수", "데이터 분석가", "데이터랩", "010-3427-5919", "choiminsu@gmail.com", "@choiminsu", "https://portfolio.com/choiminsu", "빅데이터 분석을 통해 인사이트를 도출하며, 데이터 기반의 전략적 의사 결정을 지원하고 있습니다.", 7, "received"),
        CardData("first3", "남", "데이터 분석가", "최민수", "데이터 분석가", "데이터랩", "010-3427-5919", "choiminsu@gmail.com", "@choiminsu", "https://portfolio.com/choiminsu", "빅데이터 분석을 통해 인사이트를 도출하며, 데이터 기반의 전략적 의사 결정을 지원하고 있습니다.", 11, "received"),

        CardData("first4", "여", "영업 전문가", "정수민", "영업 전문가", "영업솔루션", "010-4573-3602", "jungsumin@korea.com", "@jungsumin", "https://portfolio.com/jungsumin", "고객과의 원활한 소통을 통해 최적의 영업 전략을 수립하고 성과를 극대화하는 데 힘쓰고 있습니다.", 8, "given"),
        CardData("first4", "여", "영업 전문가", "정수민", "영업 전문가", "영업솔루션", "010-4573-3602", "jungsumin@korea.com", "@jungsumin", "https://portfolio.com/jungsumin", "고객과의 원활한 소통을 통해 최적의 영업 전략을 수립하고 성과를 극대화하는 데 힘쓰고 있습니다.", 11, "given"),

        CardData("first5", "남", "백엔드 개발자", "홍길동", "시니어 개발자 팀장", "테크월드", "010-5555-1234", "honggildong@techworld.com", "@honggildong", "https://portfolio.com/honggildong", "팀의 기술 리더로서, 프로젝트 설계와 구현에 대한 지침을 제공하여 성공적인 프로젝트 완수를 이끌고 있습니다.", 10, "received"),
        CardData("first5", "남", "백엔드 개발자", "홍길동", "시니어 개발자 팀장", "테크월드", "010-5555-1234", "honggildong@techworld.com", "@honggildong", "https://portfolio.com/honggildong", "팀의 기술 리더로서, 프로젝트 설계와 구현에 대한 지침을 제공하여 성공적인 프로젝트 완수를 이끌고 있습니다.", 10, "received"),

        CardData("first6", "여", "영업 전문가", "윤서연", "영업 팀장", "세일즈매니지먼트", "010-6666-4321", "yoonseoyeon@naver.com", "@yoonseoyeon", "https://portfolio.com/yoonseoyeon", "팀원들과 협력하여 효과적인 영업 전략을 세우고, 고객 만족도를 높이는 데 주력하고 있습니다.", 11, "given"),
        CardData("first7", "남", "디지털 마케터", "권민호", "디지털 마케터", "마케팅코리아", "010-7777-5678", "kwonminho@gmail.com", "@kwonminho", "https://portfolio.com/kwonminho", "디지털 플랫폼을 활용한 캠페인을 기획하고 운영하여 브랜드 인지도를 높이는 역할을 담당하고 있습니다.", 7, "received"),
        CardData("first8", "여", "디자이너", "김민지", "제품 디자이너 실장", "콘텐츠 하우스", "010-8888-8765", "kimminji@contenthouse.com", "@kimminji", "https://portfolio.com/kimminji", "사용자 경험을 고려하여 제품 디자인을 주도하며, 기능과 미학을 결합한 디자인을 구현하고 있습니다.", 8, "given"),
        CardData("first9", "남", "데이터 분석가", "이지훈", "데이터 엔지니어", "인사이트 코어", "010-9999-5432", "leejihoon@insightcore.com", "@leejihoon", "https://portfolio.com/leejihoon", "대용량 데이터 처리 파이프라인을 구축하고 최적화하여 데이터 분석의 기반을 다지고 있습니다.", 9, "received"),
        CardData("first10", "여", "마케터", "박서현", "마케팅 전문가 대리", "마케팅코리아", "010-1010-9090", "parkseohyun@gmail.com", "@parkseohyun", "https://portfolio.com/parkseohyun", "고객 중심의 마케팅 전략을 수립하여 브랜드 가치를 높이는 데 기여하고 있습니다.", 11, "given"),
        CardData("first11", "남", "주니어 개발자", "오준서", "주니어 개발자 사원", "데브 솔루션", "010-2020-8080", "ohjunseo@gmail.com", "@ohjunseo", "https://portfolio.com/ohjunseo", "코드 작성과 디버깅을 통해 개발 프로젝트에 기여하며, 새로운 기술 학습에 열정을 쏟고 있습니다.", 11, "received"),
        CardData("first12", "남", "주니어 개발자", "오준남", "주니어 개발자 대리", "데브 솔루션", "010-3030-7070", "ohjunnam@devsolution.com", "@ohjunnam", "https://portfolio.com/ohjunnam", "프론트엔드 기술을 활용한 웹 애플리케이션 개발에 주로 기여하고 있으며, 사용자 경험 개선에 중점을 두고 있습니다.", 11, "received"),
        CardData("first13", "여", "웹 디자이너", "김서영", "웹 디자이너 실장", "데브 솔루션", "010-4040-6060", "kimseoyoung@gmail.com", "@kimseoyoung", "https://portfolio.com/kimseoyoung", "웹 디자인을 중심으로 프로젝트의 비주얼 방향을 제시하고, 완성도 높은 디자인을 목표로 작업하고 있습니다.", 8, "given"),
        CardData("first14", "여", "웹 마케터", "김서진", "웹 마케터 실장", "마케팅코리아", "010-5050-5050", "kimseojin@gmail.com", "@kimseojin", "https://portfolio.com/kimseojin", "온라인 플랫폼을 통한 마케팅 전략을 세우고, 고객 확보 및 브랜드 인지도 제고에 힘쓰고 있습니다.", 8, "received"),
        CardData("first15", "여", "웹 마케터", "김서현", "웹 마케터 팀장", "마케팅코리아", "010-6060-4040", "kimseohyun@naver.com", "@kimseohyun", "https://portfolio.com/kimseohyun", "팀을 이끌어 다양한 마케팅 전략을 실현하며, 브랜드의 온라인 영향력을 확장하고 있습니다.", 11, "received"),
        CardData("first16", "여", "프론트엔드 개발자", "김아영", "프론트엔드 개발자 사원", "테크월드", "010-7070-3030", "kimayoung@techworld.com", "@kimayoung", "https://portfolio.com/kimayoung", "UI 컴포넌트 개발을 통해 사용자 경험을 개선하며, 코드 품질 향상에 집중하고 있습니다.", 7, "given"),
        CardData("first17", "남", "프론트엔드 개발자", "김아진", "프론트엔드 개발자 사원", "테크월드", "010-8080-2020", "kimajin@gmail.com", "@kimajin", "https://portfolio.com/kimajin", "프론트엔드 개발에 주로 기여하며, 웹 사이트의 성능과 반응성을 높이기 위해 노력하고 있습니다.", 10, "received"),
        CardData("first18", "남", "영업 전문가", "김다준", "영업 전문가 대리", "영업솔루션", "010-9090-1010", "kimdajun@korea.com", "@kimdajun", "https://portfolio.com/kimdajun", "고객 관리 및 영업 전략 수립을 통해 매출 향상에 기여하며, 성과 중심의 업무에 집중하고 있습니다.", 11, "received"),
        CardData("first19", "여", "영업 전문가", "김다영", "영업 전문가 실장", "영업솔루션", "010-1111-1212", "kimdayoung@salesolution.com", "@kimdayoung", "https://portfolio.com/kimdayoung", "고객 맞춤형 솔루션 제공을 통해 신뢰를 쌓으며, 장기적인 고객 관계 형성에 기여하고 있습니다.", 9, "received"),
        CardData("first20", "여", "기자", "김지영", "연예부 팀장", "동아일보", "010-2222-1313", "kimjieyoung@donga.com", "@kimjieyoung", "https://portfolio.com/kimjieyoung", "엔터테인먼트 산업의 최신 트렌드를 취재하고 보도하여 독자에게 깊이 있는 정보를 제공하고 있습니다.", 11, "received")
    )

    //---------------------------------------------------------------------------------
    // 교류 많았던 5명
    private fun setupTop5InteractionChart() {
        val bubbleChart = findViewById<BubbleChart>(R.id.top5_interaction_chart)

        // 교류 상위 5인 동적으로 계산
        val top5Interactions = cardDataSet
            .groupingBy { it.name }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(5) // 상위 5명만 추출

        // BubbleEntry 리스트 생성
        val bubbleEntries = top5Interactions.mapIndexed { index, (name, interactionCount) ->
            val xPosition = index * 0.6f  // X 좌표를 0.6씩 증가시켜 원들이 더 많이 겹치도록 설정
            BubbleEntry(xPosition, 0f, interactionCount.toFloat()).apply {
                data = name
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

    // 누적 막대 차트
    private fun setupStackedBarChart() {
        val stackedBarChart = findViewById<BarChart>(R.id.stacked_bar_chart)

        // 직업군 동적으로 분류
        val categories = cardDataSet.groupBy { it.job }
        val entries = categories.keys.mapIndexed { index, job ->
            val maleCount = categories[job]?.count { it.gender == "남" }?.toFloat() ?: 0f
            val femaleCount = categories[job]?.count { it.gender == "여" }?.toFloat() ?: 0f
            BarEntry(index.toFloat(), floatArrayOf(maleCount, femaleCount))
        }

        // 여성과 남성 색상 정의
        val femaleColor = Color.argb(150, 186, 85, 211)  // 핑크빛이 감도는 보라색 (Alpha = 150)
        val maleColor = Color.argb(150, 0, 128, 192)   // 청록색 + 투명도 (비슷한 톤의 대비되는 색)

        // 데이터셋 생성
        val dataSet = BarDataSet(entries, "성별 분포").apply {
            setColors(maleColor, femaleColor) // 남성과 여성에 대한 색상 설정
            stackLabels = arrayOf("남성", "여성")
        }

        // BarData 생성 및 차트 설정
        val barData = BarData(dataSet).apply {
            barWidth = 0.3f // 막대 굵기 설정 (기본값보다 얇게)
        }

        stackedBarChart.apply {
            data = barData
            description.isEnabled = false // 설명 비활성화

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(categories.keys.toList().map { it.toString() })
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f // 각 막대마다 레이블 표시
            }

            axisLeft.axisMinimum = 0f // Y축 최소값 설정
            axisRight.isEnabled = false
            legend.isEnabled = true

            // 좌우 스크롤을 가능하게 하기 위해 전체 X축 범위를 설정합니다.
            setVisibleXRangeMaximum(categories.size.toFloat()) // 최대 보이는 막대 수를 설정
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
        // progressBar 초기화
        progressBar = findViewById(R.id.progressBar)
        contentLayout = findViewById(R.id.contentLayout)

        // section1Content와 section2Content 텍스트뷰를 가져옵니다.
        val section1Content = findViewById<TextView>(R.id.section1Content)
        val section3Content2 = findViewById<TextView>(R.id.section3Content2)

        // 초기에는 ProgressBar만 보이게 설정
        progressBar?.visibility = View.VISIBLE
        contentLayout.visibility = View.GONE

        // cardDataSet을 문자열로 변환하여 데이터 분석 요청 메시지 생성
        val cardDataSetText = cardDataSet.joinToString(separator = "\n") { card ->
            "ID: ${card.cardId}, 성별: ${card.gender}, 직업: ${card.job}, 이름: ${card.name}, 직급: ${card.position}, 회사: ${card.workplace}, 월: ${card.month}, 유형: ${card.type}"
        }

        // 메세지-분석
        val analysisRequestMessage = ChatMsg(
            ChatMsg.ROLE_USER,
            "{$cardDataSetText} 이 사용자 명함 데이터에서 11월 데이터를 기준으로 " +
                    "첫 번째 문단에는 아래와 같이 3가지 주제로 구성하여 명함 사용량에 대한 분석 내용을 제공해줘.:\n" +
                    "1. 이번달 명함 사용량 분석 - 이번달에 주고받은 전체 명함 수와 지난달과의 비교 분석, 다양한 네트워킹 활동의 결과로 해석해줘.\n" +
                    "2. 직군별 명함 공유 현황 - 이번달 가장 많이 공유된 직군과 해당 직군에 대한 분석.\n" +
                    "3. 성비 분석 - 직군 별로 이번달 주고받은 명함에 대해 명함의 gender 속성을 바탕으로 남녀 성비, 이를 통해 유추할 수 있는 교류 현황.\n" +
                    "4. 추천 사항 - 사용자에게 적합한 네트워킹 추천.\n" +
                    "두 번째 문단에는 아래와 같이 3가지 주제로 구성하여 명함 교류 분포에 대한 분석 내용을 제공해줘.:\n" +
                    "1. 교류 분석 - 교류가 많았던 최대 5명에 대한 명함 교류 정보와 대체적인 직군을 분석.\n" +
                    "2. 추천 사항 - 교류가 많았던 직군 외의 다양한 직군과의 네트워킹을 위한 제안.\n" +
                    "각 문단은 순수하게 분석 내용만 포함하고, 다른 설명 없이 아래와 같은 형식으로 작성해줘:\n\n" +
                    "👀WHOSEE👀가 제공하는 명함 사용량에 대한 분석💜\n" +
                    "📌주제⭐\n️" +
                    "   - 상세 내용\n" +
                    "📌주제⭐️\n" +
                    "   - 상세 내용\n" +
                    "📌주제⭐️\n" +
                    "   - 상세 내용\n" +
                    "💡앞으로 이렇게 하면 좋을 것 같아요!💡\n" +
                    "   - 상세 내용\n\n" +
                    "👀WHOSEE👀가 제공하는 명함 교류 분포에 대한 분석💜\n" +
                    "📌주제⭐️\n" +
                    "   - 상세 내용\n" +
                    "💡앞으로 이렇게 하면 좋을 것 같아요!!💡️\n" +
                    "   - 상세 내용\n" +
                    "->이 형식대로 가독성 좋게 정리해줘. (\uD83D\uDC40WHOSEE\uD83D\uDC40가 제공하는 명함 사용량에 대한 분석\uD83D\uDC9C에 대한 내용 문단, \uD83D\uDC40WHOSEE\uD83D\uDC40가 제공하는 명함 교류 분포에 대한 분석\uD83D\uDC9C에 대한 내용 문단) 이 두 문단 사이엔 꼭 \"\\n\\n\"으로 구분하고, '습니다' 또는 '해요' 체로 말해줘. 성비 계산은 정확하게 해줘."
        )
        chatMsgList.add(analysisRequestMessage)
        sendMsgToChatGPT(section1Content, section3Content2)

        // 레이더 차트 참조
        val radarChart = findViewById<RadarChart>(R.id.rader_chart)

        // 직군별 데이터 수 동적 계산 후 상위 4개 추출하고 나머지는 기타로 그룹화
        val jobCounts = cardDataSet.groupingBy { it.job }.eachCount()
        val sortedJobCounts = jobCounts.entries.sortedByDescending { it.value }
        val top4Jobs = sortedJobCounts.take(4)
        val otherCount = sortedJobCounts.drop(4).sumOf { it.value }
        val jobData = top4Jobs.map { it.key to it.value } + ("기타" to otherCount)  // Explicitly creating a List<Pair<String, Int>>

        // 레이더 차트 데이터 엔트리 생성
        val entries = jobData.map { RadarEntry(it.second.toFloat()) }
        val maxValue = entries.maxOfOrNull { it.value } ?: 0f

        // 데이터셋 생성
        val dataSet = RadarDataSet(entries, "직군 분포").apply {
            color = Color.rgb(128, 0, 128)
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
            data = RadarData(dataSet).apply {
                setValueTextSize(12f)
                setDrawValues(false)
                setValueTextColor(Color.BLACK)
            }
            description.isEnabled = false
            webColor = Color.LTGRAY // 거미줄 색상 밝게
            webColorInner = Color.LTGRAY
            webLineWidth = 1.5f // 거미줄 외곽선 두께
            webLineWidthInner = 1.2f // 내부 거미줄 두께
            webAlpha = 100 // 거미줄 투명도에따라 선을 강조

            // X축 레이블 설정
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(jobData.map { "${it.first} (${it.second}명)" }) // Adding count to labels
                textSize = 12f
                textColor = Color.BLACK
                yOffset = 25f
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
    // sendMsgToChatGPT 함수에서 지피티에게 메시지를 보낼 때 프로그레스바 표시/숨기기
    private fun sendMsgToChatGPT(section1Content: TextView, section2Content: TextView) {
        val api = ApiClient.getChatGPTApi()
        val request = ChatGPTRequest("gpt-4o-mini", chatMsgList)

        // 로딩 시작 시 프로그레스바를 보이고 화면 터치를 비활성화
        progressBar?.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        //분석
        api.getChatResponse(request)?.enqueue(object : Callback<ChatGPTResponse?> {
            override fun onResponse(call: Call<ChatGPTResponse?>, response: Response<ChatGPTResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    val chatResponse = response.body()?.choices?.get(0)?.message?.content
                    if (chatResponse != null) {
                        Log.d("getChatResponse", "${chatResponse}")

                        val responseParts = chatResponse.split("\n\n")
                        section1Content.text = responseParts.getOrNull(0) ?: "데이터를 분석하는 데 실패했습니다."
                        section2Content.text = responseParts.getOrNull(1) ?: "추가 분석 결과를 불러오지 못했습니다."
                    }

                    // 성공 시 화면 설정
                    progressBar?.visibility = View.GONE
                    contentLayout.visibility = View.VISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                } else {
                    Log.e("getChatResponse", "Error: ${response.message()}")
                    progressBar?.visibility = View.GONE
                    contentLayout.visibility = View.VISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    Toast.makeText(this@Report_Monthly, "응답을 받지 못했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChatGPTResponse?>, t: Throwable) {
                Log.e("getChatResponse", "onFailure: ", t)
                // 실패했을 때도 프로그레스바를 숨기고 화면 터치를 다시 활성화
                progressBar?.visibility = View.GONE
                contentLayout.visibility = View.VISIBLE // 실패 시 콘텐츠 보이도록 설정
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Toast.makeText(this@Report_Monthly, "응답을 받지 못했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}