package com.example.blueleaf.contentsList

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blueleaf.R
import com.example.blueleaf.chat.Anony_dialog
import com.example.blueleaf.chat.ChatActivity
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserAdapter(val context: Context, private var userList: MutableList<UserModel>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    fun setFilteredList(userList: MutableList<UserModel>){
        this.userList = userList
        notifyDataSetChanged()
    }

    lateinit var user_layout_view:View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.user_layout,parent,false)
        user_layout_view = LayoutInflater.from(parent?.context).inflate(R.layout.user_layout,parent,false)
        Log.d("hola", "1")
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("hola", "2")
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        Log.d("hola", "3")
        val currentUser = userList[position]
        var currentUserInfo:String = ""
        val storageProfileRef = FBRef.storageRef.child("profileImage").child(currentUser.uid!!).child("profileImage.png")
        val imgView = holder.chat_profile

        holder.nameText.text = currentUser.userName
        storageProfileRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->

            if (task.isSuccessful) {
                Glide.with(user_layout_view)
                    .load(task.result)
                    .into(imgView)

            } else {
                Log.d("msgs", "실패")

            }
        })
        holder.itemView.setOnClickListener {
            // CoroutineScope을 만들어서 비동기 작업을 수행
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    currentUserInfo = getUid()
                    Log.d("msgs", currentUserInfo)

                    if (currentUserInfo == "비회원 사용자") {
                        Log.d("msgs","성공!")
                        val intent = Intent(context, Anony_dialog::class.java)
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("name", currentUser.userName)
                        intent.putExtra("uId", currentUser.uid)
                        context.startActivity(intent)
                    }
                } catch (e: Exception) {
                    Log.e("msgs", "Error: ${e.message}")
                }
            }
        }

    }

    suspend fun getUid(): String {
        return suspendCancellableCoroutine { continuation ->
            FBRef.users.child(FBAuth.getUid()).get().addOnSuccessListener {
                val uid = it.child("uid").value as String
                continuation.resume(uid)
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }
    class UserViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val nameText:TextView = itemView.findViewById(R.id.name_text)
        val chat_profile:ImageView = itemView.findViewById(R.id.chat_profile)
    }


}