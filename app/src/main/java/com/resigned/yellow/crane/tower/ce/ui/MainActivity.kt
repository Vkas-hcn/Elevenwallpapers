package com.resigned.yellow.crane.tower.ce.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.resigned.yellow.crane.tower.R
import com.resigned.yellow.crane.tower.ce.dete.DetailPhotoActivity
import com.resigned.yellow.crane.tower.ce.dete.DetailWallActivity
import com.resigned.yellow.crane.tower.databinding.ActivityMainBinding
import com.resigned.yellow.crane.tower.svrv.vrv.Setting
import com.resigned.yellow.crane.tower.utils.AppData

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var adapter: ItemAdapter
    private var type = 1
    private var dataList :List<Int> = AppData.allWallPaperData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initRecyclerView()
        clickFun()
    }

    private fun initRecyclerView() {
        adapter = ItemAdapter(this, AppData.allWallPaperData) { imageRes ->
            // 点击事件：跳转到详情页面并传递图片资源
            val intent = if (type == 1) {
                Intent(this, DetailWallActivity::class.java).apply {
                    putExtra("imageRes", imageRes)
                }
            } else {
                Intent(this, DetailPhotoActivity::class.java).apply {
                }
            }

            startActivity(intent)
        }
        binding.recyMain.adapter = adapter
        binding.recyMain.layoutManager = GridLayoutManager(this, 2) // 两列布局
    }

    @SuppressLint("ResourceType")
    private fun clickFun() {
        with(binding) {
            butAll.setOnClickListener {
                setFen(1)
               dataList = AppData.allWallPaperData
                checkWall(dataList)
            }
            butScenery.setOnClickListener {
                setFen(2)
                dataList = AppData.sceneryWallPaperData
                checkWall(dataList)
            }
            butBeauty.setOnClickListener {
                setFen(3)
                dataList = AppData.beautyWallPaperData
                checkWall(AppData.beautyWallPaperData)
            }
            butWall.setOnClickListener {
                showFen(true)
                checkWall(dataList)
            }

            // 相框按钮点击事件
            butPhoto.setOnClickListener {
                showFen(false)
                type = 2
                adapter.itemList = AppData.allFramesData
                adapter.notifyDataSetChanged()
                butPhoto.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.white
                    )
                )
                butWall.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        android.R.color.transparent
                    )
                )
            }

            // 设置按钮点击事件
            imgSetting.setOnClickListener {
                startActivity(Intent(this@MainActivity, Setting::class.java))
            }
        }
    }

    private fun checkWall(dataList:List<Int>){
            type = 1
        adapter.itemList = dataList
        adapter.notifyDataSetChanged()
        binding.butWall.setCardBackgroundColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.white
            )
        )
        binding.butPhoto.setCardBackgroundColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.tran
            )
        )
    }

    private fun showFen(state:Boolean){
        with(binding){
            butAll.isVisible = state
            butScenery.isVisible = state
            butBeauty.isVisible = state
        }
    }
    private fun setFen(type:Int){
        with(binding){
            when(type){
                1->{
                    butAll.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    )
                    butAll.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.black
                        )
                    )
                    butScenery.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.nochek
                        )
                    )
                    butScenery.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    )
                    butBeauty.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.nochek
                        )
                    )
                    butBeauty.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    )
                }
                2->{
                    butAll.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.nochek
                        )
                    )
                    butAll.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    )
                    butScenery.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            android.R.color.white
                        )
                    )
                    butScenery.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.black
                        )
                    )
                    butBeauty.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.nochek
                        )
                    )
                    butBeauty.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    )
                }
                3 ->{
                    butAll.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.nochek
                        )
                    )
                    butAll.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    )
                    butScenery.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.nochek
                        )
                    )
                    butScenery.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    )
                    butBeauty.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            android.R.color.white
                        )
                    )
                    butBeauty.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.black
                        )
                    )
                }
            }
        }
    }
}
