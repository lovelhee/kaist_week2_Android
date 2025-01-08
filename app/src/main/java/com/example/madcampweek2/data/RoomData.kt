package com.example.madcampweek2.data

data class RoomResponse(
    val status: Int,
    val msg: String,
    val data: RoomData
)

data class RoomData(
    val hostedRooms: List<String>,
    val participatingRooms: List<String>
)
