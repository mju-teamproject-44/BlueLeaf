package com.example.blueleaf.chat

data class Message(
    var message: String?,
    var sendId: String
){
    constructor():this("","")
}
