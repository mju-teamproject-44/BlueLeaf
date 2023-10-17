package com.example.blueleaf.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.blueleaf.MainActivity
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.UserModel
import com.example.blueleaf.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    //Database Reference
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = Firebase.auth

        binding.loginBtn.setOnClickListener{
            val email = binding.emailArea.text.toString()
            val password= binding.passwordArea.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        Toast.makeText(this,"로그인 성공", Toast.LENGTH_LONG).show()

                        // * 기존 유저 데이터(FB 연동이 안되어 있는 데이터) 임시 추가용.
                        database = Firebase.database.reference
                        val userUID = Firebase.auth.currentUser?.uid


                        if(userUID!=null){
                            database.child("users").child(userUID!!).child("userName").get().addOnSuccessListener {
                                if(it.value == null){
                                    val userData = UserModel("사용자", email)
                                    database.child("users").child(userUID!!).setValue(userData)
                                }
                            }.addOnFailureListener {
                                //
                            }
                        }else{
                            Toast.makeText(this,"uid fail", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        Toast.makeText(this,"로그인 실패", Toast.LENGTH_LONG).show()

                    }
                }
        }
    }
}