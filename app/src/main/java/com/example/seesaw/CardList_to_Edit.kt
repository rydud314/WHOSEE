package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seesaw.databinding.ActivityFrag2WalletBinding

class CardList_to_Edit : AppCompatActivity(){

    private lateinit var binding: ActivityFrag2WalletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrag2WalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 리사이클러뷰 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // 샘플 데이터
        val cardList = listOf(
            Card("구교영", "Software Engineer", "영남대학교"),
            Card("홍아랑", "Software Engineer", "영남대학교"),
            Card("박해세", "Software Engineer", "영남대학교"),
            Card("Lucy", "Software Engineer", "영남대학교"),
            Card("홍알랑", "Software Engineer", "영남대학교"),
            Card("꼬랑지", "Software Engineer", "영남대학교"),
            Card("김나연", "Software Engineer", "영남대학교"),
            Card("구영교", "Software Engineer", "영남대학교"),
            Card("김현수", "Graphic Designer", "영남대학교")
            // 카드 추가 여기서 하기
        )

        binding.recyclerView.adapter = CardAdapter(cardList, this)
    }

}