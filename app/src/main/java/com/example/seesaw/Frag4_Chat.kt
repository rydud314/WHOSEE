package com.example.seesaw

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.seesaw.MessageActivity
import com.example.seesaw.R
import com.example.seesaw.databinding.FragmentChatBinding
import com.example.seesaw.model.ChatModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.Collections.reverseOrder
import kotlin.collections.ArrayList

class Frag4_Chat : Fragment() {
    companion object{
        fun newInstance() : Frag4_Chat{
            return Frag4_Chat()
        }
    }
    private var _binding: FragmentChatBinding?= null
    private val binding get() = _binding!!
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    var firestore = FirebaseFirestore.getInstance()

    private val chatModel = ArrayList<ChatModel>()
    private var uid : String? = null
    private val destinationUsers : ArrayList<String> = arrayListOf()
    private var chatRoomUid : String? = null
    private var noReadCount = 0

    private var noReadCountList = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

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
                if (user != uid) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }


            //
            //변경
            //
            //이준현 추가 - 이 부분이 임시로 수정한건데 일단 계정단위 친구여서 어떤 카드로 친구추가한지 몰라서 id_list에서 이름이랑 사진을 들고왔습니다
            firestore.collection("id_list").document(destinationUid.toString()).get().addOnSuccessListener {document->
                Glide.with(holder.itemView.context).load( document["imageName"].toString()).into(holder.userImage)
                holder.userImage.clipToOutline = true
                holder.userName.text = document["name"].toString()
            }.addOnFailureListener{ Log.d(ContentValues.TAG, "querysnapshot 실패")}

            //메세지 내림차순 정렬 후 마지막 메세지의 키값을 가져옴

            val commentMap = TreeMap<String, ChatModel.Comment>(Collections.reverseOrder())
            commentMap.putAll(chatModel[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]

            setReadCounter(position,holder.userNoReadCount,holder.userLastChatContent, holder.userLastChatTime)

            //채팅창 선택 시 이동
            holder.itemView.setOnClickListener {
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
