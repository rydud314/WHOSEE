package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seesaw.databinding.ActivityChooseEditCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChooseEditCard : AppCompatActivity() {

    private lateinit var binding: ActivityChooseEditCardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ChooseEditCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseEditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        var myCardList = intent.getSerializableExtra("myCardList") as ArrayList<Card>
        loadCards(myCardList)
    }

    private fun loadCards(myCardList : ArrayList<Card>) {

        adapter = ChooseEditCardAdapter(myCardList, this)
        binding.recyclerView.adapter = adapter
    }
}
