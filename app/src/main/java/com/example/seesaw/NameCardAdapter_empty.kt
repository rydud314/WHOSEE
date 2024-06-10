package com.example.seesaw

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NameCardAdapterEmpty : RecyclerView.Adapter<NameCardAdapterEmpty.EmptyCardViewHolder>() {

    class EmptyCardViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_card_empty, parent, false)
        return EmptyCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmptyCardViewHolder, position: Int) {
        // 명함이 없기 때문에 바인딩할 내용이 없습니다.

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MakeCard::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return 1 // 명함이 없을 때는 하나의 빈 명함만 표시합니다.
    }
}
