package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditCard : AppCompatActivity(){
    private lateinit var etName: EditText
    private lateinit var etJob: EditText
    private lateinit var etIntroduction: EditText
    private lateinit var etWorkplace: EditText
    private lateinit var etAge: EditText
    private lateinit var etAnnual: EditText
    private lateinit var etTel: EditText
    private lateinit var etSns: EditText
    private lateinit var etPortfolio: EditText
    private lateinit var btnMale: Button
    private lateinit var btnFemale: Button
    private lateinit var btnCreateCard: Button

    private var gender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)

        etName = findViewById(R.id.et_name)
        etJob = findViewById(R.id.et_job)
        etIntroduction = findViewById(R.id.et_introduction)
        etWorkplace = findViewById(R.id.et_workplace)
        etAge = findViewById(R.id.et_age)
        etAnnual = findViewById(R.id.et_annual)
        etTel = findViewById(R.id.et_tel)
        etSns = findViewById(R.id.et_sns)
        etPortfolio = findViewById(R.id.et_portfolio)
        btnMale = findViewById(R.id.btn_male)
        btnFemale = findViewById(R.id.btn_female)

        btnMale.setOnClickListener {
            gender = "남"
            btnMale.setBackgroundResource(R.drawable.selected_box_shape)
            btnFemale.setBackgroundResource(R.drawable.box_shape)
        }

        btnFemale.setOnClickListener {
            gender = "여"
            btnFemale.setBackgroundResource(R.drawable.selected_box_shape)
            btnMale.setBackgroundResource(R.drawable.box_shape)
        }

        // 명함 수정 버튼 설정
        val btn_edit_card: Button = findViewById(R.id.btn_edit_card)
        btn_edit_card.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}