package com.example.madcampweek2.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface UserService {
    @GET("/rooms/findUuid/{name}")
    fun findUserByName(
        @Path("name") name: String
    ): Call<FindUserResponse>

    @GET("/rooms/getRooms/{user_uuid}")
    fun getRooms(
        @Path("user_uuid") userUuid: String
    ): Call<RoomResponse>
}

// 서버에서 반환하는 성공 응답 데이터 클래스
data class FindUserResponse(
    val status: Int,
    val msg: String,
    val data: UserData
)

data class UserData(
    val uuid: UuidData
)

data class UuidData(
    val uuid: String
)

data class RoomResponse(
    val status: Int,
    val msg: String,
    val data: RoomListData
)

data class RoomListData(
    val hostedRooms: List<String>,
    val participatingRooms: List<String>
)
