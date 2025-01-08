package com.example.madcampweek2.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ReceiptService {
    @Multipart
    @POST("/analyzeReceiptByGpt")
    fun analyzeReceipt(
        @Part img: MultipartBody.Part
    ): Call<AnalyzeReceiptResponse>

    @POST("/rooms/createRoom")
    fun createRoom(@Body request: CreateRoomRequest): Call<CreateRoomResponse>

    @GET("/receipt/getReceiptItems/{room_id}")
    fun getReceiptItems(@Path("room_id") roomId: Int): Call<GetReceiptItemsResponse>

    @POST("/receipt/updateChecks")
    fun updateChecks(@Body body: UpdateChecksRequest): Call<Void>

    @GET("/receipt/getReceiptItemsWithCheckStatus/{room_id}/{user_uuid}")
    fun getReceiptItemsWithCheckStatus(
        @Path("room_id") roomId: Int,
        @Path("user_uuid") userUuid: String
    ): Call<GetReceiptItemsWithCheckResponse>

}

data class CreateRoomRequest(
    val title: String,
    val host_uuid: String,
    val friend_uuids: List<String>,
    val items: List<Map<String, String>>,
    val total_price: String
)

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

data class GetReceiptItemsResponse(
    val status: Int,
    val msg: String,
    val data: List<ReceiptItemData>
)

data class ReceiptItemData(
    val id: Int,
    val itemName: String,
    val details: String,
    val price: Int
)

data class UpdateChecksRequest(
    val userUuid: String,
    val updates: List<ReceiptUpdate>
)

data class ReceiptUpdate(
    val receiptItemId: Int,
    val checked: Int
)

data class ReceiptItemDataWithCheck(
    val id: Int,          // API에서 고유 ID로 반환되는 필드
    val itemName: String, // 메뉴 이름
    val price: String,    // 가격
    val checked: Int      // 체크 상태 (0: false, 1: true)
)

data class GetReceiptItemsWithCheckResponse(
    val status: Int,
    val msg: String,
    val data: List<ReceiptItemDataWithCheck>
)