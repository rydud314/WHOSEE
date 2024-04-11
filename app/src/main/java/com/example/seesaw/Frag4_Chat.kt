package com.example.seesaw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class Frag4_Chat : Fragment() {

    private var view: View? = null

    companion object{
        fun newInstance() : Frag4_Chat{
            return Frag4_Chat()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.activity_frag4_chat, container, false)
        return view
    }
}