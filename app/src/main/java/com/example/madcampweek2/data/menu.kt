package com.example.madcampweek2.data

data class ParticipantInMenu(
    val userUuid: String,
    val name: String,
    val items: List<MenuItem>
)

data class MenuItem(
    val name: String,
    val price: String
)
