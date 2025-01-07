package com.example.madcampweek2.makeRoom

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madcampweek2.R
import com.example.madcampweek2.home.HomeActivity

class HostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        val ivBack: ImageView = findViewById(R.id.ivback)
        ivBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val etInvite: EditText = findViewById(R.id.etInvite)
        val btnInvite: Button = findViewById(R.id.btnInvite)
        val invitedFriendsLayout: LinearLayout = findViewById(R.id.invitedFriendsLayout)

        btnInvite.setOnClickListener {
            val friendName = etInvite.text.toString()

            if (friendName.isNotBlank()) {

                val textView = TextView(this).apply {
                    text = friendName
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black, null))
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 10, 0, 10)
                    }
                    gravity = Gravity.START
                }

                invitedFriendsLayout.addView(textView)

                etInvite.text.clear()
            }
        }
    }
}