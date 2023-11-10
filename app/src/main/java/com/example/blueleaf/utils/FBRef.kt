package com.example.blueleaf.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

// 파이어베이스 레프런스를 담고 있는 클래스
class FBRef {

    companion object {

        private val database = Firebase.database
        val users = database.getReference("users")
        val category1 = database.getReference("contents")
        val category2 = database.getReference("contents2")
        val category3 = database.getReference("contents3")
        val category4 = database.getReference("contents4")
        val category5 = database.getReference("contents5")
        val category6 = database.getReference("contents6")

        val bookmarkRef = database.getReference("bookmark_list")

        val boardRef = database.getReference("board")

        val storageRef = Firebase.storage.reference

        val commentRef = database.getReference("comment")
    }

}