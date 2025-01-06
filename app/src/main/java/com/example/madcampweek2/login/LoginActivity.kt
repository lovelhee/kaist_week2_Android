package com.example.madcampweek2.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madcampweek2.R
import com.example.madcampweek2.home.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Google Sign-In 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("984252637303-4adk5dif0m1m2cah4esuofdf68cucvdq.apps.googleusercontent.com") // Google Cloud에서 생성된 클라이언트 ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInClient.signOut().addOnCompleteListener {
            Log.d("GoogleSignIn", "자동 로그아웃 완료")
        }

        // Google 로그인 버튼 클릭 이벤트
        findViewById<ImageView>(R.id.ivGoogleLogin).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // 로그인 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("GoogleSignIn", "Account: $account")
                handleSignInResult(account)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-In failed: ${e.statusCode}", e)
                when (e.statusCode) {
                    10 -> Log.e("GoogleSignIn", "Error 10: Check SHA-1 and package name in Google Cloud Console.")
                    else -> Log.e("GoogleSignIn", "Unhandled error: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount?) {
        if (account != null) {
            Log.d("GoogleSignIn", "Sign-In successful: ${account.email}")

            // 서버로 ID 토큰 전송
            val idToken = account.idToken
            sendTokenToServer(idToken)

            // HomeActivity로 이동
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun sendTokenToServer(idToken: String?) {
        // 서버와 연동하여 ID 토큰을 검증하는 코드 작성
        Log.d("GoogleSignIn", "Token sent to server: $idToken")
    }
}