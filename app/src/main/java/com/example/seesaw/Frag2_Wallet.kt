package com.example.seesaw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Frag2_Wallet : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_frag2_wallet, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 샘플 데이터
        val cardList = listOf(
            Card("김나연", "Software Engineer", "영남대학교"),
            Card("김현수", "Graphic Designer", "영남대학교")
            // 카드 추가 여기서 하기
        )

        recyclerView.adapter = CardAdapter(cardList)
        return view
    }
    companion object{
        fun newInstance() : Frag2_Wallet{
            return Frag2_Wallet()
        }
    }
}