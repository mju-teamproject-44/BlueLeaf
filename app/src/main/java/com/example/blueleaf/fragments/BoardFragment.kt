package com.example.blueleaf.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.blueleaf.R
import com.example.blueleaf.board.BoardListLVAdapter
import com.example.blueleaf.board.BoardModel
import com.example.blueleaf.board.BoardWriteActivity
import com.example.blueleaf.contentsList.ContentModel
import com.example.blueleaf.databinding.FragmentBoardBinding
import com.example.blueleaf.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
// pr test
class BoardFragment : Fragment() {
    private lateinit var binding:FragmentBoardBinding

    private val boardDataList = mutableListOf<BoardModel>()

    private lateinit var boardRVAdapter : BoardListLVAdapter

    private val TAG = BoardFragment::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_board,container,false)
        
        boardRVAdapter = BoardListLVAdapter(boardDataList)
        binding.boardListView.adapter = boardRVAdapter

        binding.writeBtn.setOnClickListener(){
            val intent = Intent(context, BoardWriteActivity::class.java)
            startActivity(intent)
        }

        binding.homeTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_boardFragment_to_homeFragment)
        }

        binding.informationTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_boardFragment_to_informationFragment)
        }

        binding.bookmarkTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_boardFragment_to_bookmarkFragment)
        }

        binding.plantTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_boardFragment_to_plantFragment)
        }

        getFBBoardData()

        return binding.root
    }

    private fun getFBBoardData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                boardDataList.clear()

                for (dataModel in dataSnapshot.children) {

                    Log.d(TAG,dataModel.toString())
                    val item = dataModel.getValue(BoardModel::class.java)
                    boardDataList.add(item!!)
                }

                // 동기화
                boardRVAdapter.notifyDataSetChanged()

                Log.d(TAG,boardDataList.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        FBRef.boardRef.addValueEventListener(postListener)
    }

}