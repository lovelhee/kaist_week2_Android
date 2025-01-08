package com.example.madcampweek2.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoomService {
    @GET("/rooms/findRoomId/{user_uuid}")
    fun findRoomIds(@Path("user_uuid") userUuid: String): Call<FindRoomIdsResponse>
}

data class FindRoomIdsResponse(
    val status: Int,
    val msg: String,
    val data: RoomIdsData
)

data class RoomIdsData(
    val roomIds: List<Int>
)