package com.example.madcampweek2.check

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madcampweek2.R

class CheckActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_check)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvCheck = findViewById<TextView>(R.id.tvCheck)

        // "먹은 것만 체크하세요!" 텍스트 설정
        val fullText = "먹은 것만\n체크하세요!"
        val spannableString = SpannableString(fullText)

        // "체크"만 보라색으로 설정
        val checkStartIndex = fullText.indexOf("체크")
        val checkEndIndex = checkStartIndex + "체크".length
        val colorSpan = ForegroundColorSpan(resources.getColor(R.color.main_purple, theme))
        spannableString.setSpan(colorSpan, checkStartIndex, checkEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // TextView에 SpannableString 적용
        tvCheck.text = spannableString
    }
}