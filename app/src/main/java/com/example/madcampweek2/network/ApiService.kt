package com.example.madcampweek2.network

import com.example.madcampweek2.data.ApiResponse
import com.example.madcampweek2.data.ParticipantInStart
import com.example.madcampweek2.data.ReceiptSummaryResponse
import com.example.madcampweek2.data.RoomResponse
import com.example.madcampweek2.data.UpdateRoomStatusRequest
import com.example.madcampweek2.data.UpdateRoomStatusResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @GET("/rooms/getRooms/{user_uuid}")
    suspend fun getRooms(@Path("user_uuid") userUuid: String?): Response<RoomResponse>

    @GET("/receipt/getReceiptSummary/{room_id}")
    suspend fun getReceiptSummary(@Path("room_id") roomId: Int): Response<ReceiptSummaryResponse>

    @POST("/rooms/updateRoomStatus")
    suspend fun updateRoomStatus(@Body requestBody: UpdateRoomStatusRequest): Response<UpdateRoomStatusResponse>

    @GET("/rooms/getRoomParticipants/{roomId}")
    suspend fun getRoomParticipants(@Path("roomId") roomId: Int): Response<ApiResponse<List<ParticipantInStart>>>

}