package com.resigned.yellow.crane.tower.nj.ss

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.resigned.yellow.crane.tower.R
import com.resigned.yellow.crane.tower.ce.ui.MainActivity
import com.resigned.yellow.crane.tower.databinding.SsssBinding

class SsActivity : AppCompatActivity() {
    val binding by lazy { SsssBinding.inflate(layoutInflater) }
    private lateinit var countDownTimer: CountDownTimer
    private val totalTime = 2000L
    private val interval = 100L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ssss)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        startCountDown()
    }

    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = ((totalTime - millisUntilFinished) * 100 / totalTime).toInt()
                binding.linearProgressIndicator.progress = progress
            }

            override fun onFinish() {
                val intent = Intent(this@SsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}
