//package com.example.seesaw
//
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.seesaw.databinding.ActivityFrag2WalletBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class CardList_to_Edit : AppCompatActivity(){
//
//    private lateinit var binding: ActivityFrag2WalletBinding
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityFrag2WalletBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // 리사이클러뷰 설정
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//
//        firestore = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//
//        val currentUser = auth.currentUser
//        val uid = currentUser?.uid.toString()
//        Log.d("CardList_to_Edit", "UID =  $uid")
//
//        var cardId = ""
//        var card_list = mutableListOf<String>()
//
//        val myCardList : MutableList<Card> = mutableListOf()
//        myCardList.clear()
//
//        if (uid != null) {
//            firestore.collection("wallet_list").document(uid).get().addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val document = task.result
//                    if (document != null && document.exists()) {
//                        val cards = document.get("cards") as? List<Map<String, Any>>
//                        if (cards != null) {
//                            for (card in cards) {
//                                cardId = card["cardId"].toString()
//                                Log.d("CardList_to_Edit", "cardId = $cardId")
//                                card_list.add(cardId)
//                            }
//
//                            firestore.collection("all_card_list").whereIn("cardId", card_list).get().addOnSuccessListener { querySnapshot ->
//                                val documents = querySnapshot.documents.toMutableList()
//                                Log.d("CardList_to_Edit", "querysnapshot complete")
//
//                                if (documents != null && documents.isNotEmpty()){
//                                    Log.d("CardList_to_Edit", "result.document is not null")
//
//                                    for (document in documents){
//                                        val cardId = document["cardId"].toString()
//                                        val name = document["name"].toString()
//                                        val workplace = document["workplace"].toString()
//                                        val introduction = document["introduction"].toString()
//                                        val email = document["email"].toString()
//                                        val position = document["position"].toString()
//                                        val gender  = document["gender"].toString()
//                                        val imageName= document["imageName"].toString()
//                                        val job = document["job"].toString()
//                                        val pofol = document["pofol"].toString()
//                                        val sns = document["sns"].toString()
//                                        val tel = document["tel"].toString()
//
//                                        Log.d("CardList_to_Edit", "cardData => $name($cardId)")
//                                        myCardList.add(Card(name, position, workplace, email, cardId, gender, imageName, introduction, job, pofol, sns, tel))
//                                    }
//                                }
//                                Log.d("CardList_to_Edit", "${myCardList.size}")
//
//                                binding.recyclerView.adapter = CardAdapter_for_Edit(myCardList, this)
//                            }.addOnFailureListener{ Log.d("CardList_to_Edit", "querysnapshot 실패")}
//                        } else {
//                            Log.d("CardList_to_Edit", "wallet_list에 uid 문서는 있는데 필드가 없음 -> 명함 저장 시 제대로 이뤄지지 않음")
//                        }
//                    } else {
//                        Log.d("CardList_to_Edit", "No such wallet document")
//                    }
//                } else {
//                    Log.d("CardList_to_Edit", "get failed with ", task.exception)
//                }
//            }
//        }
//        else{
//            Log.d("CardList_to_Edit", "when check the my card, uid is null")
//        }
//
//        Log.d("CardList_to_Edit", "second : ${myCardList.size}")
//    }
//}
