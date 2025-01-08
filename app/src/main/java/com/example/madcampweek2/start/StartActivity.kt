package com.example.madcampweek2.start

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.complete.CompleteActivity
import com.example.madcampweek2.data.ReceiptItem
import com.example.madcampweek2.data.ReceiptItemInStart
import com.example.madcampweek2.home.HomeActivity
import com.example.madcampweek2.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartActivity : AppCompatActivity() {

    private lateinit var rvMenu: RecyclerView
    private lateinit var receiptItemAdapter: ReceiptItemAdapter
    private lateinit var tvPrice: TextView
    private val receiptItems = mutableListOf<ReceiptItemInStart>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val ivBack: ImageView = findViewById(R.id.ivback)
        ivBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // RecyclerView 초기화
        rvMenu = findViewById(R.id.rvMenu)
        rvMenu.layoutManager = LinearLayoutManager(this)
        receiptItemAdapter = ReceiptItemAdapter(this, receiptItems)
        rvMenu.adapter = receiptItemAdapter

        // tvPrice 초기화
        tvPrice = findViewById(R.id.tvPrice)

        // roomId와 userUuid 설정
        val roomId = intent.getIntExtra("roomId", 0)
        val userUuid = "62ab355f-a664-47aa-85a1-7a8cf82d68da" // 고정값

        // 데이터 가져오기
        fetchReceiptItems(roomId, userUuid)

        // 송금하기 버튼
        val btnSend: Button = findViewById(R.id.btnSend)
        btnSend.setOnClickListener {
            // CompleteActivity로 이동
            val intent = Intent(this, CompleteActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity 종료
        }
    }

    private fun fetchReceiptItems(roomId: Int, userUuid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getReceiptItemsWithCheckStatus(roomId, userUuid)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.data?.let { items ->
                        receiptItems.clear()
                        val filteredItems = items.filter { it.checked == 1 } // checked가 1인 항목만 추가
                        receiptItems.addAll(filteredItems)

                        // 합계 계산
                        val totalPrice = filteredItems.sumOf {
                            it.price.toBigDecimal() // BigDecimal로 변환 후 합산
                        }

                        // UI 업데이트
                        withContext(Dispatchers.Main) {
                            // 합계를 tvPrice에 설정
                            tvPrice.text = totalPrice.toPlainString().replace(".00", "")
                            receiptItemAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@StartActivity, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@StartActivity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}