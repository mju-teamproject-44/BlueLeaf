package com.example.blueleaf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import com.example.blueleaf.auth.IntroActivity
import com.example.blueleaf.contentsList.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    //Database Reference
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth

        // 이미 로그인 된 유저가 아니면 IntroActivity로 이동
        if(auth.currentUser?.uid == null) {
            Handler().postDelayed({
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }, 3000)

        }
        // 이미 로그인 된 유저이면 Main으로 이동
        else {

            // * 기존 유저 데이터(FB 연동이 안되어 있는 데이터) 임시 추가용
            database = Firebase.database.reference
            val userUID = Firebase.auth.currentUser?.uid


            if(userUID!=null){
                database.child("users").child(userUID!!).child("userName").get().addOnSuccessListener {
                    if(it.value == null){
                        val userData = UserModel("사용자", "temporary@email.com")
                        database.child("users").child(userUID!!).setValue(userData)
                    }
                }.addOnFailureListener {
                    //
                }
            }else{
                Toast.makeText(this,"uid fail", Toast.LENGTH_LONG).show()
            }

            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 3000)
        }

    }
}