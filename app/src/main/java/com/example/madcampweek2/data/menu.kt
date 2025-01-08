package com.example.madcampweek2.data

data class ParticipantInMenu(
    val userUuid: String,
    val name: String,
    val items: List<MenuItem>,
    val total: String // 총합을 저장할 필드 추가
)

data class MenuItem(
    val name: String,
    val price: String
)
