package com.example.seesaw

import android.util.Log
import androidx.lifecycle.ViewModel

class CardIdViewModel : ViewModel() {
    private val TAG = "IDViewModel : "

    var cardId : String = ""

    init {
        Log.d(TAG, "IDViewModel 생성")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "IDViewModel 종료")
    }

}