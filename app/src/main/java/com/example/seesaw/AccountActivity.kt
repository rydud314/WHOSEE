package com.example.seesaw

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var btnLogout: Button
    private lateinit var btnChange_detail: Button
    private lateinit var btnExit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        btnExit=findViewById(R.id.btn_exit)
        btnLogout=findViewById(R.id.btn_logout)
        btnChange_detail=findViewById(R.id.btn_change_detail)
    }
}
