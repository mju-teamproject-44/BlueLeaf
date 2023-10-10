package com.example.blueleaf.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.blueleaf.R
import org.w3c.dom.Text

class BoardListLVAdapter(val boardList:MutableList<BoardModel>): BaseAdapter() {
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
        if(cvView==null){
            cvView = LayoutInflater.from(parent?.context).inflate(R.layout.board_list_item,parent,false)
        }

        // 사용자가 입력한 title 연결
        val title = cvView?.findViewById<TextView>(R.id.titleArea)
        title!!.text = boardList[position].title

        // 사용자가 입력한 content 연결
        val content = cvView?.findViewById<TextView>(R.id.contentArea)
        content!!.text = boardList[position].content

        // 사용자가 게시물 작성한 시간 time 연결
        val time = cvView?.findViewById<TextView>(R.id.timeArea)
        time!!.text = boardList[position].time

        // 사용자의 uid 연결
        val uid = cvView?.findViewById<TextView>(R.id.uidArea)
        uid!!.text = boardList[position].uid

        return cvView!!
    }
}