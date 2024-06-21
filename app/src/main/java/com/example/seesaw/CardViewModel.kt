package com.example.seesaw

import android.util.Log
import androidx.lifecycle.ViewModel

class CardViewModel : ViewModel() {
    private val TAG = "ViewModel : "

    var myCardList : ArrayList<Card> = arrayListOf()

    init {
        Log.d(TAG, "ViewModel 생성")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel 종료")
    }

}