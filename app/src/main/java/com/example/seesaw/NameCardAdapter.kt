package com.example.seesaw

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NameCardAdapter(private val cards: List<Card>) : RecyclerView.Adapter<NameCardAdapter.NameCardViewHolder>() {

    class NameCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val jobTextView: TextView = view.findViewById(R.id.tv_job)
        val nameTextView: TextView = view.findViewById(R.id.tv_name)
        val workplaceTextView: TextView = view.findViewById(R.id.tv_workplace)
        val genderTextView: TextView = view.findViewById(R.id.tv_gender)
        val positionTextView: TextView = view.findViewById(R.id.tv_position)
        val emailTextView: TextView = view.findViewById(R.id.tv_email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_card, parent, false)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return NameCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: NameCardViewHolder, position: Int) {
        val card = cards[position]
        holder.jobTextView.text = card.job
        holder.nameTextView.text = card.name
        holder.workplaceTextView.text = card.workplace
        holder.genderTextView.text = card.gender
        holder.positionTextView.text = card.position
        holder.emailTextView.text = card.email

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NameCardDetailMine::class.java)
            intent.putExtra("card", card)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = cards.size
}

data class NameCardData(
    val name: String,
    val position: String,
    val workplace: String,
    val gender: String,
    val job: String,
    val email: String
)
