package com.example.madcampweek2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://172.10.7.18:8000"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val receiptService: ReceiptService = retrofit.create(ReceiptService::class.java)
    val userService: UserService = retrofit.create(UserService::class.java)
}