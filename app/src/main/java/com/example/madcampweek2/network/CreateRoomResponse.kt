package com.example.madcampweek2.network

data class CreateRoomResponse(
    val status: Int,
    val msg: String,
    val data: RoomData?
)

data class RoomData(
    val roomId: Int,
    val receiptId: Int,
    val title: String,
    val hostUuid: String,
    val friendUuids: List<String>,
    val numOfParticipants: Int,
    val items: List<ReceiptItem>,
    val totalPrice: String,
    val createdAt: String,
    val status: Int
)
