package com.example.blueleaf.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.blueleaf.MainActivity
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.UserModel
import com.example.blueleaf.databinding.ActivityIntroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class IntroActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityIntroBinding

    //Database Reference
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        binding.loginBtn.setOnClickListener(){
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        binding.joinBtn.setOnClickListener(){
            val intent = Intent(this,JoinActivity::class.java)
            startActivity(intent)
        }

        binding.noAccountBtn.setOnClickListener(){
            auth = Firebase.auth
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        // * 익명 유저 데이터 FB 연동
                        database = Firebase.database.reference
                        val userUID = Firebase.auth.currentUser?.uid

                        if(userUID!=null){
                            val userData = UserModel("비회원 사용자", "비회원 사용자")
                            database.child("users").child(userUID!!).setValue(userData)
                        }else{
                            Toast.makeText(this,"uid fail", Toast.LENGTH_LONG).show()
                        }


                        // Sign in success, update UI with the signed-in user's information
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        Toast.makeText(this,"익명 로그인 성공", Toast.LENGTH_LONG).show()

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this,"익명 로그인 실패", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}