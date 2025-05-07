package com.resigned.yellow.crane.tower.svrv.vrv

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.resigned.yellow.crane.tower.R
import com.resigned.yellow.crane.tower.databinding.SettingUiBinding

class Setting  : AppCompatActivity() {
    val binding by lazy { SettingUiBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sett)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.btnPP.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                android.net.Uri.parse("https://sites.google.com/view/fantastic-w-wallpaper/home")
            startActivity(intent)
        }
    }
}