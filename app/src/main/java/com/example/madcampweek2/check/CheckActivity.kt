package com.example.madcampweek2.check

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.home.HomeActivity
import com.example.madcampweek2.network.ApiClient
import com.example.madcampweek2.network.GetReceiptItemsResponse
import com.example.madcampweek2.network.ReceiptItemData
import com.example.madcampweek2.network.ReceiptService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReceiptItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        // 글자 색 설정
        val tvCheck = findViewById<TextView>(R.id.tvCheck)

        val fullText = "먹은 것만\n체크하세요!"
        val spannableString = SpannableString(fullText)

        val checkStartIndex = fullText.indexOf("체크")
        val checkEndIndex = checkStartIndex + "체크".length
        val colorSpan = ForegroundColorSpan(resources.getColor(R.color.main_purple, theme))
        spannableString.setSpan(colorSpan, checkStartIndex, checkEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvCheck.text = spannableString
        // 여기까지

        val ivBack: ImageView = findViewById(R.id.ivback)
        ivBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val roomId = intent.getIntExtra("roomId", -1)
        if (roomId == -1) {
            Toast.makeText(this, "방 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.rvMenu)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchReceiptItems(roomId)
    }

    private fun fetchReceiptItems(roomId: Int) {
        val service = ApiClient.retrofit.create(ReceiptService::class.java)
        service.getReceiptItems(roomId).enqueue(object : Callback<GetReceiptItemsResponse> {
            override fun onResponse(
                call: Call<GetReceiptItemsResponse>,
                response: Response<GetReceiptItemsResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    val items = response.body()?.data ?: emptyList()
                    displayItems(items)
                } else {
                    Log.e("CheckActivity", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@CheckActivity, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetReceiptItemsResponse>, t: Throwable) {
                Log.e("CheckActivity", "Failure: ${t.message}")
                Toast.makeText(this@CheckActivity, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayItems(items: List<ReceiptItemData>) {
        adapter = ReceiptItemAdapter(items)
        recyclerView.adapter = adapter
    }
}