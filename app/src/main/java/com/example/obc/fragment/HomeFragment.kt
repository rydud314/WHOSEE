package com.example.obc.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.obc.MessageActivity
import com.example.obc.model.Friend
import com.example.obc.R
import com.example.obc.databinding.FragmentHomeBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!
    var firestore = FirebaseFirestore.getInstance()
    private var friendArray : ArrayList<Friend> = arrayListOf()
    lateinit var friendInfoQuery : QuerySnapshot

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
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("other", 0)
        val editor = sharedPreferences.edit()
        CoroutineScope(Dispatchers.IO).launch {
            runBlocking {
                getFriendInfo(sharedPreferences.getString("userEmail", "").toString())
            }
            for(document in friendInfoQuery){
                friendArray.add(
                    Friend(document["friendEmail"].toString(),document["friendName"].toString(), document["friendProfileImageUrl"].toString(),
                        document["friendUid"].toString(),document["friendState"].toString())
                )
            }
            requireActivity().runOnUiThread{
                binding.homeRecycler.layoutManager = LinearLayoutManager(requireContext())
                binding.homeRecycler.adapter = RecyclerViewAdapter() //recyclerView에 어댑터 설정
            }
        }
        return binding.root
    }


    inner class RecyclerViewAdapter  : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.home_item_iv)
            val textView : TextView = itemView.findViewById(R.id.home_item_tv)
            val textViewEmail : TextView = itemView.findViewById(R.id.home_item_email)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

            Glide.with(holder.itemView.context).load(friendArray[position].friendProfileImageUrl).apply(RequestOptions().circleCrop()).into(holder.imageView)
            holder.textView.text = friendArray[position].friendName
            holder.textViewEmail.text = friendArray[position].friendEmail

            holder.itemView.setOnClickListener{
                val intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("destinationUid", friendArray[position].friendUid)
                context?.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return friendArray.size
        }
    }

    suspend fun getFriendInfo(email:String): Boolean {
        return try {
            var state=false
            firestore.collection(email).get()
                .addOnSuccessListener { result -> // 성공할 경우
                    friendInfoQuery = result
                }
                .addOnFailureListener{
                }.await()
            state
        }catch (e: FirebaseException){
            false
        }
    }
}