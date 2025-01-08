package com.example.madcampweek2.data

data class UpdateRoomStatusResponse(
    val status: Int,
    val msg: String,
    val data: Any? // `data`가 null일 경우 Any?로 처리
)

data class UpdateRoomStatusRequest(
    val roomId: Int,
    val status: Int
)