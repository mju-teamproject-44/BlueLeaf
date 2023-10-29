package com.example.blueleaf.board

import java.net.URI

data class BoardModel(
    val image: String = "",
    val title: String = "",
    val content: String = "",
    val uid: String = "",
    val username: String = "",
    val time: String = ""
)