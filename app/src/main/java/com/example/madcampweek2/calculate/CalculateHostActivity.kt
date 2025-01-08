package com.example.madcampweek2.calculate

import android.os.Bundle
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
import com.example.madcampweek2.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalculateHostActivity : AppCompatActivity() {
    private lateinit var rvPeopleCheck: RecyclerView
    private lateinit var rvForgot: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_host)

        rvPeopleCheck = findViewById(R.id.rvPeopleCheck)
        rvForgot = findViewById(R.id.rvForgot)

        // 서버에서 데이터 가져오기
        fetchData()
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getReceiptSummary(5) // room_id 5로 가정
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    val participants = data?.participants?.map { participant ->
                        val userCheck = data.userChecks[participant.userUuid]
                        ParticipantInMenu(
                            userUuid = participant.userUuid,
                            name = participant.name,
                            items = userCheck?.items?.map { userItem ->
                                MenuItem(userItem.name, userItem.price) // UserItem -> MenuItem 변환
                            } ?: emptyList()
                        )
                    } ?: emptyList()

                    val forgotParticipants = data?.uncheckedItems?.map {
                        ParticipantInMenu(
                            userUuid = "",
                            name = it.itemName, // "itemName"으로 수정
                            items = listOf(MenuItem(it.itemName, it.price))
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