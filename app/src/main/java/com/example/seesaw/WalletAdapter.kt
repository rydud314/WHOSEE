package com.example.seesaw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class WalletAdapter(private val cardList: List<Card>, private val context: Context) : RecyclerView.Adapter<WalletAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val name: TextView = view.findViewById(R.id.name)
        val position: TextView = view.findViewById(R.id.position)
        val workplace: TextView = view.findViewById(R.id.workplace)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.name.text = card.name
        holder.position.text = card.position
        holder.workplace.text = card.workplace

        // Glide를 사용하여 이미지를 로드하고 동그랗게 자릅니다.
        Glide.with(context)
            .load(card.imageName)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(holder.profileImage)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("card", card)

            Log.d(TAG, "쉐어리사이클러 Card이름= ${card.cardId}")

            val intent = Intent(context, NameCardDetailOthers::class.java)
            intent.putExtra("card", card)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = cardList.size
}
