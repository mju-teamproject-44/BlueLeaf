package com.example.blueleaf.utils

import com.google.firebase.auth.FirebaseAuth

// 유저의 uid를 뽑아오는 util 함수
class FBAuth {

    companion object {

        private lateinit var auth: FirebaseAuth

        fun getUid() : String {

            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()

        }

    }

}