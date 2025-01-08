package com.example.madcampweek2.room

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.calculate.CalculateHostActivity
import com.example.madcampweek2.data.RoomTag
import com.example.madcampweek2.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        recyclerView = findViewById(R.id.rvRoom)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchRooms()
    }

    private fun fetchRooms() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userUuid = sharedPreferences.getString("user_uuid", null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getRooms(userUuid)
                if (response.isSuccessful && response.body() != null) {
                    val roomData = response.body()!!.data
                    val roomList = mutableListOf<RoomTag>()

                    // 서버 데이터 처리 (id와 title 모두 포함)
                    roomData.hostedRooms.forEach { room ->
                        roomList.add(RoomTag(id = room.id, tag = "호스트", title = room.title))
                    }
                    roomData.participatingRooms.forEach { room ->
                        roomList.add(RoomTag(id = room.id, tag = "참여자", title = room.title))
                    }

                    // RecyclerView 업데이트
                    withContext(Dispatchers.Main) {
                        adapter = RoomAdapter(roomList) { room ->
                            // 클릭 시 동작
                            if (room.tag == "호스트") {
                                // CalculateHostActivity로 이동
                                val intent = Intent(this@RoomActivity, CalculateHostActivity::class.java)
                                intent.putExtra("roomId", room.id) // roomId 전달
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@RoomActivity,
                                    "ID: ${room.id}, Title: ${room.title} 클릭됨!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        recyclerView.adapter = adapter
                    }
                } else {
                    showError("데이터를 가져오는 데 실패했습니다.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showError("오류가 발생했습니다.")
            }
        }
    }

    private fun showError(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@RoomActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}