package com.example.blueleaf.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// 파이어베이스 레프런스를 담고 있는 클래스
class FBRef {

    companion object {

        private val database = Firebase.database

        val category1 = database.getReference("contents")
        val category2 = database.getReference("contents2")

        val bookmarkRef = database.getReference("bookmark_list")


    }

}