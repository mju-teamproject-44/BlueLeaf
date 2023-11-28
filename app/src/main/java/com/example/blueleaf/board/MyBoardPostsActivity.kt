package com.example.blueleaf.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.BookmarkRVAdapter
import com.example.blueleaf.databinding.ActivityBoardInsideBinding
import com.example.blueleaf.databinding.ActivityMyBoardPostsBinding
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyBoardPostsActivity : AppCompatActivity() {

    // log
    private val TAG = MyBoardPostsActivity::class.java.simpleName

    // viewBinding
    private lateinit var binding: ActivityMyBoardPostsBinding

    // data 관련
    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()
    private lateinit var boardRVAdapter: BoardListLVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_board_posts)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_board_posts)
        boardRVAdapter = BoardListLVAdapter(boardDataList)
        binding.boardListView.adapter = boardRVAdapter

        getFBBoardData()

        // list 클릭 시
        binding.boardListView.setOnItemClickListener{ parent,view,position,id->
            // 게시글로 이동
            val intent = Intent(this,BoardInsideActivity::class.java)
            intent.putExtra("key",boardKeyList[position])
            startActivity(intent)
        }

    }

    private fun getFBBoardData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 이전 리스트 데이터를 초기화하고 다시 불러온다.
                // 아래의 clear가 없으면 지금까지 쓴 게시글 리스트들 불러올 때마다 중첩됨
                boardDataList.clear()

                for (dataModel in dataSnapshot.children) {

                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(BoardModel::class.java)
                    if(item?.uid == FBAuth.getUid()){
                        boardDataList.add(item!!)
                        boardKeyList.add(dataModel.key.toString())
                    }
                }

                // 최신 게시글이 맨 위로 오게 한다 -> adapter와 동기화 전 list reverse
                // 최신 게시글이 맨 위로 오게 한다
                boardDataList.reverse()
                boardKeyList.reverse()

                // 자신의 게시글 개수 알려주는 TextView 설정
                binding.countPostsTV.setText("${boardDataList.count()} 개의 게시글을 작성하셨습니다")
                // 동기화
                boardRVAdapter.notifyDataSetChanged()

                Log.d(TAG, boardDataList.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        FBRef.boardRef.addValueEventListener(postListener)
    }
}