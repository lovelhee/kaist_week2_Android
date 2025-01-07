package com.example.madcampweek2.roomList

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.network.ApiClient
import com.example.madcampweek2.network.RoomResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_room_list)

        // Window Insets 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // SharedPreferences에서 사용자 UUID 가져오기
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userUuid = sharedPreferences.getString("user_uuid", null)

        if (userUuid != null) {
            fetchRooms(userUuid)
        } else {
            Log.e("RoomListActivity", "UUID를 찾을 수 없습니다.")
        }
    }

    // 방 정보를 가져오는 함수
    private fun fetchRooms(userUuid: String) {
        val userService = ApiClient.userService
        userService.getRooms(userUuid).enqueue(object : Callback<RoomResponse> {
            override fun onResponse(call: Call<RoomResponse>, response: Response<RoomResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val roomData = response.body()?.data // RoomListData 사용
                    val hostedRooms = roomData?.hostedRooms ?: emptyList()
                    val participatingRooms = roomData?.participatingRooms ?: emptyList()

                    // hostedRooms와 participatingRooms를 통합
                    val allRooms = mutableListOf<String>().apply {
                        addAll(hostedRooms)
                        addAll(participatingRooms)
                    }

                    setupRecyclerView(allRooms)
                } else {
                    Log.e("RoomListActivity", "방 정보 로드 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RoomResponse>, t: Throwable) {
                Log.e("RoomListActivity", "네트워크 오류: ${t.message}")
            }
        })
    }

    // RecyclerView 설정 함수
    private fun setupRecyclerView(rooms: List<String>) {
        val recyclerView = findViewById<RecyclerView>(R.id.rvWaitingRoom)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RoomAdapter(rooms)
    }
}