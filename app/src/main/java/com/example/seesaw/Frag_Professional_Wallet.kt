package com.example.seesaw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seesaw.Report_Annual.CardData

class Frag_Professional_Wallet(private val itCards: List<CardData>) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_professional_wallet, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = object : Professional_Wallet_Adapter(itCards, childFragmentManager) {}

        return view
    }

    companion object {
        fun newInstance(itCards: List<CardData>): Frag_Professional_Wallet {
            return Frag_Professional_Wallet(itCards)
        }
    }
}
