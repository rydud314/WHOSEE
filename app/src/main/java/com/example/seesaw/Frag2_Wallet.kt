package com.example.seesaw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class Frag2_Wallet : Fragment() {

    private var view: View? = null

    companion object{
        fun newInstance() : Frag2_Wallet{
            return Frag2_Wallet()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.activity_frag2_wallet, container, false)
        return view
    }
}