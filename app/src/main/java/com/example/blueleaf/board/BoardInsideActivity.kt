package com.example.blueleaf.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.blueleaf.R
import com.example.blueleaf.comment.CommentLVAdapter
import com.example.blueleaf.comment.CommentModel
import com.example.blueleaf.databinding.ActivityBoardInsideBinding
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BoardInsideActivity : AppCompatActivity() {

    private val TAG = BoardInsideActivity::class.java.simpleName

    private lateinit var binding: ActivityBoardInsideBinding

    private lateinit var key: String

    private val commentDataList = mutableListOf<CommentModel>()
    private lateinit var commentAdapter: CommentLVAdapter

    val database = Firebase.database.reference
    private lateinit var commentWriter: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_inside)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_inside)


        // username 연결
        database.child("users").child(FBAuth.getUid()).child("userName").get()
            .addOnSuccessListener {
                commentWriter = it.value.toString()
                Log.i("firebase", "Got value ${it.value}")
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        binding.boardSettingIcon.setOnClickListener {
            showDialog()
        }
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
        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImageData(key)

        commentAdapter = CommentLVAdapter(commentDataList)
        binding.commentLV.adapter = commentAdapter

        binding.commentBtn.setOnClickListener {
            insertComment(key)
        }


        getCommentData(key)
    }

    fun getCommentData(key:String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                commentDataList.clear()

                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(CommentModel::class.java)
                    commentDataList.add(item!!)
                }
                commentAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        FBRef.commentRef.child(key).addValueEventListener(postListener)
    }

    fun insertComment(key: String) {
        // comment
        //    - boardKey
        //       - commentKey
        //          - commentData
        //          - commentData
        //          - commentData
        FBRef.commentRef
            .child(key)
            .push()
            .setValue(
                CommentModel(
                    binding.commentArea.text.toString(),
                    commentWriter,
                    FBAuth.getTime()
                )

            )

        Toast.makeText(this, "댓글 입력 완료", Toast.LENGTH_LONG).show()
        binding.commentArea.setText("")
    }

    private fun showDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("게시글 수정/삭제")

        val alertDialog = mBuilder.show()
        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener {
            Toast.makeText(this, "editBtnClick", Toast.LENGTH_LONG).show()
            val intent = Intent(this, BoardEditActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }
        alertDialog.findViewById<Button>(R.id.removeBtn)?.setOnClickListener {
            FBRef.boardRef.child(key).removeValue() //
            Toast.makeText(this, "삭제 완료", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun getBoardData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 예외 처리를 해줘야 게시글이 삭제됐을 때 정상적으로 빠져나올 수 있다.
                try {
                    // BoardFragment.kt에서의 getFBBoardData와 다르게 하나의 값만 받아오면 되므로 반복문이 필요없다.
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    Log.d(TAG, dataModel!!.title)

                    binding.titleArea.text = dataModel!!.title
                    binding.contentArea.text = dataModel!!.content
                    binding.timeArea.text = dataModel!!.time
                    binding.usernameArea.text = dataModel!!.username

                    val myUid = FBAuth.getUid()
                    val writerUid = dataModel.uid

                    if (myUid.equals(writerUid)) {
                        Toast.makeText(baseContext, "글쓴이 O", Toast.LENGTH_LONG).show()
                        binding.boardSettingIcon.isVisible = true
                    } else {

                    }


                } catch (e: Exception) {
                    Log.d(TAG, "삭제 완료")
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }

    private fun getImageData(key: String) {
        // Reference to an image file in Cloud Storage
        val storageReference = FBRef.storageRef.child(key + ".png")

        // ImageView in your Activity
        val imageViewFromFB = binding.getImageArea

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
            } else {
                binding.getImageArea.isVisible = false
            }
        })
    }
}