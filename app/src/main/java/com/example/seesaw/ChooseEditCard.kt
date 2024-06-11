package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seesaw.databinding.ActivityChooseEditCardBinding

class ChooseEditCard : AppCompatActivity() {

    private lateinit var binding: ActivityChooseEditCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseEditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 샘플 데이터
        val cards = listOf(
            Card("John Doe", "Developer", "Google", "john.doe@gmail.com", "1", "Male", "", "Introduction", "Job", "Pofol", "SNS", "123-456-7890"),
            Card("Jane Smith", "Designer", "Facebook", "jane.smith@gmail.com", "2", "Female", "", "Introduction", "Job", "Pofol", "SNS", "123-456-7890")
        )

        // RecyclerView 설정
        val adapter = NameCardAdapter_for_Edit(cards)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
}
