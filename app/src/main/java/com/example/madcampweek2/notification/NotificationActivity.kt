
package com.example.madcampweek2.notification

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.data.Notification
import com.example.madcampweek2.home.HomeActivity
import com.example.madcampweek2.start.StartActivity

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val ivBack: ImageView = findViewById(R.id.ivback)
        ivBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val notifications = listOf(
            Notification(R.drawable.ic_fire, "이재환", "님이 정산을 시작했어요."),
            Notification(R.drawable.ic_sad, "이재환", "님이 굶고 있어요. 송금을 ...")
        )

        val recyclerView: RecyclerView = findViewById(R.id.rvNotification)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // 어댑터 설정 (클릭 이벤트 추가)
        recyclerView.adapter = NotificationAdapter(notifications) { notification ->
            // 클릭 시 StartActivity로 이동 (roomId 전달)
            val intent = Intent(this, StartActivity::class.java)
            intent.putExtra("roomId", 43) // roomId를 Intent에 추가 (예: 39)
            startActivity(intent)
        }
    }
}