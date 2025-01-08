package com.example.madcampweek2.start

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.complete.CompleteActivity
import com.example.madcampweek2.data.ParticipantInStart
import com.example.madcampweek2.data.UpdateRoomStatusRequest
import com.example.madcampweek2.home.HomeActivity
import com.example.madcampweek2.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartHostActivity : AppCompatActivity() {

    private lateinit var rvFriends: RecyclerView
    private lateinit var participantAdapter: ParticipantAdapter
    private lateinit var btnCompleteSettlement: Button // 정산 완료 버튼
    private val participants = mutableListOf<ParticipantInStart>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_host)

        rvFriends = findViewById(R.id.rvFriends)
        rvFriends.layoutManager = LinearLayoutManager(this)

        btnCompleteSettlement = findViewById(R.id.btnPayComplete)
        btnCompleteSettlement.isEnabled = false // 초기에는 비활성화


        participantAdapter = ParticipantAdapter(
            this,
            participants,
            onAlarmClick = { participant ->
                Toast.makeText(this, "${participant.name}에게 알림을 보냅니다!", Toast.LENGTH_SHORT).show()
            },
            onAllPaidCheck = { allPaid ->
                // 모든 isPaid가 1이면 정산 완료 버튼 활성화
                btnCompleteSettlement.isEnabled = allPaid
                // 버튼 색상 변경
                if (allPaid) {
                    btnCompleteSettlement.setBackgroundColor(getColor(R.color.main_purple)) // 활성화 상태
                } else {
                    btnCompleteSettlement.setBackgroundColor(android.graphics.Color.parseColor("#7E7E7E")) // 비활성화 상태
                }
            }
        )
        rvFriends.adapter = participantAdapter


        // 방 ID를 Intent로 전달받음
        val roomId = intent.getIntExtra("roomId", 0)
        val currentUserUuid = "1111" // 현재 로그인한 사용자 UUID 가져오기
        fetchRoomParticipants(roomId, currentUserUuid)

        val ivBack: ImageView = findViewById(R.id.ivback)
        ivBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCompleteSettlement.setOnClickListener {
            // POST 요청 보내기
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 요청 바디 생성
                    val requestBody = UpdateRoomStatusRequest(
                        roomId = roomId,
                        status = 0
                    )

                    val response = ApiClient.apiService.updateRoomStatus(requestBody) // roomId와 status 0 전달
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@StartHostActivity, "정산이 끝났습니다.", Toast.LENGTH_SHORT).show()
                            // CompleteActivity로 이동
                            val intent = Intent(this@StartHostActivity, CompleteActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@StartHostActivity, "정산 상태 업데이트 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@StartHostActivity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun fetchRoomParticipants(roomId: Int, currentUserUuid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Retrofit으로 API 호출
                val response = ApiClient.apiService.getRoomParticipants(roomId)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.data?.let { participantList ->
                        participants.clear()
                        participants.addAll(
                            participantList.filter { it.uuid != currentUserUuid } // 현재 사용자 제외
                                .map {
                                    ParticipantInStart(
                                        uuid = it.uuid,
                                        name = it.name,
                                        amountOfMoney = it.amountOfMoney,
                                        isPaid = it.isPaid
                                    )
                                }
                        )

                        // UI 업데이트
                        withContext(Dispatchers.Main) {
                            participantAdapter.notifyDataSetChanged()

                            // 데이터 로드 후 모든 참가자 상태 확인
                            participantAdapter.checkAllPaid()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@StartHostActivity, "참가자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@StartHostActivity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}