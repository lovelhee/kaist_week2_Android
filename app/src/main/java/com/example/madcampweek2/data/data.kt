package com.example.madcampweek2.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.sql.Timestamp

// Users Table
data class User(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("name") val name: String
)

// Rooms Table
data class Room(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("hostUuid") val hostUuid: String,
    @SerializedName("numOfParticipants") val numOfParticipants: Int,
    @SerializedName("createdAt") val createdAt: Timestamp,
    @SerializedName("status") val status: Int // 0: End, 1: In Progress
)

// RoomParticipants Table
data class RoomParticipant(
    @SerializedName("roomId") val roomId: Int,
    @SerializedName("userUuid") val userUuid: String,
    @SerializedName("amountOfMoney") val amountOfMoney: BigDecimal,
    @SerializedName("isPaid") val isPaid: Boolean
)

// Tokens Table
data class Token(
    @SerializedName("userUuid") val userUuid: String,
    @SerializedName("tokenValue") val tokenValue: String,
    @SerializedName("issuedAt") val issuedAt: Timestamp,
    @SerializedName("expiresAt") val expiresAt: Timestamp
)

// Receipts Table
data class Receipt(
    @SerializedName("id") val id: Int,
    @SerializedName("roomId") val roomId: Int
)

// ReceiptItems Table
data class ReceiptItem(
    @SerializedName("id") val id: Int,
    @SerializedName("receiptId") val receiptId: Int,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("numOfCheckedItems") val numOfCheckedItems: Int,
    @SerializedName("details") val details: String,
    @SerializedName("price") val price: BigDecimal
)

// UserItemChecks Table
data class UserItemCheck(
    @SerializedName("receiptItemId") val receiptItemId: Int,
    @SerializedName("userUuid") val userUuid: String,
    @SerializedName("checked") val checked: Boolean
)

// UserAccounts Table
data class UserAccount(
    @SerializedName("id") val id: Int,
    @SerializedName("userUuid") val userUuid: String,
    @SerializedName("bank") val bank: String,
    @SerializedName("accountNumber") val accountNumber: String // 암호화된 데이터
)

// 로그인
// Request Body
data class GoogleAuthRequest(
    val gmail: String,
    val token: String
)

// Response Body
data class GoogleAuthResponse(
    val status: Int,
    val msg: String,
    val data: AuthData
)

data class AuthData(
    val user: UserData,
    val token: String
)

data class UserData(
    val id: UserId,
    val name: String
)

data class UserId(
    val uuid: String
)

