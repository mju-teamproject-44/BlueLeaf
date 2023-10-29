package com.example.blueleaf.comment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.blueleaf.R
import com.example.blueleaf.utils.FBAuth

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

        if(cvView == null){
            cvView = LayoutInflater.from(parent?.context).inflate(R.layout.comment_list_item, parent, false)
        }


        // 사용자가 입력한 title 연결
        val title = cvView?.findViewById<TextView>(R.id.titleArea)
        title!!.text = commentList[position].commentTitle

        // username 연결
        val username = cvView?.findViewById<TextView>(R.id.usernameArea)
        username!!.text = commentList[position].commentWriter

        // 사용자가 댓글 작성한 시간 time 연결
        val time = cvView?.findViewById<TextView>(R.id.timeArea)
        time!!.text = commentList[position].commentCreatedTime


        return cvView!!
    }

}