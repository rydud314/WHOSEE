package com.example.seesaw


import android.content.ContentValues.TAG
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Frag2_Wallet : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_frag2_wallet, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val uid = currentUser?.uid.toString()
        Log.d(TAG, "UID =  $uid")

        var cardId = ""
        var card_list = mutableListOf<String>()

        val myCardList : MutableList<Card> = mutableListOf()
        myCardList.clear()

        if (uid != null) {
            firestore.collection("wallet_list").document(uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        val cards = document.get("cards") as? List<Map<String, Any>>
                        if (cards != null) {
                            for (card in cards) {
                                cardId = card["cardId"].toString()
                                Log.d(TAG, "cardId = $cardId")
                                card_list.add(cardId)

                            }

                            //자신의 카드 정보 가져오기
                            firestore.collection("all_card_list").whereIn("cardId", card_list).get().addOnSuccessListener { querySnapshot ->
                                val documents = querySnapshot.documents.toMutableList()
                                Log.d(TAG, "querysnapshot complete")

                                if (documents != null && documents.isNotEmpty()){
                                    Log.d(TAG, "result.document is not null")

                                    for (document in documents){
                                        val cardId = document["cardId"].toString()
                                        val name = document["name"].toString()
                                        val workplace = document["workplace"].toString()
                                        val introduction = document["introduction"].toString()
                                        val email = document["email"].toString()
                                        val position = document["position"].toString()
                                        val gender  = document["gender"].toString()
                                        val imageName= document["imageName"].toString()
                                        val job = document["job"].toString()
                                        val pofol = document["pofol"].toString()
                                        val sns = document["sns"].toString()
                                        val tel = document["tel"].toString()

                                        Log.d(TAG, "cardData => $name($cardId)")
                                        myCardList.add(Card(name, position, workplace, email, cardId, gender, imageName, introduction, job, pofol, sns, tel))
                                    }
                                }
                                Log.d(TAG, "${myCardList.size}")

                                recyclerView.adapter = CardAdapter(myCardList)


                            }.addOnFailureListener{Log.d(TAG, "querysnapshot 실패")}

                        } else {
                            Log.d(TAG, "wallet_list에 uid 문서는 있는데 필드가 없음 -> 명함 저장 시 제대로 이뤄지지 않음")
                        }
                    } else {
                        Log.d(TAG, "No such wallet document")
                        Toast.makeText(context, "보유한 친구 명함이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.exception)
                }
            }
        }
        else{
            Log.d(TAG, "when check the my card, uid is null")
        }

        Log.d(TAG, "second : ${myCardList.size}")


        /*
        // 샘플 데이터
        val cardList = listOf(
            Card("김나연", "사원", "영남대학교", "@gmail.com", "cardId", "여",
                "imageName", "안냐세요", "개발자", "포폴", "sns안해요", "010-1234-1234")
            //Card("김현수", "Graphic Designer", "영남대학교")
            // 카드 추가 여기서 하기
        )*/

        return view

    }
    companion object{
        fun newInstance() : Frag2_Wallet{
            return Frag2_Wallet()
        }
    }
}