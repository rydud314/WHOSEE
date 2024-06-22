package com.example.seesaw

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seesaw.databinding.ActivityChooseShareCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChooseShareCard : AppCompatActivity() {

    private lateinit var binding: ActivityChooseShareCardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter_share: ChooseShareCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseShareCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.recyclerViewShare.layoutManager = LinearLayoutManager(this)

        var myCardList = intent.getSerializableExtra("myCardList") as ArrayList<Card>
        loadCards(myCardList)

    }
    private fun loadCards(myCardList : ArrayList<Card>) {

        adapter_share = ChooseShareCardAdapter(myCardList, this)
        binding.recyclerViewShare.adapter = adapter_share
    }
}
