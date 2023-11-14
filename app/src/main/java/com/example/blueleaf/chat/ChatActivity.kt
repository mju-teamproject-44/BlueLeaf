package com.example.blueleaf.chat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ActivityChatBinding
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
        Log.d("messi", "1")
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //초기화
        Log.d("messi", "2")

        messageList = ArrayList() // lateinit이기에 꼭 초기화해줘야 됨
        val messageAdapter:MessageAdapter = MessageAdapter(this,messageList)

        //RecyclerView
        Log.d("messi", "3")

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter


        // 넘어온 데이터 변수에 담기
        Log.d("messi", "4")
        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        Log.d("messi", "5")
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        Log.d("messi", "6")
        // 접속자 id
        val senderUid = mAuth.currentUser?.uid

        // 보낸 사람의 방
            senderRoom = receiverUid + senderUid
        // 받는 사람의 방
            receiverRoom = senderUid + receiverUid

        // 액션바에 상대방 이름 보여주기
        supportActionBar?.title = receiverName

        // 메시지 전송 버튼 이벤트
        Log.d("messi", "7")

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

            // 전송 시 메시지 칸 클리어
            binding.msgEdit.setText("")

        }

        Log.d("messi", "8")

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
                    /*
                    messageAdapter = MessageAdapter(this, messageList)
                    binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.chatRecyclerView.adapter = messageAdapter
                    */

                    Log.d("messi", messageList.toString())
                    // 호출되면 화면에 메시지 내용을 보여준다.
                    messageAdapter.notifyDataSetChanged()
                    Log.d("msgsNotify", "end")

                }

                override fun onCancelled(error: DatabaseError) {
                    //오류가 발생했을 때 실행
                }

            })
    }
}