package com.example.blueleaf.board

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.blueleaf.R
import com.example.blueleaf.databinding.FragmentBoardShowBinding
import com.example.blueleaf.databinding.FragmentBoardTransBinding
import com.example.blueleaf.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BoardTransFragment : Fragment() {
    private val TAG = BoardTransFragment::class.java.simpleName

    // viewBinding 관련
    private var _binding: FragmentBoardTransBinding? = null
    private val binding get() = _binding!!

    // data 관련
    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()
    private lateinit var boardRVAdapter: BoardListLVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBoardTransBinding.inflate(inflater, container, false)
        boardRVAdapter = BoardListLVAdapter(boardDataList)
        binding.boardListView.adapter = boardRVAdapter


        binding.boardListView.setOnItemClickListener { parent, view, position, id ->
            // 첫 번째 방법 : listview에 있는 데이터 title content time 다 다른 액티비티로 전달해줘서 만들기
//            val intent = Intent(context,BoardInsideActivity::class.java)
//            intent.putExtra("title",boardDataList[position].title)
//            intent.putExtra("content",boardDataList[position].content)
//            intent.putExtra("time",boardDataList[position].time)
//            intent.putExtra("uid",boardDataList[position].uid)
//            startActivity(intent)
            // 두 번째 방법 : Firebase에 있는 board에 대한 데이터의 id를 기반으로 다시 데이터를 받아오는 방법

            // activity 넘기기
            val intent = Intent(context, BoardInsideActivity::class.java)
            intent.putExtra("key", boardKeyList[position]) // 첫 번째 방법과 다르게 key값 하나만 전달해준다
            intent.putExtra("boardCategory","거래 게시판")
            startActivity(intent)

        }
        getFBBoardData()
        return binding.root
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    private fun getFBBoardData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 이전 리스트 데이터를 초기화하고 다시 불러온다.
                // 아래의 clear가 없으면 지금까지 쓴 게시글 리스트들 불러올 때마다 중첩됨
                boardDataList.clear()

                for (dataModel in dataSnapshot.children) {

                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(BoardModel::class.java)
                    boardDataList.add(item!!)
                    boardKeyList.add(dataModel.key.toString())
                }

                // 최신 게시글이 맨 위로 오게 한다 -> adapter와 동기화 전 list reverse
                // 최신 게시글이 맨 위로 오게 한다
                boardDataList.reverse()
                boardKeyList.reverse()

                // 동기화
                boardRVAdapter.notifyDataSetChanged()

                Log.d(TAG, boardDataList.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        FBRef.boardTransRef.addValueEventListener(postListener)
    }


}