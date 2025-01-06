package com.example.madcampweek2.home

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: TitleAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // 글자색 조정, 건드리지 말기
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvNBread = findViewById<TextView>(R.id.tvNBread)
        val tvPay = findViewById<TextView>(R.id.tvPay)

        val breadText = "N빵\n하러 가기"
        val breadSpannable = SpannableString(breadText)
        val purpleColor = getColor(R.color.main_purple)
        breadSpannable.setSpan(ForegroundColorSpan(purpleColor), 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvNBread.text = breadSpannable

        val payText = "돈! 보내셔야죠!"
        val paySpannable = SpannableString(payText)
        paySpannable.setSpan(ForegroundColorSpan(purpleColor), 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvPay.text = paySpannable
        // 여기까지

        // 탭에 따른 리사이클러뷰 변경
        val rvTitle = findViewById<RecyclerView>(R.id.rvTitle)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        adapter = TitleAdapter()
        rvTitle.layoutManager = LinearLayoutManager(this)
        rvTitle.adapter = adapter

        adapter.submitList(getReceiveData())

        tabLayout.addTab(tabLayout.newTab().setText("받을 돈"))
        tabLayout.addTab(tabLayout.newTab().setText("줄 돈"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    "받을 돈" -> adapter.submitList(getReceiveData())
                    "줄 돈" -> adapter.submitList(getPayData())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        // 여기까지
    }

    // 더미데이터
    private fun getReceiveData(): List<String> {
        return listOf("받을 돈 항목 1", "받을 돈 항목 2", "받을 돈 항목 3", "받을 돈 항목 4")
    }

    // 더미데이터
    private fun getPayData(): List<String> {
        return listOf("줄 돈 항목 1", "줄 돈 항목 2", "줄 돈 항목 3", "줄 돈 항목 4", "줄 돈 항목 5")
    }
}