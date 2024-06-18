package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity(), OnboardingCallback {

    private lateinit var binding: ActivityOnboardingBinding
    private val fragments = listOf(
        OnboardingFragment1(),
        OnboardingFragment2(),
        OnboardingFragment3(),
        OnboardingFragment4()
    )
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showFragment(currentIndex)
        updateProgress()
    }

    override fun onNextClicked() {
        if (currentIndex < fragments.size - 1) {
            currentIndex++
            showFragment(currentIndex)
        }
        updateProgress()
    }

    override fun onFinishClicked() {
        startActivity(Intent(this, Login::class.java))
        finish()
    }

    private fun showFragment(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragments[index])
        transaction.commit()
        updateProgress()
    }

    private fun updateProgress() {
        val progress = (currentIndex + 1) * 100 / fragments.size
        binding.progressBar.progress = progress
    }
}
