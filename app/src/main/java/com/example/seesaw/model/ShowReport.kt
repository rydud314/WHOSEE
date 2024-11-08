package com.example.seesaw.model


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seesaw.R
import android.view.View  // Add this import
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowReport : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatMsgAdapter
    private lateinit var btnSend: Button
    private lateinit var etMsg: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var chatMsgList: MutableList<ChatMsg>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_report)

        // 뷰 객체 연결
        recyclerView = findViewById(R.id.recyclerView)
        btnSend = findViewById(R.id.btn_send)
        etMsg = findViewById(R.id.et_msg)
        progressBar = findViewById(R.id.progressBar)

        // 채팅 메시지 데이터를 담을 list 생성
        chatMsgList = mutableListOf()

        // 리사이클러뷰 초기화
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = ChatMsgAdapter()
        adapter.dataList = chatMsgList // setDataList 대신 프로퍼티로 설정
        recyclerView.adapter = adapter

        // 메시지 전송버튼 클릭 리스너 설정
        btnSend.setOnClickListener {
            val msg = etMsg.text.toString()
            if (msg.isNotEmpty()) {
                // 새로운 ChatMsg 객체를 생성하여 어댑터에 추가합니다.
                val userMessage = ChatMsg(ChatMsg.ROLE_USER, msg)
                adapter.addChatMsg(userMessage)
                etMsg.setText(null) // 입력창 초기화

                // 키보드 숨기기
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                // 로딩 바 보이기
                progressBar.visibility = ProgressBar.VISIBLE

                // API 요청 보내기
                sendMsgToChatGPT()
            } else {
                Toast.makeText(this, "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // EditText 객체에 text가 변경될 때 실행될 리스너 설정
        etMsg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                btnSend.isEnabled = s?.length ?: 0 > 0
            }
        })
    }

    private fun sendMsgToChatGPT() {
        // Retrofit 객체 초기화
        val api = ApiClient.getChatGPTApi()

        // ChatGPTRequest 객체 생성 (기존 메시지 리스트 전달)
        val request = ChatGPTRequest(
            "gpt-4o-mini", // 모델
            chatMsgList
        )

        // API 호출
        api.getChatResponse(request)?.enqueue(object : Callback<ChatGPTResponse?> {
            override fun onResponse(call: Call<ChatGPTResponse?>, response: Response<ChatGPTResponse?>) {
                // 응답을 성공적으로 받은 경우
                if (response.isSuccessful && response.body() != null) {
                    val chatResponse = response.body()?.choices?.get(0)?.message?.content
                    // 리사이클러뷰에 답변 추가하기
                    if (chatResponse != null) {
                        adapter.addChatMsg(ChatMsg(ChatMsg.ROLE_ASSISTANT, chatResponse))
                    }
                    // 로딩바 숨기기
                    progressBar.visibility = View.INVISIBLE  // GONE 대신 INVISIBLE로 변경

                    // 화면 터치 차단 해제
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                } else {
                    Log.e("getChatResponse", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ChatGPTResponse?>, t: Throwable) {
                Log.e("getChatResponse", "onFailure: ", t)
            }
        })

    }

    override fun onStart() {
        super.onStart()
    }
}
