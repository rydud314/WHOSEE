package com.example.seesaw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ChooseShareCardAdapter(private val cardList: List<Card>, private val context: Context) : RecyclerView.Adapter<ChooseShareCardAdapter.CardViewHolder>() {
/*
    companion object {
        private lateinit var arguments: Bundle
        private const val ARG_USER = "user_key"

        fun shareInstance(shareCard: Card): ChooseShareCardAdapter {
            val fragment = ChooseShareCardAdapter
            val bundle = Bundle().apply {
                putParcelable(ARG_USER, shareCard)
            }
            fragment.arguments = bundle
            return fragment
        }
    }*/
    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val name: TextView = view.findViewById(R.id.name)
        val position: TextView = view.findViewById(R.id.position)
        val workplace: TextView = view.findViewById(R.id.workplace)
    }

    lateinit var viewModel_share : CardViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.name.text = card.name
        holder.position.text = card.position
        holder.workplace.text = card.workplace
        var Frag3_Share = Frag3_Share()
        val bundle = Bundle()


        // Glide를 사용하여 이미지를 로드하고 동그랗게 자릅니다.
        Glide.with(context)
            .load(card.imageName)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(holder.profileImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, Frag3_Share::class.java).apply {
                putExtra("c", card)

            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = cardList.size
}
