package com.example.blueleaf.board

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.blueleaf.R
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.core.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView


class BoardListLVAdapter(val boardList: MutableList<BoardModel>) : BaseAdapter() {



    override fun getCount(): Int {
        return boardList.size
    }

    override fun getItem(position: Int): Any {
        return boardList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var cvView = convertView
//        if(cvView==null){
        cvView = LayoutInflater.from(parent?.context).inflate(R.layout.board_list_item, parent, false)
//        }

        val itemView = cvView?.findViewById<LinearLayout>(R.id.itemView)

//        // 사용자가 추가한 img 연결
//        val image = cvView?.findViewById<ImageView>(R.id.imageArea)
//        image!!.setImageURI(boardList[position].image)

        // Reference to an image file in Cloud Storage
        val storageReference = FBRef.storageRef.child("board").child(boardList[position].key + ".png")

        // ImageView in your Activity
        val imageViewFromFB = cvView?.findViewById<ImageView>(R.id.imageArea)

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                if (imageViewFromFB != null) {
                    Glide.with(cvView)
                        .load(task.result)
                        .into(imageViewFromFB)
                }
            } else {
                    imageViewFromFB!!.isVisible = false
            }
        })

        // 사용자가 입력한 title 연결
        val title = cvView?.findViewById<TextView>(R.id.titleArea)
        title!!.text = boardList[position].title

        // 사용자가 입력한 content 연결
        val content = cvView?.findViewById<TextView>(R.id.contentArea)
        content!!.text = boardList[position].content

        // 사용자가 게시물 작성한 시간 time 연결
        val time = cvView?.findViewById<TextView>(R.id.timeArea)
        time!!.text = boardList[position].time

        // 사용자의 프로필 이미지 연결
        val userUID = boardList[position].uid
        val storageProfileRef = FBRef.storageRef.child("profileImage").child(userUID!!).child("profileImage.png")
        val profileImage = cvView.findViewById<CircleImageView>(R.id.profileImage)
        storageProfileRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                if (profileImage != null) {
                    Glide.with(cvView)
                        .load(task.result)
                        .into(profileImage)
                }
            } else {
                profileImage!!.isVisible = false
            }
        })

//        // 사용자의 uid 연결
//        val uid = cvView?.findViewById<TextView>(R.id.uidArea)
//        uid!!.text = boardList[position].uid

        // 사용자의 username 연결
        val username = cvView?.findViewById<TextView>(R.id.usernameArea)
        username!!.text = boardList[position].username


        if (boardList[position].uid.equals(FBAuth.getUid())) {
            itemView?.setBackgroundColor(Color.parseColor("#D0ECE1"))
        }


        return cvView!!
    }
}