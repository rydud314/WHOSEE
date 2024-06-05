package com.example.seesaw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class Frag5_Auction : Fragment() {

    private var view: View? = null

    companion object{
        fun newInstance() : Frag5_Auction{
            return Frag5_Auction()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.activity_frag5_auction, container, false)
        return view
    }
}