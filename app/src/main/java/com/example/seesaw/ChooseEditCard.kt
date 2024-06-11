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

        loadCards()
    }

    private fun loadCards() {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid.toString()

        if (uid.isNotEmpty()) {
            firestore.collection("my_card_list").document(uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        val cards = document.get("cards") as? List<Map<String, Any>>
                        if (cards != null) {
                            val cardList = mutableListOf<Card>()
                            for (card in cards) {
                                val cardId = card["cardId"].toString()
                                // 여기서 cardId로 명함 상세 정보를 가져와서 cardList에 추가
                                firestore.collection("all_card_list").document(cardId).get().addOnSuccessListener { doc ->
                                    if (doc != null) {
                                        val name = doc.getString("name") ?: ""
                                        val position = doc.getString("position") ?: ""
                                        val workplace = doc.getString("workplace") ?: ""
                                        val email = doc.getString("email") ?: ""
                                        val gender = doc.getString("gender") ?: ""
                                        val imageName = doc.getString("imageName") ?: ""
                                        val introduction = doc.getString("introduction") ?: ""
                                        val job = doc.getString("job") ?: ""
                                        val pofol = doc.getString("pofol") ?: ""
                                        val sns = doc.getString("sns") ?: ""
                                        val tel = doc.getString("tel") ?: ""

                                        val cardItem = Card(name, position, workplace, email, cardId, gender, imageName, introduction, job, pofol, sns, tel)
                                        cardList.add(cardItem)

                                        // 어댑터 설정
                                        if (cardList.size == cards.size) {
                                            adapter = ChooseEditCardAdapter(cardList, this)
                                            binding.recyclerView.adapter = adapter
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
