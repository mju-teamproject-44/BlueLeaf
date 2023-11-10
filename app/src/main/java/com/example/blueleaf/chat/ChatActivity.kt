package com.example.blueleaf.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatActivity:AppCompatActivity() {
    // 메시지를 받는 사람에 대한 정보
    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var binding:ActivityChatBinding

    lateinit var mAuth:FirebaseAuth //인증 객체
    lateinit var mDbRef:DatabaseReference //DB 객체
    private lateinit var receiverRoom:String // 받는 대화방
    private lateinit var senderRoom:String // 보낸 대화방

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 넘어온 데이터 변수에 담기
        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        // 접속자 id
        val senderUid = mAuth.currentUser?.uid

        // 보낸 사람의 방
            senderRoom = receiverUid + senderUid
        // 받는 사람의 방
            receiverRoom = senderUid + receiverUid

        // 액션바에 상대방 이름 보여주기
        supportActionBar?.title = receiverName

        // 메시지 전송 버튼 이벤트
        binding.sendBtn.setOnClickListener{
            val message = binding.msgEdit.text.toString()
            val messsageObj = Message(message,senderUid.toString())
            // 전송 시 메시지 칸 클리어
            binding.msgEdit.text.clear()

            //데이터 저장
            mDbRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messsageObj).addOnSuccessListener {
                    // 저장 성공 시
                    mDbRef.child("chats").child(receiverRoom).child("messages").push()
                        .setValue(messsageObj)
                }

        }
    }
}