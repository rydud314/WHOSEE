package com.example.seesaw

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val cardList: List<Card>, private val context: Context) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val name: TextView = view.findViewById(R.id.name)
        val position: TextView = view.findViewById(R.id.position)
        val workplace: TextView = view.findViewById(R.id.workplace)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.name.text = card.name
        holder.position.text = card.position
        holder.workplace.text = card.workplace
        card.index = position

        holder.itemView.setOnClickListener{
            val intent = Intent(context, NameCardDetailOthers::class.java).apply {
                putExtra("card", card)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = cardList.size
}
