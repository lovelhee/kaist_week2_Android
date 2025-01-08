package com.example.madcampweek2.data

data class RoomResponse(
    val status: Int,
    val msg: String,
    val data: RoomData
)

data class RoomData(
    val hostedRooms: List<RoomDetail>,
    val participatingRooms: List<RoomDetail>
)

data class RoomDetail(
    val id: Int,
    val title: String
)
data class RoomTag(
    val id: Int,    // 방 ID
    val tag: String, // 태그 ("호스트" 또는 "참여자")
    val title: String // 방 제목
)