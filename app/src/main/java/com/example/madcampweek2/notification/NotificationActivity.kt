
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
        recyclerView.adapter = NotificationAdapter(notifications)
    }
}