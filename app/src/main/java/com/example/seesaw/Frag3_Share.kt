package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment

class Frag3_Share : Fragment() {

    private var view: View? = null

    companion object{
        fun newInstance() : Frag3_Share{
            return Frag3_Share()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.activity_frag3_share, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // findViewById는 이제 view에서 호출
        val btn_camera: Button = view.findViewById(R.id.btn_camera)
        btn_camera.setOnClickListener{
            val intent = Intent(context, CameraScan_1::class.java)
            startActivity(intent)
        }
    }
}