package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.seesaw.model.ApiClient
import com.example.seesaw.model.ChatGPTRequest
import com.example.seesaw.model.ChatGPTResponse
import com.example.seesaw.model.ChatMsg
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Report_Annual : AppCompatActivity() {
    private var isZoomedIn = false // 줌 상태를 추적하는 변수
    private val chatMsgList1: MutableList<ChatMsg> = mutableListOf()
    private val chatMsgList2: MutableList<ChatMsg> = mutableListOf()

    private var progressBar: ProgressBar? = null
    private lateinit var contentLayout: View

    private lateinit var btn_it_cards : Button
    private lateinit var btn_design_cards : Button
    private lateinit var btn_sales_cards : Button
    private lateinit var btn_etc_cards : Button

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
    // 연간 리포트 데이터 클래스
    data class AnnualData(
        val month: Int,
        var givenCards: Int = 0,
        var receivedCards: Int = 0
    )

    // cardDataSet (명함 데이터셋)
    private val cardDataSet = listOf(
        CardData("first1", "여", "백엔드 개발자", "김하영", "백엔드 개발자", "테크월드", "010-9798-8609", "kimhayoung@gmail.com", "@kimhayoung", "https://portfolio.com/kimhayoung", "서버 및 데이터베이스 관리에 주로 집중하며, 안정적이고 효율적인 백엔드 시스템을 구축하는 데 기여하고 있습니다.", 7, "received"),
        CardData("first2", "남", "디지털 마케터", "박소영", "디지털 마케터", "마케팅코리아", "010-7430-3929", "parksoyoung@naver.com", "@parksoyoung", "https://portfolio.com/parksoyoung", "온라인 마케팅 캠페인을 기획하고 분석하여 브랜드 가치를 높이고 고객과의 접점을 확대하고 있습니다.", 7, "given"),
        CardData("first3", "남", "데이터 분석가", "최민수", "데이터 분석가", "데이터랩", "010-3427-5919", "choiminsu@gmail.com", "@choiminsu", "https://portfolio.com/choiminsu", "빅데이터 분석을 통해 인사이트를 도출하며, 데이터 기반의 전략적 의사 결정을 지원하고 있습니다.", 7, "received"),
        CardData("first4", "여", "영업 전문가", "정수민", "영업 전문가", "영업솔루션", "010-4573-3602", "jungsumin@korea.com", "@jungsumin", "https://portfolio.com/jungsumin", "고객과의 원활한 소통을 통해 최적의 영업 전략을 수립하고 성과를 극대화하는 데 힘쓰고 있습니다.", 8, "given"),
        CardData("first5", "남", "시니어 개발자", "홍길동", "시니어 개발자 팀장", "테크월드", "010-5555-1234", "honggildong@techworld.com", "@honggildong", "https://portfolio.com/honggildong", "팀의 기술 리더로서, 프로젝트 설계와 구현에 대한 지침을 제공하여 성공적인 프로젝트 완수를 이끌고 있습니다.", 10, "received"),
        CardData("first6", "여", "영업 전문가", "윤서연", "영업 팀장", "세일즈매니지먼트", "010-6666-4321", "yoonseoyeon@naver.com", "@yoonseoyeon", "https://portfolio.com/yoonseoyeon", "팀원들과 협력하여 효과적인 영업 전략을 세우고, 고객 만족도를 높이는 데 주력하고 있습니다.", 7, "given"),
        CardData("first7", "남", "디지털 마케터", "권민호", "디지털 마케터", "마케팅코리아", "010-7777-5678", "kwonminho@gmail.com", "@kwonminho", "https://portfolio.com/kwonminho", "디지털 플랫폼을 활용한 캠페인을 기획하고 운영하여 브랜드 인지도를 높이는 역할을 담당하고 있습니다.", 7, "received"),
        CardData("first8", "여", "디자이너", "김민지", "제품 디자이너 실장", "콘텐츠 하우스", "010-8888-8765", "kimminji@contenthouse.com", "@kimminji", "https://portfolio.com/kimminji", "사용자 경험을 고려하여 제품 디자인을 주도하며, 기능과 미학을 결합한 디자인을 구현하고 있습니다.", 8, "given"),
        CardData("first9", "남", "데이터 분석가", "이지훈", "데이터 엔지니어", "인사이트 코어", "010-9999-5432", "leejihoon@insightcore.com", "@leejihoon", "https://portfolio.com/leejihoon", "대용량 데이터 처리 파이프라인을 구축하고 최적화하여 데이터 분석의 기반을 다지고 있습니다.", 9, "received"),
        CardData("first10", "여", "마케터", "박서현", "마케팅 전문가 대리", "마케팅코리아", "010-1010-9090", "parkseohyun@gmail.com", "@parkseohyun", "https://portfolio.com/parkseohyun", "고객 중심의 마케팅 전략을 수립하여 브랜드 가치를 높이는 데 기여하고 있습니다.", 10, "given"),
        CardData("first11", "남", "주니어 개발자", "오준서", "주니어 개발자 사원", "데브 솔루션", "010-2020-8080", "ohjunseo@gmail.com", "@ohjunseo", "https://portfolio.com/ohjunseo", "코드 작성과 디버깅을 통해 개발 프로젝트에 기여하며, 새로운 기술 학습에 열정을 쏟고 있습니다.", 11, "received"),
        CardData("first12", "남", "주니어 개발자", "오준남", "주니어 개발자 대리", "데브 솔루션", "010-3030-7070", "ohjunnam@devsolution.com", "@ohjunnam", "https://portfolio.com/ohjunnam", "프론트엔드 기술을 활용한 웹 애플리케이션 개발에 주로 기여하고 있으며, 사용자 경험 개선에 중점을 두고 있습니다.", 11, "received"),
        CardData("first13", "여", "웹 디자이너", "김서영", "웹 디자이너 실장", "데브 솔루션", "010-4040-6060", "kimseoyoung@gmail.com", "@kimseoyoung", "https://portfolio.com/kimseoyoung", "웹 디자인을 중심으로 프로젝트의 비주얼 방향을 제시하고, 완성도 높은 디자인을 목표로 작업하고 있습니다.", 8, "given"),
        CardData("first14", "여", "웹 마케터", "김서진", "웹 마케터 실장", "마케팅코리아", "010-5050-5050", "kimseojin@gmail.com", "@kimseojin", "https://portfolio.com/kimseojin", "온라인 플랫폼을 통한 마케팅 전략을 세우고, 고객 확보 및 브랜드 인지도 제고에 힘쓰고 있습니다.", 8, "received"),
        CardData("first15", "여", "웹 마케터", "김서현", "웹 마케터 팀장", "마케팅코리아", "010-6060-4040", "kimseohyun@naver.com", "@kimseohyun", "https://portfolio.com/kimseohyun", "팀을 이끌어 다양한 마케팅 전략을 실현하며, 브랜드의 온라인 영향력을 확장하고 있습니다.", 8, "received"),
        CardData("first16", "여", "프론트엔드 개발자", "김아영", "프론트엔드 개발자 사원", "테크월드", "010-7070-3030", "kimayoung@techworld.com", "@kimayoung", "https://portfolio.com/kimayoung", "UI 컴포넌트 개발을 통해 사용자 경험을 개선하며, 코드 품질 향상에 집중하고 있습니다.", 7, "given"),
        CardData("first17", "남", "프론트엔드 개발자", "김아진", "프론트엔드 개발자 사원", "테크월드", "010-8080-2020", "kimajin@gmail.com", "@kimajin", "https://portfolio.com/kimajin", "프론트엔드 개발에 주로 기여하며, 웹 사이트의 성능과 반응성을 높이기 위해 노력하고 있습니다.", 10, "received"),
        CardData("first18", "남", "영업 전문가", "김다준", "영업 전문가 대리", "영업솔루션", "010-9090-1010", "kimdajun@korea.com", "@kimdajun", "https://portfolio.com/kimdajun", "고객 관리 및 영업 전략 수립을 통해 매출 향상에 기여하며, 성과 중심의 업무에 집중하고 있습니다.", 7, "received"),
        CardData("first19", "여", "영업 전문가", "김다영", "영업 전문가 실장", "영업솔루션", "010-1111-1212", "kimdayoung@salesolution.com", "@kimdayoung", "https://portfolio.com/kimdayoung", "고객 맞춤형 솔루션 제공을 통해 신뢰를 쌓으며, 장기적인 고객 관계 형성에 기여하고 있습니다.", 9, "received"),
        CardData("first20", "여", "기자", "김지영", "연예부 팀장", "동아일보", "010-2222-1313", "kimjieyoung@donga.com", "@kimjieyoung", "https://portfolio.com/kimjieyoung", "엔터테인먼트 산업의 최신 트렌드를 취재하고 보도하여 독자에게 깊이 있는 정보를 제공하고 있습니다.", 9, "received")
    )


    // cardId email gender imgaeName introduction job name pofol position sns tel workplace

    // AnnualDataSet (연간 리포트 데이터셋, 1월~12월 초기화)
    private val annualDataSet = List(12) { month -> AnnualData(month + 1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annual_report)
        // progressBar 초기화
        progressBar = findViewById(R.id.progressBar)
        contentLayout = findViewById(R.id.contentLayout)

        // section1Content와 section2Content 텍스트뷰를 가져옵니다.
        val section1Content = findViewById<TextView>(R.id.section1Content)
        val section2Content = findViewById<TextView>(R.id.section2Content)

        // 초기에는 ProgressBar만 보이게 설정
        progressBar?.visibility = View.VISIBLE
        contentLayout.visibility = View.GONE

        // cardDataSet을 문자열로 변환하여 데이터 분석 요청 메시지 생성
        val cardDataSetText = cardDataSet.joinToString(separator = "\n") { card ->
            "ID: ${card.cardId}, 성별: ${card.gender}, 직업: ${card.job}, 이름: ${card.name}, 직급: ${card.position}, 회사: ${card.workplace}, 월: ${card.month}, 유형: ${card.type}"
        }
        // 첫 번째 메세지-분석
        val analysisRequestMessage = ChatMsg(
            ChatMsg.ROLE_USER,
            "{$cardDataSetText} 이 사용자 명함 데이터를 가지고 연간 명함 사용량 및 어떤 분야의 직군과 가장 많이 공유했는지, " +
                    "저장된 명함의 gender 속성을 보고 남여 성비를 판단하고, 저장된 명함의 직군 분포에 대한 분석은 꼭 포함해줘. " +
                    "그리고 부가적으로 사용자의 명함 데이터를 보고 더 설명해주고 싶은 것을 구체적으로 분석해줘. " +
                    "꼭 너의 다른 말 없이 소제목도 없이 분석 내용만 크게 (명함 사용량에 대한 내용/명함 분포도에 대한 내용) 2문단으로만 " +
                    "\"\\n\\n\"으로 스플릿 할 수 있도록 답변해줘. " +
                    "그리고 시간이 걸리더라도 남여 성비는 gender 속성을 보고 꼭 정확하게 잘 세어줘. 갯수 틀리면 안돼. " +
                    "공손히 '습니다'체로 말해줘. "
        )
        chatMsgList1.add(analysisRequestMessage)
        // 두 번째 메시지-분류
        val classificationRequestMessage = ChatMsg(
            ChatMsg.ROLE_USER,
            "{$cardDataSetText} 이 데이터셋을 보고 'IT 관련 직군', '디자인 관련 직군', '영업 관련 직군', 그 외는 '기타 직군' 4그룹으로 분류해줘. " +
                    "꼭 너의 다른 말 없이 소제목도 없이 분류된 데이터셋을 직군마다 \"\\n\\n\"으로 스플릿 할 수 있도록 답변해줘. " +
                    "분류된 각각 데이터는 \"\\n\"로 구분해주고 " +
                    "분류된 각각 데이터의 모든 칼럼 사이에는 \",\"로 구분해줘. " +
                    "같은 직군이면 같은 그룹으로 정확하게 분류되도록 해야 해. 중요해. "
        )
        chatMsgList2.add(classificationRequestMessage)


        sendMsgToChatGPT(section1Content, section2Content)

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

        // 직업군을 분류하는 함수
        fun classifyJob(job: String): String {
            return when {
                "디자이너" in job -> "디자이너"
                "개발자" in job -> "개발자"
                "영업 전문가" in job -> "영업 전문가"
                "데이터 분석가" in job -> "데이터 분석가"
                "마케터" in job -> "마케터"
                else -> "기타"
            }
        }

        // 직업군 차트 데이터 생성
        val jobCounts = cardDataSet
            .groupBy { classifyJob(it.job) } // classifyJob 함수를 사용해 분류
            .mapValues { it.value.size }
        //val jobCounts = cardDataSet.groupingBy { it.job }.eachCount()
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




        // IT 관련 직군 명함 - 필터링
        val itCards = cardDataSet.filter { it.job.contains("개발자") || it.job.contains("데이터") }
        btn_it_cards = findViewById(R.id.btn_it_cards)
        btn_it_cards.setOnClickListener {
            val dialog = Frag_Professional_Wallet.newInstance(itCards)
            dialog.show(supportFragmentManager, "Frag_It_Wallet")
        }

        // 디자인 관련 직군 명함 - 필터링
        val designCards = cardDataSet.filter { it.job.contains("디자이너")}
        btn_design_cards = findViewById(R.id.btn_design_cards)
        btn_design_cards.setOnClickListener {
            val dialog = Frag_Professional_Wallet.newInstance(designCards)
            dialog.show(supportFragmentManager, "Frag_Design_Wallet")
        }

        // 영업 관련 직군 명함 - 필터링
        val salesCards = cardDataSet.filter { it.job.contains("영업") }
        btn_sales_cards = findViewById(R.id.btn_sales_cards)
        btn_sales_cards.setOnClickListener {
            val dialog = Frag_Professional_Wallet.newInstance(salesCards)
            dialog.show(supportFragmentManager, "Frag_Sales_Wallet")
        }

        // 기타 직군 명함 - 필터링
        val etcCards = cardDataSet.filter {
            !it.job.contains("개발자") &&
                    !it.job.contains("데이터") &&
                    !it.job.contains("디자이너") &&
                    !it.job.contains("영업")
        }
        btn_etc_cards = findViewById(R.id.btn_etc_cards)
        btn_etc_cards.setOnClickListener {
            val dialog = Frag_Professional_Wallet.newInstance(etcCards)
            dialog.show(supportFragmentManager, "Frag_Etc_Wallet")
        }

    }


    // sendMsgToChatGPT 함수에서 지피티에게 메시지를 보낼 때 프로그레스바 표시/숨기기
    private fun sendMsgToChatGPT(section1Content: TextView, section2Content: TextView) {
        val api = ApiClient.getChatGPTApi()
        val request = ChatGPTRequest("gpt-4o-mini", chatMsgList1)
        val classificationRequest = ChatGPTRequest("gpt-4o-mini", chatMsgList2)

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
                    Toast.makeText(this@Report_Annual, "응답을 받지 못했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChatGPTResponse?>, t: Throwable) {
                Log.e("getChatResponse", "onFailure: ", t)
                // 실패했을 때도 프로그레스바를 숨기고 화면 터치를 다시 활성화
                progressBar?.visibility = View.GONE
                contentLayout.visibility = View.VISIBLE // 실패 시 콘텐츠 보이도록 설정
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Toast.makeText(this@Report_Annual, "응답을 받지 못했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            }
        })

        //분류
        api.getChatResponse(classificationRequest)?.enqueue(object : Callback<ChatGPTResponse?> {
            override fun onResponse(call: Call<ChatGPTResponse?>, response: Response<ChatGPTResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    val classificationResponse = response.body()?.choices?.get(0)?.message?.content
                    if (classificationResponse != null) {
                        // 두 번째 응답을 로그캣에만 출력합니다.
                        Log.d("ClassificationResponse", classificationResponse)
                    }
                } else {
                    Log.e("ClassificationResponse", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ChatGPTResponse?>, t: Throwable) {
                Log.e("ClassificationResponse", "onFailure: ", t)
            }
        })
    }

}