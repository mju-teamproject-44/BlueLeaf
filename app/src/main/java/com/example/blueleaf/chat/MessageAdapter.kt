package com.example.blueleaf.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val receive = 1 // 받는 타입
    private val send = 2 // 보내는 타입
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("msgsViewHolder", "second")
        Log.d("msgsViewHolder", "msgLsit: " + messageList.toString())
        Log.d("msgsViweHolder", "viewType" + viewType.toString())
        if(viewType == 1){ // 받는 화면 -> 받는 쪽 이면 ReceiveViewHolder생성
            Log.d("msgsViewHolder", "receive in onCreateViewHolder")
            val view:View = LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            Log.d("msgsViewHolder", view.toString())
            return ReceiveViewHolder(view)
        }else{// 보내는 화면
            Log.d("msgsViewHolder", "send in onCreateViewHolder")
            val view:View = LayoutInflater.from(context).inflate(R.layout.send,parent,false)
            Log.d("msgsViewHolder", view.toString())
            return SendViewHolder(view) // 여기서 view타입이 안 맞아서 문젠가?
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("msgsBind", position.toString())
        // 현재 메시지
        val currentMsg = messageList[position]
        // 보내는 데이터
        if(holder.javaClass == SendViewHolder::class.java) {
            Log.d("msgsBind", "send in 바인드" )
            val viewHolder = holder as SendViewHolder // 타입 변환
            //viewHolder.sendMessage.text = currentMsg.message
            viewHolder.sendMessage.text = currentMsg.message
        }else{ //받는 데이터
            Log.d("msgsBind", "receive in 바인드" )
            val viewHolder = holder as ReceiveViewHolder // 타입 변환
            //viewHolder.sendMessage.text = currentMsg.message
            viewHolder.receiveMessage.text = currentMsg.message
            Log.d("msgsBind", "next" )

        }
    }

    override fun getItemViewType(position: Int): Int {
        // 메시지 값
        val currentMessage = messageList[position]
        Log.d("msgsView", "first")
        Log.d("msgsView", messageList.toString())
        Log.d("msgsView","ViewType " + currentMessage.toString())
        /*
        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.sendId)){
            return send
        }else{
            return receive
        }
        */
        Log.d("msgsView", "Log in user: " + FirebaseAuth.getInstance().currentUser?.uid.toString())
        Log.d("msgsView", "msg master: " + currentMessage.sendId.toString())
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.sendId)){
            Log.d("msgsView","send")
            return send
        }else{
            Log.d("msgsView", "receive")
            return receive
        }

    }
    override fun getItemCount(): Int {
        Log.d("msgsCount", messageList.size.toString())
        return messageList.size
    }


    // 보낸 쪽
    class SendViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val sendMessage: TextView = itemView.findViewById(R.id.send_message_text)

    }
    // 받는 쪽
    class ReceiveViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        // 받는 쪽 View를 전달받아 객체를 생성한다.
        val receiveMessage: TextView = itemView.findViewById(R.id.receive_message_text)

    }

}