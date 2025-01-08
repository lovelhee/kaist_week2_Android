package com.example.madcampweek2.data

data class ParticipantInStart(
    val uuid: String,
    val name: String,
    val amountOfMoney: String,
    val isPaid: Int
)

data class ReceiptItemInStart(
    val itemName: String,
    val price: String,
    val checked: Int
)