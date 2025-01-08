package com.example.madcampweek2.check

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Button
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
import com.example.madcampweek2.network.ReceiptUpdate
import com.example.madcampweek2.network.UpdateChecksRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReceiptItemAdapter
    private lateinit var btnComplete: Button
    private var items: List<ReceiptItemData> = listOf()
    private val checkedStates = mutableMapOf<Int, Int>()

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

        btnComplete = findViewById(R.id.btnComplete)
        btnComplete.setOnClickListener {
            saveCheckedStates()
        }
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
        adapter = ReceiptItemAdapter(items) { id, isChecked ->
            checkedStates[id] = if (isChecked) 1 else 0
        }
        recyclerView.adapter = adapter
    }

    private fun saveCheckedStates() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userUuid = sharedPreferences.getString("user_uuid", null)

        if (userUuid == null) {
            Toast.makeText(this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = checkedStates.map { (id, checked) ->
            ReceiptUpdate(receiptItemId = id, checked = checked)
        }

        val body = UpdateChecksRequest(userUuid = userUuid, updates = updates)

        val service = ApiClient.retrofit.create(ReceiptService::class.java)
        service.updateChecks(body).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CheckActivity, "체크 상태가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("CheckActivity", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@CheckActivity, "저장 실패.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("CheckActivity", "Failure: ${t.message}")
                Toast.makeText(this@CheckActivity, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}