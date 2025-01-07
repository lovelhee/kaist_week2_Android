package com.example.madcampweek2.check

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madcampweek2.R
import com.example.madcampweek2.home.HomeActivity

class CheckActivity : AppCompatActivity() {
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
    }
}