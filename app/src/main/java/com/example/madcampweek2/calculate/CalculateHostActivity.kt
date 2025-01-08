package com.example.madcampweek2.calculate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.data.MenuItem
import com.example.madcampweek2.data.Participant
import com.example.madcampweek2.data.ParticipantInMenu
import com.example.madcampweek2.data.ReceiptSummary
import com.example.madcampweek2.data.UpdateRoomStatusRequest
import com.example.madcampweek2.network.ApiClient
import com.example.madcampweek2.start.StartHostActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalculateHostActivity : AppCompatActivity() {
    private lateinit var rvPeopleCheck: RecyclerView
    private lateinit var rvForgot: RecyclerView
    private var roomId: Int = 0 // roomId를 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_host)

        rvPeopleCheck = findViewById(R.id.rvPeopleCheck)
        rvForgot = findViewById(R.id.rvForgot)

        // Intent로부터 roomId 수신
        roomId = intent.getIntExtra("roomId", 5)
        Log.d("CalculateHostActivity", "roomId: $roomId")

        // "정산 시작" 버튼 클릭 이벤트 처리
        val btnStartHost: Button = findViewById(R.id.btnStart)
        btnStartHost.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 요청 바디 생성
                    val requestBody = UpdateRoomStatusRequest(
                        roomId = roomId,
                        status = 2
                    )

                    // POST 요청
                    val response = ApiClient.apiService.updateRoomStatus(requestBody)

                    // 응답 확인
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        withContext(Dispatchers.Main) {
                            // 성공 메시지 출력
                            responseBody?.let {
                                Toast.makeText(this@CalculateHostActivity, "정산이 시작됩니다!", Toast.LENGTH_SHORT).show()
                            }

                            // 다음 Activity로 이동
                            val intent = Intent(this@CalculateHostActivity, StartHostActivity::class.java)
                            intent.putExtra("roomId", roomId) // roomId 전달
                            startActivity(intent)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@CalculateHostActivity,
                                "상태 업데이트 실패: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CalculateHostActivity, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // ivback 클릭 이벤트 처리
        val ivBack: ImageView = findViewById(R.id.ivback)
        ivBack.setOnClickListener {
            onBackPressed() // 뒤로가기 동작 실행
        }

        // 서버에서 데이터 가져오기
        fetchData()
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getReceiptSummary(roomId)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    val participants = data?.participants?.map { participant ->
                        val userCheck = data.userChecks[participant.userUuid]
                        val userTotal = data.userTotals[participant.userUuid]?.total ?: "0" // userTotals에서 total 값 가져오기

                        ParticipantInMenu(
                            userUuid = participant.userUuid,
                            name = participant.name,
                            items = userCheck?.items?.map { userItem ->
                                MenuItem(userItem.name, userItem.price) // UserItem -> MenuItem 변환
                            } ?: emptyList(),
                            total = userTotal // total 값 설정
                        )
                    } ?: emptyList()

                    val forgotParticipants = data?.uncheckedItems?.map {
                        ParticipantInMenu(
                            userUuid = "",
                            name = it.itemName,
                            items = listOf(MenuItem(it.itemName, it.price)),
                            total = "0" // uncheckedItems에는 total 값이 없으므로 0으로 설정
                        )
                    } ?: emptyList()

                    withContext(Dispatchers.Main) {
                        // RecyclerView 설정
                        rvPeopleCheck.layoutManager = LinearLayoutManager(this@CalculateHostActivity)
                        rvPeopleCheck.adapter = PeopleAdapter(participants)

                        rvForgot.layoutManager = LinearLayoutManager(this@CalculateHostActivity)
                        rvForgot.adapter = ForgotAdapter(forgotParticipants)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CalculateHostActivity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}