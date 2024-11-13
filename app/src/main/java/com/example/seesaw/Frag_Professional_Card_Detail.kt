package com.example.seesaw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class Frag_Professional_Card_Detail(private val card: Report_Annual.CardData) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_professional_card_detail, container, false)

        // View elements
        val cardImageView: ImageView = view.findViewById(R.id.card_image)
        val nameTextView: TextView = view.findViewById(R.id.tv_name)
        val nameTextView2: TextView = view.findViewById(R.id.tv_name2)
        val telTextView: TextView = view.findViewById(R.id.tv_tel)
        val snsTextView: TextView = view.findViewById(R.id.tv_sns)
        val portfolioTextView: TextView = view.findViewById(R.id.tv_portfolio)
        val jobTextView: TextView = view.findViewById(R.id.tv_job)
        val positionTextView: TextView = view.findViewById(R.id.tv_position)
        val workplaceTextView: TextView = view.findViewById(R.id.tv_workplace)
        val genderTextView: TextView = view.findViewById(R.id.tv_gender)
        val emailTextView: TextView = view.findViewById(R.id.tv_email) // 추가된 이메일 필드
        val introductionTextView: TextView = view.findViewById(R.id.tv_introduction) // 추가된 소개 필드
        val chatButton: Button = view.findViewById(R.id.btn_chat)

        // Set data for each TextView
        nameTextView.text = card.name
        nameTextView2.text = card.name
        telTextView.text = String.format("Tel: %s", card.tel)
        snsTextView.text = String.format("SNS: %s", card.sns)
        portfolioTextView.text = String.format("Portfolio: %s", card.pofol)
        jobTextView.text = card.job
        positionTextView.text = card.position
        workplaceTextView.text = card.workplace
        genderTextView.text = card.gender
        emailTextView.text = String.format("Email: %s", card.email) // 이메일 텍스트 설정
        introductionTextView.text = card.introduction // 소개 텍스트 설정

        // Load image from drawable using cardId
        val imageResource = resources.getIdentifier(card.cardId, "drawable", requireContext().packageName)
        if (imageResource != 0) { // If image resource is found
            cardImageView.setImageResource(imageResource)
        } else {
            // Set a default image if the specific image is not found
            cardImageView.setImageResource(R.drawable.card_face)
        }

        // Chat button action (implement your chat functionality here)
        chatButton.setOnClickListener {
            // Start chat functionality here
        }

        return view
    }

    companion object {
        fun newInstance(card: Report_Annual.CardData): Frag_Professional_Card_Detail {
            return Frag_Professional_Card_Detail(card)
        }
    }
}
