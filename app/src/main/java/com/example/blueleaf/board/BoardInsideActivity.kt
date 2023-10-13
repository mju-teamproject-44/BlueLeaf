package com.example.blueleaf.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ActivityBoardInsideBinding
import com.example.blueleaf.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BoardInsideActivity : AppCompatActivity() {

    private val TAG = BoardInsideActivity::class.java.simpleName

    private lateinit var binding: ActivityBoardInsideBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_inside)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_board_inside)

        // 첫 번째 방법으로 게시판 내용 전달 받기(각각의 내용을 전달받음)
//
//        // 각각의 값을 불러와 변수에 저장
//        val title = intent.getStringExtra("title").toString()
//        val content = intent.getStringExtra("content").toString()
//        val time = intent.getStringExtra("time").toString()
//        val uid = intent.getStringExtra("uid").toString()
//
//        // 지정한 레이아웃 위치(id를 통해 구분)에 각각의 값(내용) 연결
//        binding.titleArea.text = title
//        binding.contentArea.text = content
//        binding.timeArea.text = time
//        binding.uidArea.text = uid
//
//        // 아래는 정상적으로 값이 들어가는지 확인하기 위한 log 출력용 코드(없어도 된다)
//        Log.d(TAG,title)
//        Log.d(TAG,content)
//        Log.d(TAG,time)
//        Log.d(TAG,uid)

        // 두 번째 방법으로 게시판 내용 전달 받기(key값 하나만 전달받음)
        val key = intent.getStringExtra("key")
        getBoardData(key.toString())
        getImageData(key.toString())
    }

    private fun getBoardData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d(TAG, dataSnapshot.toString())
                // BoardFragment.kt에서의 getFBBoardData와 다르게 하나의 값만 받아오면 되므로 반복문이 필요없다.
                val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                Log.d(TAG,dataModel!!.title)

                binding.titleArea.text = dataModel!!.title
                binding.contentArea.text = dataModel!!.content
                binding.timeArea.text = dataModel!!.time
                binding.uidArea.text = dataModel!!.uid

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }

    private fun getImageData(key:String){
        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key+".png")

        // ImageView in your Activity
        val imageViewFromFB = binding.getImageArea

       storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener{ task->
           if(task.isSuccessful){
               Glide.with(this)
                   .load(task.result)
                   .into(imageViewFromFB)
           }else{

           }
       })
    }
}