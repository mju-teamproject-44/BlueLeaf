package com.example.blueleaf.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ActivityBoardSearchBinding
import com.example.blueleaf.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BoardSearchActivity : AppCompatActivity(), BoardDataCallback {

    private val TAG = BoardSearchActivity::class.java.simpleName
    private lateinit var binding: ActivityBoardSearchBinding
    private var boardDataList = mutableListOf<BoardModel>()
    private var boardKeyList = mutableListOf<String>()
    private lateinit var tempBoardDataList: List<BoardModel>
    private lateinit var boardRVAdapter: BoardListLVAdapter
    val database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_search)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_search)
        boardRVAdapter = BoardListLVAdapter(boardDataList)
        binding.boardListView.adapter = boardRVAdapter


        // 각 게시판 노드에 대해 데이터 가져오기
//        getFBBoardData(FBRef.boardInfoRef, this)
//        getFBBoardData(FBRef.boardShowRef, this)
//        getFBBoardData(FBRef.boardTransRef, this)
        getFBBoardData(FBRef.boardRef,this)

        //searchBoardData(boardDataList)
        //initSearchView()

        binding.boardListView.setOnItemClickListener { parent, view, position, id ->
            Log.d(TAG, "setONItemClickListener안입니다 -> \n$boardDataList")
            // 두 번째 방법 : Firebase에 있는 board에 대한 데이터의 id를 기반으로 다시 데이터를 받아오는 방법
            // activity 넘기기
            val intent = Intent(this, BoardInsideActivity::class.java)
            intent.putExtra("key", boardKeyList[position]) // 첫 번째 방법과 다르게 key값 하나만 전달해준다
            // 문제 있는 부분
//            Log.d(TAG, "intent로 넘겨주기 전 boardDataList[position].boardType 상태 -> ${boardDataList[position].boardType}")
//            intent.putExtra("boardCategory", boardDataList[position].boardType)
            startActivity(intent)

        }

        //searchView 관련
        binding.boardSearchView.isSubmitButtonEnabled = true
        binding.boardSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
//                searchBoardData(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchBoardData(newText)
                return false
            }
        })
    }


    override fun onBoardDataLoaded(
        boardDataList: MutableList<BoardModel>,
        boardKeyList: MutableList<String>
    ) {
        // ui 업데이트 또는 추가 작업 수행
        Log.d(TAG, "boardDataList의 상태(getFBBoardData로 담은 후) \n$boardDataList")
        // 기존 게시글 데이터 전체를 저장한다
        tempBoardDataList = boardDataList.map { it.copy() }
        Log.d(TAG, "tempBoardDataList의 상태(getFBBoardData로 담은 후) \n$tempBoardDataList")
    }

    private fun searchBoardData(text: String) {
        Log.d(TAG, "searchBoardData 입니다 *$text*")
        Log.d(TAG, "boardDataList의 상태 \n$boardDataList")
        Log.d(TAG, "tempBoardDataList의 상태 \n$tempBoardDataList")

        // 탐색 결과로 나온 게시글을 담기 위해 기존 게시글 데이터 초기화
        boardDataList.clear()
        boardKeyList.clear()
        Log.d(TAG, "boardDataList의 상태(clear후) \n$boardDataList")
        Log.d(TAG, "tempBoardDataList의 상태(clear나온 후) \n$tempBoardDataList")

        // 문자 입력 X -> 모든 데이터를 보여준다
        if (text.isEmpty()) {
//            boardDataList = tempBoardDataList.map{it.copy()}.toMutableList()
//            Log.d(TAG, "boardDataList의 상태(문자입력X) $boardDataList")
//            Log.d(TAG, "tempBoardDataList의 상태(clear후) $tempBoardDataList")
        }
        // 문자 입력 O
        else {
            // 리스트의 모든 데이터 검색
            for (i in tempBoardDataList) {
                if (searchBoardTitle(i, text)) {
                    Log.d(TAG, "i의 상태 ->$i")
                    boardDataList.add(i)
                    boardKeyList.add(i.key)
                    Log.d(TAG, "boardDataList의 상태 \n$boardDataList")
                }
            }
        }
        Log.d(TAG, "[갱신 전] boardDataList의 상태 \n$boardDataList")

        binding.searchResult.setText("${boardDataList.count()}개의 게시글이 검색되었습니다.")
        // 리스트 adapter 갱신
        boardRVAdapter.notifyDataSetChanged()
    }

    private fun searchBoardTitle(board: BoardModel, text: String): Boolean {
        // title에 대한 검색
        Log.d(TAG, "searchBoardTitle 입니다 text 상태 -> $text")
        var titlePlusContent = board.title + board.content
        Log.d(TAG,"TF상태 -> ${titlePlusContent.contains(text)}")
        return titlePlusContent.contains(text)
    }

    private fun getFBBoardData(bRef: DatabaseReference, callback: BoardDataCallback) {

        bRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 이전 리스트 데이터를 초기화하고 다시 불러온다.
                // 아래의 clear가 없으면 지금까지 쓴 게시글 리스트들 불러올 때마다 중첩됨
                // boardDataList.clear()

                for (dataModel in dataSnapshot.children) {

//                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(BoardModel::class.java)
                    boardDataList.add(item!!)
                    boardKeyList.add(dataModel.key.toString())
                }

                Log.d(TAG, "boardDataList의 상태(GETFBBOARDDATA) \n$boardDataList")

                // 최신 게시글이 맨 위로 오게 한다 -> adapter와 동기화 전 list reverse
                // 최신 게시글이 맨 위로 오게 한다
                boardDataList.reverse()
                boardKeyList.reverse()

                // 동기화0
                boardRVAdapter.notifyDataSetChanged()

                callback.onBoardDataLoaded(boardDataList, boardKeyList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}

interface BoardDataCallback {
    fun onBoardDataLoaded(boardDataList: MutableList<BoardModel>, boardKeyList: MutableList<String>)
}