package com.example.seesaw

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.seesaw.databinding.ActivityMessageBinding
import com.example.seesaw.model.ChatModel
import com.example.seesaw.model.ChatModel.Comment
import com.example.seesaw.model.Friend
import com.google.android.gms.tasks.OnCompleteListener
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.auth.oauth2.GoogleCredentials
import java.io.FileInputStream
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.api.client.json.jackson2.JacksonFactory
import okhttp3.*
import java.util.*

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomUid: String? = null
    private var destinationUid: String? = null
    private var otherUserFcmToken: String? = null
    var firestore = FirebaseFirestore.getInstance()

    private var uid: String? = null
    private var recyclerView: RecyclerView? = null
    var peopleCount = 0

//    private lateinit var googleCredential: GoogleCredential


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMessageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

//        // OAuth 2.0 인증 설정 및 토큰 자동 갱신 설정
//        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
//        val jsonFactory = JacksonFactory.getDefaultInstance()
//
//        googleCredential = GoogleCredential.getApplicationDefault(httpTransport, jsonFactory)
//            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

        val sendBtn = binding.sendBtn
        val messageText = binding.messageText

        destinationUid = intent.getStringExtra("destinationUid")
        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = binding.messageActivityRecyclerview
        Log.v("체크용2", uid.toString())
        Log.v("체크용3", Firebase.auth.currentUser.toString())

        // 상대방의 FCM 토큰을 가져오기 위한 코드 추가
        val otherUserRef =
            fireDatabase.child("users").child(destinationUid!!).child("fcmToken")
        otherUserRef.get().addOnSuccessListener { dataSnapshot ->
            otherUserFcmToken = dataSnapshot.getValue(String::class.java)
        }

        Log.e("otherUserFcmToken", "otherUserFcmToken: $otherUserFcmToken")



        sendBtn.setOnClickListener {
            if (messageText.text.isNotEmpty()) {
                val chatModel = ChatModel()
                chatModel.users[uid.toString()] = true
                chatModel.users[destinationUid!!] = true
                val time = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("yy.MM.dd.kk:mm")
                val curTime = dateFormat.format(Date(time)).toString()
                //
                val comment = Comment(uid, messageText.text.toString(), curTime)

//                // 상대방의 FCM 토큰을 가져오기 위한 코드 추가
//                val otherUserRef =
//                    fireDatabase.child("users").child(destinationUid!!).child("fcmToken")
//                otherUserRef.get().addOnSuccessListener { dataSnapshot ->
//                    otherUserFcmToken = dataSnapshot.getValue(String::class.java)
//                }
//
//                Log.e("otherUserFcmToken", "otherUserFcmToken: $otherUserFcmToken")


                if (chatRoomUid == null) {
                    sendBtn.isEnabled = false
                    fireDatabase.child("chatrooms").push().setValue(chatModel)
                        .addOnSuccessListener {
                            //채팅방 생성
                            checkChatRoom()
                            //메세지 보내기
                            Handler().postDelayed({
                                fireDatabase.child("chatrooms").child(chatRoomUid.toString())
                                    .child("comments").push().setValue(comment)

                                val client = OkHttpClient()

                                val root = JSONObject()
                                val message = JSONObject()
                                val notification = JSONObject()
                                val android = JSONObject()
                                notification.put("title", getString(R.string.app_name))
                                notification.put("body", messageText.text.toString())
                                android.put("priority", "high")
                                message.put("notification", notification)
                                message.put("android", android)
                                message.put("token", otherUserFcmToken)
                                root.put("message", message)

//                                val FCM_OAuth_Access_Token = googleCredential.accessToken
                                val FCM_OAuth_Access_Token = "ya29.a0AcM612xzwwSqQoWYnpsOKei9POGhL-KCa69l7-6Bn7hZvxkLLPAPI7YgMFLtdc4mRjLo7GT3wpAGnV96ck1NbrtgoKtmKgJ-ItwDWzyAFtiFxV-fcTy41zlx-xhxY9gkluDyWfM5rsC90uG0pqEbJWUl7AgWgPEF5ZvLCJc6aCgYKAVoSARASFQHGX2MiFN5bHhbDJhaJY5EOrGDBFw0175"

                                val requestBody = root.toString()
                                    .toRequestBody("application/json; charset=utf-8".toMediaType())
                                val request = Request.Builder().post(requestBody)
                                    .url("https://fcm.googleapis.com/v1/projects/card-93c5d/messages:send")
                                    .header("Authorization", "Bearer $FCM_OAuth_Access_Token")
                                    .build()
                                client.newCall(request).enqueue(object : Callback {
                                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                                        // 요청 실패 시 처리
                                        Log.e("FCM", "Failed to send message: ${e.message}")
                                    }

                                    override fun onResponse(
                                        call: okhttp3.Call,
                                        response: okhttp3.Response
                                    ) {
                                        // 요청 성공 시 처리
                                        if (response.isSuccessful) {
                                            Log.d("FCM", "Message sent successfully")
                                        } else {
                                            Log.e("FCM", "Failed to send message: ${response.code}")
                                        }
                                    }
                                })

                                messageText.text = null


                            }, 1000L)
                        }
                } else {
                    fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments")
                        .push().setValue(comment)

//                    // 상대방의 FCM 토큰을 가져오기 위한 코드 추가
//                    val otherUserRef =
//                        fireDatabase.child("users").child(destinationUid!!).child("fcmToken")
//                    otherUserRef.get().addOnSuccessListener { dataSnapshot ->
//                        otherUserFcmToken = dataSnapshot.getValue(String::class.java)
//                    }
//                    Log.e("otherUserFcmToken", "otherUserFcmToken: $otherUserFcmToken")


                    val client = OkHttpClient()

                    val root = JSONObject()
                    val message = JSONObject()
                    val notification = JSONObject()
                    val android = JSONObject()
                    notification.put("title", getString(R.string.app_name))
                    notification.put("body", messageText.text.toString())
                    android.put("priority", "high")

                    message.put("notification", notification)
                    message.put("android", android)
                    message.put("token", otherUserFcmToken)

                    root.put("message", message)

//                    val FCM_OAuth_Access_Token = googleCredential.accessToken
                    val FCM_OAuth_Access_Token = "ya29.a0AcM612xzwwSqQoWYnpsOKei9POGhL-KCa69l7-6Bn7hZvxkLLPAPI7YgMFLtdc4mRjLo7GT3wpAGnV96ck1NbrtgoKtmKgJ-ItwDWzyAFtiFxV-fcTy41zlx-xhxY9gkluDyWfM5rsC90uG0pqEbJWUl7AgWgPEF5ZvLCJc6aCgYKAVoSARASFQHGX2MiFN5bHhbDJhaJY5EOrGDBFw0175"


                    val requestBody = root.toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaType())
                    val request = Request.Builder().post(requestBody)
                        .url("https://fcm.googleapis.com/v1/projects/card-93c5d/messages:send")
                        .header("Authorization", "Bearer $FCM_OAuth_Access_Token")
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: okhttp3.Call, e: IOException) {
                            // 요청 실패 시 처리
                            Log.e("FCM", "Failed to send message: ${e.message}")
                        }

                        override fun onResponse(
                            call: okhttp3.Call,
                            response: okhttp3.Response
                        ) {
                            // 요청 성공 시 처리
                            if (response.isSuccessful) {
                                Log.d("FCM", "Message sent successfully")
                            } else {
                                Log.e("FCM", "Failed to send message: ${response.code}")
                            }
                        }
                    })

                    messageText.text = null
                    Log.d("chatUidNotNull dest", "$destinationUid")
                }
            }
        }
        checkChatRoom()


    }

    private fun checkChatRoom() {
        fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        val chatModel = item.getValue<ChatModel>()
                        if (chatModel?.users!!.containsKey(destinationUid)) {
                            chatRoomUid = item.key
                            binding.sendBtn.isEnabled = true
                            recyclerView?.layoutManager = LinearLayoutManager(this@MessageActivity)
                            recyclerView?.adapter = RecyclerViewAdapter(this@MessageActivity)
                        }
                    }
                }
            })
    }

    inner class RecyclerViewAdapter(val context: Context) :
        RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        private val comments = ArrayList<Comment>()
        private var friend: Friend? = null


        init {

            firestore.collection("id_list").document(destinationUid.toString()).get()
                .addOnSuccessListener { document ->
                    friend = Friend(
                        document["email"].toString(),
                        document["name"].toString(),
                        document["imageName"].toString(),
                        document["cardId"].toString(),
                        document["job"].toString()
                    )
                    binding.messageScreenTop.text = document["name"].toString()
                    getMessageList()
                }.addOnFailureListener { Log.d(ContentValues.TAG, "querysnapshot 실패") }

        }

        private fun getMessageList() {
            val sharedPreferences = context.getSharedPreferences("other", 0)
            val editor = sharedPreferences.edit()
            if (sharedPreferences.getString("userState", "").toString() == "Login") {
                fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            comments.clear()
                            var readUsersMap: HashMap<String, Any> = HashMap()
                            for (data in snapshot.children) {
                                var key = data.key
                                var commentOrigin = data.getValue<Comment>()!!
                                var commentModify = data.getValue<Comment>()!!
                                commentModify.readUsers?.put(uid.toString(), true)
                                readUsersMap.put(key.toString(), commentModify)
                                comments.add(commentOrigin)
                            }

                            if (comments.size > 0 && sharedPreferences.getString("userState", "")
                                    .toString() == "Login"
                            ) {
                                Log.v("마지막", "!!!!")
                                if (!comments.get(comments.size - 1).readUsers!!.containsKey(uid)) {
                                    fireDatabase.child("chatrooms").child(chatRoomUid.toString())
                                        .child("comments").updateChildren(readUsersMap)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                notifyDataSetChanged() //메시지 갱신
                                                recyclerView?.scrollToPosition(comments.size - 1)     //메세지를 보낼 시 화면을 맨 밑으로 내림
                                            }
                                        }
                                } else {
                                    notifyDataSetChanged() //메시지 갱신
                                    recyclerView?.scrollToPosition(comments.size - 1)
                                }
                            }
                        }
                    })
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

            return MessageViewHolder(view)
        }

        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.messageRightArriveTime.text = ""
            holder.messageReadCountRight.text = ""
            holder.messageRightContent.text = ""
            holder.messageRightContent.background = null

            holder.messageLeftArriveTime.text = ""
            holder.messageReadCountLeft.text = ""
            holder.messageLeftContent.text = ""
            holder.messageLeftContent.background = null
            holder.userName.text = ""
            holder.userImage.visibility = View.INVISIBLE

            val layoutParams = holder.layout_main.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = 0
            holder.layout_main.layoutParams = layoutParams
            holder.messageLeftArriveTime.text = ""

            if (position == 0 || comments[position - 1].time.toString()
                    .substring(0, 9) != comments[position].time.toString().substring(0, 9)
            ) {
                holder.chatDate.text = comments[position].time.toString().substring(0, 8)
                holder.chatDate.textSize = 9.9f
            } else {
                holder.chatDate.text = ""
                holder.chatDate.textSize = 0f
            }



            if (comments[position].uid.equals(uid)) // 내 채팅
            {
                holder.messageRightContent.textSize = 11.8F
                holder.messageRightContent.text = comments[position].message
                holder.messageRightContent.setTextColor(Color.parseColor("#FFFFFFFF"))
                holder.messageRightContent.setBackgroundResource(R.drawable.message_right_chat)

                setReadCounter(position, holder.messageReadCountRight)

                if (comments.size - 1 > position) {
                    if (comments[position].time.toString() == comments[position + 1].time.toString() && comments[position + 1].uid.equals(
                            uid
                        )
                    ) {
                        val layoutParams =
                            holder.layout_main.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.bottomMargin = -57
                        holder.layout_main.layoutParams = layoutParams
                        holder.messageRightArriveTime.text = ""
                    } else
                        holder.messageRightArriveTime.text =
                            comments[position].time.toString().substring(9)
                } else { //마지막 대화인 경우
                    holder.messageRightArriveTime.text =
                        comments[position].time.toString().substring(9)
                }

            } else// 상대방 채팅
            {
                holder.messageLeftContent.textSize = 11.8F
                holder.messageLeftContent.text = comments[position].message
                holder.messageLeftContent.setTextColor(Color.parseColor("#FF545F71"))
                holder.messageLeftContent.setBackgroundResource(R.drawable.message_left_chat)

                setReadCounter(position, holder.messageReadCountLeft)

                if (comments.size - 1 > position || position == 0) {
                    if (position == 0 || comments[position - 1].uid.equals(uid) || comments[position].time.toString() != comments[position - 1].time.toString()) {
                        holder.userName.text = friend?.friendName
                        holder.userImage.visibility = View.VISIBLE
                        Glide.with(holder.itemView.context).load(friend?.friendImageName)
                            .apply(RequestOptions().circleCrop()).into(holder.userImage)
                    }

                    if (position != 0 && (comments[position].time.toString() == comments[position + 1].time.toString() && !comments[position + 1].uid.equals(
                            uid
                        ))
                    ) {
                        val layoutParams =
                            holder.layout_main.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.bottomMargin = -57
                        holder.layout_main.layoutParams = layoutParams
                        holder.messageLeftArriveTime.text = ""
                    } else {
                        holder.messageLeftArriveTime.text =
                            comments[position].time.toString().substring(9)
                    }
                } else { //마지막 대화인 경우
                    if (comments[position - 1].uid.equals(uid) || (comments[position].time.toString() != comments[position - 1].time.toString())) {
                        holder.userName.text = friend?.friendName
                        holder.userImage.visibility = View.VISIBLE
                        Glide.with(holder.itemView.context).load(friend?.friendImageName)
                            .apply(RequestOptions().circleCrop()).into(holder.userImage)
                        holder.messageLeftArriveTime.text =
                            comments[position].time.toString().substring(9)
                    } else {
                        holder.messageLeftArriveTime.text =
                            comments[position].time.toString().substring(9)
                    }
                }

            }


        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val messageLeftContent: TextView = view.findViewById(R.id.messageLeftContent)
            val messageRightContent: TextView = view.findViewById(R.id.messageRightContent)
            val userName: TextView = view.findViewById(R.id.userName)
            val userImage: ImageView = view.findViewById(R.id.userImage)
            val layout_destination: LinearLayout =
                view.findViewById(R.id.messageItem_layout_destination)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val messageLeftArriveTime: TextView = view.findViewById(R.id.messageLeftArriveTime)
            val messageRightArriveTime: TextView = view.findViewById(R.id.messageRightArriveTime)
            val messageReadCountLeft: TextView = view.findViewById(R.id.messageReadCountLeft)
            val messageReadCountRight: TextView = view.findViewById(R.id.messageReadCountRight)
            val chatDate: TextView = view.findViewById(R.id.chatDate)

        }

        fun setReadCounter(position: Int, textView: TextView) {
            if (peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms")
                    .child(chatRoomUid.toString()).child("users")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val users = snapshot.value as Map<String, Boolean>
                            peopleCount = users.size
                            var count = peopleCount - comments[position].readUsers!!.size
                            if (count > 0) {
                                textView.visibility = View.VISIBLE
                                textView.text = count.toString()
                            } else
                                textView.visibility = View.INVISIBLE
                        }
                    })
            } else {
                var count = peopleCount - comments[position].readUsers!!.size
                if (count > 0) {
                    textView.visibility = View.VISIBLE
                    textView.text = count.toString()
                } else
                    textView.visibility = View.INVISIBLE
            }


        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val sharedPreferences = this.getSharedPreferences("other", 0)
        val editor = sharedPreferences.edit()
        editor.putString("userState", "back")
        editor.apply()


    }

}