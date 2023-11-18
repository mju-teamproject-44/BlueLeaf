package com.example.blueleaf.chat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ActivityChatBinding
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity:AppCompatActivity() {
    // 메시지를 받는 사람에 대한 정보
    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var binding:ActivityChatBinding

    lateinit var mAuth:FirebaseAuth //인증 객체
    lateinit var mDbRef:DatabaseReference //DB 객체
    private lateinit var receiverRoom:String // 받는 대화방
    private lateinit var senderRoom:String // 보낸 대화방
    private lateinit var messageList: ArrayList<Message>
    //lateinit var messageAdapter:MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 넘어온 데이터 변수에 담기
        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        // 메시지 리스트 초기화, Adapter 초기화
        messageList = ArrayList() // lateinit이기에 꼭 초기화해줘야 됨
        val messageAdapter:MessageAdapter = MessageAdapter(this,messageList,receiverName)

        // 레이아웃 메니저 설정, 어뎁터 연결
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        // FB 인스턴스
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        // 접속자 id
        val senderUid = mAuth.currentUser?.uid

        // 보낸 사람의 방
            senderRoom = receiverUid + senderUid
        // 받는 사람의 방
            receiverRoom = senderUid + receiverUid

        // 상대방 이름을 채팅방 상단에
        binding.chatUserName.setText(receiverName)


        // 메시지 전송 이벤트
        binding.sendBtn.setOnClickListener{
            val message = binding.msgEdit.text.toString()
            val messsageObj = Message(message,senderUid.toString())
            //데이터 저장
            mDbRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messsageObj).addOnSuccessListener {
                    // 저장 성공 시
                    mDbRef.child("chats").child(receiverRoom).child("messages").push()
                        .setValue(messsageObj)
                }

            // 전송 시 메시지 칸 초기화
            binding.msgEdit.setText("")

        }

        //메시지 가져오기
        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("messi", "8.5")

                    // chats -> senderRoom -> messages안의 데이터가 변경되면 이 함수가 실행된다.
                    messageList.clear() // 실행이 되면 messageList안에 데이터를 비워둔다.

                    for(postSnapshot in snapshot.children) {
                        val msg = postSnapshot.getValue(Message::class.java)
                        messageList.add(msg!!)
                    }


                    Log.d("messi", messageList.toString())
                    messageAdapter.notifyDataSetChanged()
                    Log.d("msgsNotify", "end")

                }

                override fun onCancelled(error: DatabaseError) {
                    //오류가 발생했을 때 실행
                }

            })
    }
}