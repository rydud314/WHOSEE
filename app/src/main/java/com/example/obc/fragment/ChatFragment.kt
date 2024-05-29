package com.example.obc.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.obc.MessageActivity
import com.example.obc.R
import com.example.obc.databinding.FragmentChatBinding
import com.example.obc.model.ChatModel
import com.example.obc.model.Friend
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.Collections.reverse
import java.util.Collections.reverseOrder
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatFragment : Fragment() {

    companion object {
        interface OnItemLongClickListener {
            fun onItemLongClicked(position: Int): Boolean
        }
    }
/*
    companion object{
                fun newInstance() : ChatFragment {
            return ChatFragment()
        }
    }*/
    private var _binding: FragmentChatBinding?= null
    private val binding get() = _binding!!
    private val fireDatabase = FirebaseDatabase.getInstance().reference

    private val chatModel = ArrayList<ChatModel>()
    private var uid : String? = null
    private val destinationUsers : ArrayList<String> = arrayListOf()
    private var chatRoomUid : String? = null
    private var noReadCount = 0
    private var noReadCountSum = 0
    private var noReadCountList = arrayListOf<Int>()

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    @SuppressLint("ResourceType", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentChatBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("other", 0)
        val editor = sharedPreferences.edit()

        binding.chatfragmentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.chatfragmentRecyclerview.adapter = RecyclerViewAdapter()


        binding.auction.setOnClickListener {
            binding.auction.setTextColor(Color.parseColor("#FF545F71"))
            binding.nameCredit.setTextColor(Color.parseColor("#FF9BA5B7"))
        }

        binding.nameCredit.setOnClickListener {
            binding.nameCredit.setTextColor(Color.parseColor("#FF545F71"))
            binding.auction.setTextColor(Color.parseColor("#FF9BA5B7"))
        }

        binding.searchOption1.setOnClickListener {
            binding.searchOption1.setBackgroundResource(R.drawable.inside_only_bottom_bold)
            binding.searchOption1.setTextColor(Color.parseColor("#FF545F71"))
            binding.searchOption2.background = null
            binding.searchOption2.setTextColor(Color.parseColor("#FF9BA5B7"))
        }
        binding.searchOption2.setOnClickListener {
            binding.searchOption1.background = null
            binding.searchOption1.setTextColor(Color.parseColor("#FF9BA5B7"))
            binding.searchOption2.setBackgroundResource(R.drawable.inside_only_bottom_bold)
            binding.searchOption2.setTextColor(Color.parseColor("#FF545F71"))
        }

        if(sharedPreferences.getString("userState", "").toString() == "back"){

        }
        var optionSetting = arrayListOf<String>("시간순","이름순")
        binding.chatSpinner.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_message, optionSetting)
        return binding.root
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {


        init {
            uid = Firebase.auth.currentUser?.uid.toString()

            fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModel.clear()
                    for(data in snapshot.children){
                        chatModel.add(data.getValue<ChatModel>()!!)
                    }
                    notifyDataSetChanged()
                }
            })
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val userImage: ImageView = itemView.findViewById(R.id.userImage)
            val userName : TextView = itemView.findViewById(R.id.userName)
            val userLastChatContent : TextView = itemView.findViewById(R.id.userLastChatContent)
            val userLastChatTime : TextView = itemView.findViewById(R.id.userLastChatTime)
            val userNoReadCount : TextView = itemView.findViewById(R.id.noReadCount)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, @SuppressLint("RecyclerView") position: Int) {
            var destinationUid: String? = null
            //채팅방에 있는 유저 모두 체크
            for (user in chatModel[position].users.keys) {
                if (!user.equals(uid)) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }
            fireDatabase.child("users").child("$destinationUid").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val friend = snapshot.getValue<Friend>()
                    Glide.with(holder.itemView.context).load(friend?.friendProfileImageUrl).into(holder.userImage)
                    holder.userImage.clipToOutline = true
                    holder.userName.text = friend?.friendName
                }
            })
            //메세지 내림차순 정렬 후 마지막 메세지의 키값을 가져옴

            val commentMap = TreeMap<String, ChatModel.Comment>(reverseOrder())
            commentMap.putAll(chatModel[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]

            setReadCounter(position,holder.userNoReadCount,holder.userLastChatContent, holder.userLastChatTime)


            //채팅창 선택 시 이동
            holder.itemView.setOnClickListener {
                Log.v("클릭", "")
                val sharedPreferences = requireActivity().getSharedPreferences("other", 0)
                val editor = sharedPreferences.edit()
                val intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                editor.putString("userState","Login")
                editor.apply()
                context?.startActivity(intent)
            }

        }

        private fun setReadCounter(position: Int, noReadCountTextView: TextView, userLastChatContentTextView: TextView, userLastChatTimeTextView: TextView){
            fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children){
                        val chatModel = item.getValue<ChatModel>()
                        if(chatModel?.users!!.containsKey(destinationUsers[position])){
                            chatRoomUid = item.key
                            break
                        }
                    }
                    fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var userLastChatTimeTemp = ""
                            var userLastChatContentTemp = ""
                            for(data in snapshot.children){
                                var commentOrigin =data.getValue<ChatModel.Comment>()!!
                                if(commentOrigin.readUsers?.get(uid.toString())!=true)
                                    noReadCount+=1
                                userLastChatContentTemp = commentOrigin.message.toString()
                                userLastChatTimeTemp= commentOrigin.time.toString()
                            }

                            if(noReadCountList.size != chatModel.size){
                                for (i in 1..chatModel.size - noReadCountList.size)
                                    noReadCountList.add(0)
                            }
                            if(userLastChatContentTemp.length>13)
                                userLastChatContentTextView.text = userLastChatContentTemp.substring(0,13)+"..."
                            else
                                userLastChatContentTextView.text = userLastChatContentTemp
                            if(userLastChatTimeTemp.length>9)
                                userLastChatTimeTextView.text = userLastChatTimeTemp.substring(9)
                            else
                                userLastChatTimeTextView.text = ""

                            if(noReadCount>0){
                                noReadCountTextView.text = noReadCount.toString()
                                if(noReadCountList[position]!=noReadCount){
                                    noReadCountList[position] = noReadCount
                                    binding.noReadCountSum.text = "     안 읽은 메시지 : " + noReadCountList.sum().toString()
                                }
                                else{
                                }
                                noReadCount=0
                            }
                            else{
                                noReadCountTextView.text = ""
                                noReadCountList[position] = 0
                            }
                            notifyDataSetChanged()

                        }
                    })
                }
            })

        }
        override fun getItemCount(): Int {
            return chatModel.size
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }
}