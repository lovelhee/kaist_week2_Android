package com.example.madcampweek2.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madcampweek2.R
import com.example.madcampweek2.data.GoogleAuthRequest
import com.example.madcampweek2.data.GoogleAuthResponse
import com.example.madcampweek2.home.HomeActivity
import com.example.madcampweek2.network.ApiClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            val email = account.email ?: "unknown"
            if (idToken != null) {
                sendTokenToServer(idToken, email)
            } else {
                Log.e("GoogleSignIn", "ID Token is null")
            }
        }
    }

    private fun sendTokenToServer(idToken: String, email: String) {
        val authService = ApiClient.authService
        val request = GoogleAuthRequest(gmail = email, token = idToken)

        authService.authenticateWithGoogle(request).enqueue(object : Callback<GoogleAuthResponse> {
            override fun onResponse(call: Call<GoogleAuthResponse>, response: Response<GoogleAuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()
                    if (authResponse?.status == 200) {
                        Log.d("GoogleSignIn", "서버 응답: ${authResponse.msg}")

                        // 사용자 정보 저장
                        saveUserInfo(authResponse.data.user.id.uuid, authResponse.data.user.name, authResponse.data.token)

                        // HomeActivity로 이동
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Log.e("GoogleSignIn", "로그인 실패: ${authResponse?.msg}")
                    }
                } else {
                    Log.e("GoogleSignIn", "서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GoogleAuthResponse>, t: Throwable) {
                Log.e("GoogleSignIn", "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun saveUserInfo(uuid: String, name: String, token: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("user_uuid", uuid)
            putString("user_name", name)
            Log.d("GoogleSignIn", name)
            putString("auth_token", token)
            apply()
        }

        Log.d("GoogleSignIn", "사용자 정보 저장 완료")
    }
}