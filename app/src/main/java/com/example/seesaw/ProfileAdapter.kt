package com.example.seesaw

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView

class ProfileAdapter(val profilelist: ArrayList<Profiles>) : RecyclerView.Adapter<ProfileAdapter.CustomeViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.CustomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return profilelist.size
    }

    override fun onBindViewHolder(holder: ProfileAdapter.CustomeViewHolder, position: Int) {
        holder.profile_img.setImageResource(profilelist.get(position).profile_img)
        holder.name.text = profilelist.get(position).name
        holder.state_msg.text = profilelist.get(position).state_msg
    }


    class CustomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val profile_img = itemView.findViewById<ImageView>(R.id.iv_profile) //프로필 사진
        val name = itemView.findViewById<TextView>(R.id.tv_profile_name) //프로필 이름
        val state_msg = itemView.findViewById<TextView>(R.id.tv_state_msg) //상태 메시지
    }

}