package com.example.blueleaf.contentsList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R


class UserAdapter(private val userList: MutableList<UserModel>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.user_layout,parent,false)
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
        holder.nameText.text = currentUser.userName
    }

    class UserViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val nameText:TextView = itemView.findViewById(R.id.name_text)
    }


}