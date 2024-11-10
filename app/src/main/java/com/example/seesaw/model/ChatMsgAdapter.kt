package com.example.seesaw.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seesaw.R
import com.example.seesaw.model.ChatMsg

class ChatMsgAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dataList: MutableList<ChatMsg> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addChatMsg(chatMsg: ChatMsg) {
        dataList.add(chatMsg)
        notifyItemInserted(dataList.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position].role == ChatMsg.ROLE_USER) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            MyChatViewHolder(inflater.inflate(R.layout.item_my_chat, parent, false))
        } else {
            BotChatViewHolder(inflater.inflate(R.layout.item_bot_chat, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMsg = dataList[position]
        if (chatMsg.role == ChatMsg.ROLE_USER) {
            (holder as MyChatViewHolder).setMsg(chatMsg)
        } else {
            (holder as BotChatViewHolder).setMsg(chatMsg)
        }
    }

    override fun getItemCount(): Int = dataList.size

    // 내가 보낸 메시지를 띄우기 위한 뷰홀더입니다.
    internal inner class MyChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMsg: TextView = itemView.findViewById(R.id.tv_msg)

        fun setMsg(chatMsg: ChatMsg) {
            tvMsg.text = chatMsg.content
        }
    }

    // 챗봇의 메시지를 띄우기 위한 뷰홀더입니다.
    internal inner class BotChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMsg: TextView = itemView.findViewById(R.id.tv_msg)

        fun setMsg(chatMsg: ChatMsg) {
            tvMsg.text = chatMsg.content
        }
    }
}
