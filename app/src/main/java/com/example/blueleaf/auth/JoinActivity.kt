package com.example.blueleaf.auth

import android.content.Intent
import android.health.connect.datatypes.units.Length
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.blueleaf.MainActivity
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.UserModel
import com.example.blueleaf.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityJoinBinding

    //Database Reference
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(this,R.layout.activity_join) // 회원가입 레이아웃을 가져옴

        // 회원가입 레이아웃의 회원가입 버튼을 가져와 클릭 리스너를 부착
        // 이 부분에서 입력값을 가져오고 유효성 검사를 한다
        binding.joinBtn.setOnClickListener {
            // 버튼을 클릭했을 당시의 email, password1, password2를 가져옴
            val email = binding.emailArea.text.toString()
            val password1 = binding.passwordArea1.text.toString()
            val password2 = binding.passwordArea2.text.toString()
            var isJoinable = true // 회원 가입 가능 여부, 유효성 검사 중에 한 가지만 걸려도 false

            // 이메일을 입력하지 않았을 경우
            if(email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_LONG).show()
                isJoinable = false
            }

            // 비밀번호1을 입력하지 않았을 경우
            if(password1.isEmpty()) {
                Toast.makeText(this,"비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()
                isJoinable = false

            }

            // 비밀번호2를 입력하지 않았을 경우
            if(password2.isEmpty()) {
                Toast.makeText(this, "비빌먼호를 확인을 해주세요", Toast.LENGTH_LONG).show()
                isJoinable = false

            }

            // 비밀번호1과 비밀번호2가 일치하지 않는 경우
            if(!password1.equals(password2)) {
                Toast.makeText(this,"비밀번호가 일치 하지 않습니다", Toast.LENGTH_LONG).show()
                isJoinable = false

            }

            if(password1.length < 6) {
                Toast.makeText(this,"비밀번호는 최소 7자 이상이여야 합니다", Toast.LENGTH_LONG).show()
                isJoinable = false
            }

            if(isJoinable) {
                // fb 회원가입 로직
                auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"회원가입 성공", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)

                            // 로그인 성공 후 MainActivity 진입 시 뒤로 가기를 하면 다시 회원 가입 form이 나오는 경우를 위한 코드
                            // 기존 까지의 Activity를 다 날려 버린다는 의미
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                            // * 유저 데이터(사용자 명 및 이메일)을 Firebase에 저장 (Jinhyun)
                            database = Firebase.database.reference
                            val userUID = Firebase.auth.currentUser?.uid

                            if(userUID!=null){
                                val userData = UserModel("사용자", email)
                                database.child("users").child(userUID!!).setValue(userData)
                            }else{
                                Toast.makeText(this,"uid fail", Toast.LENGTH_LONG).show()
                            }

                            startActivity(intent)
                        } else {
                            Toast.makeText(this,"회원가입 실패", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }


    }
}