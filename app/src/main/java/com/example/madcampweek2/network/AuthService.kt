package com.example.madcampweek2.network

import com.example.madcampweek2.data.GoogleAuthRequest
import com.example.madcampweek2.data.GoogleAuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/google")
    fun authenticateWithGoogle(
        @Body request: GoogleAuthRequest
    ): Call<GoogleAuthResponse>
}