package com.example.madcampweek2.data

data class ReceiptSummaryResponse(
    val status: Int,
    val msg: String,
    val data: ReceiptSummary?
)

data class ReceiptSummary(
    val participants: List<Participant>,
    val userChecks: Map<String, UserCheck>,
    val userTotals: Map<String, UserTotal>,
    val uncheckedItems: List<UncheckedItem>
)

data class Participant(
    val userUuid: String,
    val name: String
)

data class UserCheck(
    val name: String,
    val items: List<UserItem>
)

data class UserItem(
    val name: String,
    val price: String
)

data class UserTotal(
    val name: String,
    val total: String
)

data class UncheckedItem(
    val itemName: String,
    val price: String
)