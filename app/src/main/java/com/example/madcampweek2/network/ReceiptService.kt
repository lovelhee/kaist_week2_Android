package com.example.madcampweek2.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ReceiptService {
    @Multipart
    @POST("/analyzeReceiptByGpt")
    fun analyzeReceipt(
        @Part img: MultipartBody.Part
    ): Call<AnalyzeReceiptResponse>
}

data class AnalyzeReceiptResponse(
    val status: Int,
    val msg: String,
    val data: ReceiptData
)

data class ReceiptData(
    val items: List<ReceiptItem>,
    val total_price: String
)

data class ReceiptItem(
    val menu: String,
    val details: String,
    val price: String
)