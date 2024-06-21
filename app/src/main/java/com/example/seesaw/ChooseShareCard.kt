package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seesaw.databinding.ActivityChooseShareCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChooseShareCard : AppCompatActivity() {

    private lateinit var binding: ActivityChooseShareCardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ChooseShareCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseShareCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        var myCardList = intent.getSerializableExtra("myCardList") as ArrayList<Card>
        loadCards(myCardList)
    }

    private fun loadCards(myCardList : ArrayList<Card>) {

        adapter = ChooseShareCardAdapter(myCardList, this)
        binding.recyclerView.adapter = adapter
    }
}
