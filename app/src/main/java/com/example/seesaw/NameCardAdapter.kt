package com.example.seesaw

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NameCardAdapter(private val cards: List<NameCardData>) : RecyclerView.Adapter<NameCardAdapter.NameCardViewHolder>() {

    class NameCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val jobTextView: TextView = view.findViewById(R.id.tv_job)
        val nameTextView: TextView = view.findViewById(R.id.tv_name)
        val addressTextView: TextView = view.findViewById(R.id.tv_address)
        val ageGenderTextView: TextView = view.findViewById(R.id.tv_age_gender)
        val curJobTextView: TextView = view.findViewById(R.id.tv_season)
        val emailTextView: TextView = view.findViewById(R.id.tv_email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_card, parent, false)
        return NameCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: NameCardViewHolder, position: Int) {
        val card = cards[position]
        holder.jobTextView.text = card.job
        holder.nameTextView.text = card.name
        holder.addressTextView.text = card.address
        holder.ageGenderTextView.text = card.age + "/" +card.Gender
        holder.curJobTextView.text = card.annual
        holder.emailTextView.text = card.email

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NameCardDetail::class.java)
            intent.putExtra("card", card)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = cards.size
}
