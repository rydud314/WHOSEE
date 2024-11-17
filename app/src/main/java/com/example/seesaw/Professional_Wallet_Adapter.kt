package com.example.seesaw

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seesaw.Report_Annual.CardData
import androidx.fragment.app.FragmentManager

abstract class Professional_Wallet_Adapter(
    private val cards: List<CardData>,
    private val fragmentManager: FragmentManager // FragmentManager를 전달받음
) : RecyclerView.Adapter<Professional_Wallet_Adapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card)

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            // CardDetailFragment를 보여줌
            val dialog = Frag_Professional_Card_Detail.newInstance(card)
            dialog.show(fragmentManager, "Frag_IT_Card_Detal")
        }
    }

    override fun getItemCount(): Int = cards.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        private val nameText: TextView = itemView.findViewById(R.id.name)
        private val positionText: TextView = itemView.findViewById(R.id.position)
        private val workplaceText: TextView = itemView.findViewById(R.id.workplace)

        fun bind(card: CardData) {
            val context = itemView.context
            val drawableId = context.resources.getIdentifier(card.cardId, "drawable", context.packageName)

            if (drawableId != 0) {
                profileImage.setImageResource(drawableId) // cardId에 맞는 이미지 설정
            } else {
                profileImage.setImageResource(R.drawable.ic_profile_placeholder) // 기본 이미지 설정
            }

            // 원형 이미지로 설정
            profileImage.apply {
                clipToOutline = true
                background = context.getDrawable(R.drawable.card_face) // card_face.xml을 통해 원형 배경 설정
            }

            nameText.text = card.name
            positionText.text = card.position
            workplaceText.text = card.workplace
        }
    }
}
