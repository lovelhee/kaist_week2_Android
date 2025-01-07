package com.example.madcampweek2.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madcampweek2.R
import com.example.madcampweek2.login.LoginActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val ivCheck = findViewById<View>(R.id.ivCheck)
        val ivbBlankPayCheck = findViewById<View>(R.id.ivbBlankPayCheck)

        val scaleX = ObjectAnimator.ofFloat(ivCheck, "scaleX", 0.5f, 1.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(ivCheck, "scaleY", 0.5f, 1.5f, 1f)
        scaleX.duration = 1000
        scaleY.duration = 1000

        val translationX = ObjectAnimator.ofFloat(ivbBlankPayCheck, "translationX", 1000f, 0f)
        translationX.duration = 1500

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, translationX)
        animatorSet.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}