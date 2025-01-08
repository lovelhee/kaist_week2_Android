package com.example.madcampweek2.network

import com.example.madcampweek2.data.RoomResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response

interface ApiService {
    @GET("/rooms/getRooms/{user_uuid}")
    suspend fun getRooms(@Path("user_uuid") userUuid: String): Response<RoomResponse>
}