package com.example.blueleaf.board

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.blueleaf.R
import com.example.blueleaf.utils.FBAuth


class BoardListLVAdapter(val boardList: MutableList<BoardModel>) : BaseAdapter() {


    private val TAG = BoardInsideActivity::class.java.simpleName

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
        cvView =
            LayoutInflater.from(parent?.context).inflate(R.layout.board_list_item, parent, false)
//        }

        val itemLinearLayoutView = cvView?.findViewById<LinearLayout>(R.id.itemView)

        // 사용자가 입력한 title 연결
        val title = cvView?.findViewById<TextView>(R.id.titleArea)
        title!!.text = boardList[position].title

        // 사용자가 입력한 content 연결
        val content = cvView?.findViewById<TextView>(R.id.contentArea)
        content!!.text = boardList[position].content

        // 사용자가 게시물 작성한 시간 time 연결
        val time = cvView?.findViewById<TextView>(R.id.timeArea)
        time!!.text = boardList[position].time

//        // 사용자의 uid 연결
//        val uid = cvView?.findViewById<TextView>(R.id.uidArea)
//        uid!!.text = boardList[position].uid

        // 사용자의 username 연결
        val username = cvView?.findViewById<TextView>(R.id.usernameArea)
        username!!.text = boardList[position].username


        if (boardList[position].uid.equals(FBAuth.getUid())) {
            itemLinearLayoutView?.setBackgroundColor(Color.parseColor("#D0ECE1"))
        }


        return cvView!!
    }
}