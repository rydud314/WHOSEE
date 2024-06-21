package com.example.seesaw

import android.util.Log
import androidx.lifecycle.ViewModel

class ShareViewModel : ViewModel() {
    private val TAG = "ViewModel : "

    var shareCard : Card? = null

    init {
        Log.d(TAG, "ShareViewModel 생성")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ShareViewModel 종료")
    }

}