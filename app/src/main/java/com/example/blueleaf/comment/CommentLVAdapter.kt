package com.example.blueleaf.comment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.blueleaf.R
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class CommentLVAdapter(val commentList: MutableList<CommentModel>): BaseAdapter() {
    override fun getCount(): Int {
        return commentList.size
    }

    override fun getItem(position: Int): Any {
        return commentList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var cvView = convertView
        cvView = LayoutInflater.from(parent?.context).inflate(R.layout.comment_list_item, parent, false)

//        if(cvView == null){
//            cvView = LayoutInflater.from(parent?.context).inflate(R.layout.comment_list_item, parent, false)
//        }


        // 사용자가 입력한 title 연결
        val title = cvView?.findViewById<TextView>(R.id.titleArea)
        title!!.text = commentList[position].commentTitle

        // username 연결
        val username = cvView?.findViewById<TextView>(R.id.usernameArea)
        username!!.text = commentList[position].commentWriter

        // 사용자가 댓글 작성한 시간 time 연결
        val time = cvView?.findViewById<TextView>(R.id.timeArea)
        time!!.text = commentList[position].commentCreatedTime

        // 사용자의 프로필 이미지 연결
        val userUID = Firebase.auth.currentUser?.uid
        val storageProfileRef = FBRef.storageRef.child("profileImage").child(userUID!!).child("profileImage.png")
        val profileImage = cvView?.findViewById<CircleImageView>(R.id.profileImage)
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

        return cvView!!
    }

}