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

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>, private val receiverName:String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val receive = 1 // 받는 타입
    private val send = 2 // 보내는 타입
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){ // 받는 화면 -> 받는 쪽 이면 ReceiveViewHolder생성
            val view:View = LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            return ReceiveViewHolder(view)
        }else{// 보내는 화면
            val view:View = LayoutInflater.from(context).inflate(R.layout.send,parent,false)
            return SendViewHolder(view) // 여기서 view타입이 안 맞아서 문젠가?
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 현재 메시지
        val currentMsg = messageList[position]
        // 보내는 데이터
        if(holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder // 타입 변환
            //viewHolder.sendMessage.text = currentMsg.message
            viewHolder.sendMessage.text = currentMsg.message
        }else{ //받는 데이터
            val viewHolder = holder as ReceiveViewHolder // 타입 변환
            //viewHolder.sendMessage.text = currentMsg.message
            viewHolder.receiveMessage.text = currentMsg.message
            viewHolder.partnerName.text = receiverName

        }
    }

    override fun getItemViewType(position: Int): Int {
        // 메시지 값
        val currentMessage = messageList[position]

        /*
        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.sendId)){
            return send
        }else{
            return receive
        }
        */

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
        val partnerName:TextView = itemView.findViewById(R.id.partnerName)

    }

}