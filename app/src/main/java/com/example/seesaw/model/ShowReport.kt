package com.example.seesaw.model

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seesaw.R
import com.example.seesaw.model.ChatMsg
import java.util.ArrayList

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
            // etMsg에 쓰여있는 텍스트를 가져옵니다.
            val msg = etMsg.text.toString()
            if (msg.isNotEmpty()) {
                // 새로운 ChatMsg 객체를 생성하여 어댑터에 추가합니다.
                adapter.addChatMsg(ChatMsg(ChatMsg.ROLE_USER, msg))
                // etMsg의 텍스트를 초기화합니다.
                etMsg.setText(null)
                // 키보드를 내립니다.
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            } else {
                Toast.makeText(this, "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // EditText 객체에 text가 변경될 때 실행될 리스너 설정
        etMsg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 입력창에 메시지가 입력되었을 때만 버튼이 클릭 가능하도록 설정
                btnSend.isEnabled = s?.length ?: 0 > 0
            }
        })
    }

    override fun onStart() {
        super.onStart()

        // 테스트를 위한 더미 데이터 생성
        // i가 짝수일 경우 내 메시지, 홀수일 경우 챗봇의 메시지로 생성되도록 10개의 채팅 메시지 객체를 만들어 리스트에 넣습니다.
        for (i in 0 until 10) {
            chatMsgList.add(ChatMsg(if (i % 2 == 0) ChatMsg.ROLE_USER else ChatMsg.ROLE_ASSISTANT, "메시지 $i"))
        }
    }
}
