package com.example.madcampweek2.home

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.calculate.CalculateActivity
import com.example.madcampweek2.check.CheckActivity
import com.example.madcampweek2.makeRoom.HostActivity
import com.example.madcampweek2.network.ApiClient
import com.example.madcampweek2.notification.NotificationActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: TitleAdapter
    private val userUuid = "1111" // 고정된 UUID 값

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

        fetchRoomData { hostedRooms, participatingRooms ->
            adapter.submitList(hostedRooms) // 기본값: 받을 돈

            tabLayout.addTab(tabLayout.newTab().setText("받을 돈"))
            tabLayout.addTab(tabLayout.newTab().setText("줄 돈"))

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.text) {
                        "받을 돈" -> adapter.submitList(hostedRooms)
                        "줄 돈" -> adapter.submitList(participatingRooms)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
        // 여기까지

        // 화면 이동
        val tvGoN: TextView = findViewById(R.id.tvGoN)
        val layoutGoN: LinearLayout = findViewById(R.id.layoutGoN)
        val imgBtnNotifi: ImageView = findViewById(R.id.imgBtnNotifi)

        tvGoN.setOnClickListener {
            navigateToActivity(HostActivity::class.java)
        }
        layoutGoN.setOnClickListener {
            navigateToActivity(HostActivity::class.java)
        }

        val layoutPay: LinearLayout = findViewById(R.id.layoutPay)
        layoutPay.setOnClickListener {
            navigateToActivity(CheckActivity::class.java)
        }

        val layoutReceipt: LinearLayout = findViewById(R.id.layoutReceipt)
        layoutReceipt.setOnClickListener {
            navigateToActivity(CalculateActivity::class.java)
        }

        imgBtnNotifi.setOnClickListener {
            navigateToActivity(NotificationActivity::class.java)
        }
        // 여기까지
    }

    // 서버에서 데이터 가져오기
    private fun fetchRoomData(onResult: (List<String>, List<String>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getRooms(userUuid)
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    val hostedRooms = data.hostedRooms
                    val participatingRooms = data.participatingRooms

                    // UI 업데이트
                    withContext(Dispatchers.Main) {
                        onResult(hostedRooms, participatingRooms)
                    }
                } else {
                    // 실패 처리
                    withContext(Dispatchers.Main) {
                        onResult(emptyList(), emptyList())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(emptyList(), emptyList())
                }
            }
        }
    }

    // 화면 이동
    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
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