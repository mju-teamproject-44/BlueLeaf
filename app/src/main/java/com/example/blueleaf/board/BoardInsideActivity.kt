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
import com.example.blueleaf.chat.Anony_dialog
import com.example.blueleaf.chat.ChatActivity
import com.example.blueleaf.comment.CommentLVAdapter
import com.example.blueleaf.comment.CommentModel
import com.example.blueleaf.databinding.ActivityBoardInsideBinding
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BoardInsideActivity : AppCompatActivity() {

    private val TAG = BoardInsideActivity::class.java.simpleName

    private lateinit var binding: ActivityBoardInsideBinding

    private lateinit var key: String
    private lateinit var writerUid: String
    private lateinit var writerUsername: String

    private val commentDataList = mutableListOf<CommentModel>()
    private lateinit var commentAdapter: CommentLVAdapter

    val database = Firebase.database.reference
    private lateinit var commentWriter: String

    override fun onCreate(savedInstanceState: Bundle?) {
        var currentUserInfo:String = ""

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


        binding.userArea.setOnClickListener {
            // CoroutineScope을 만들어서 비동기 작업을 수행
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    currentUserInfo = getUid()
                    Log.d("msgs", currentUserInfo)
                    if(currentUserInfo != writerUid){
                        if (currentUserInfo == "비회원 사용자") {
                            Log.d("msgs","성공!")
                            val intent = Intent(this@BoardInsideActivity, Anony_dialog::class.java)
                            this@BoardInsideActivity.startActivity(intent)
                        } else {
                            val intent = Intent(this@BoardInsideActivity, ChatActivity::class.java)
                            intent.putExtra("name", writerUsername)
                            intent.putExtra("uId", writerUid)
                            this@BoardInsideActivity.startActivity(intent)
                        }
                    }

                } catch (e: Exception) {
                    Log.e("msgs", "Error: ${e.message}")
                }
            }
        }
    }

    suspend fun getUid(): String {
        return suspendCancellableCoroutine { continuation ->
            FBRef.users.child(FBAuth.getUid()).get().addOnSuccessListener {
                val uid = it.child("uid").value as String
                continuation.resume(uid)
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    fun getCommentData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                commentDataList.clear()
                binding.commentCount.text = dataSnapshot.childrenCount.toString()
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
                    FBAuth.getTime(),
                    FBAuth.getUid()
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
//            intent.putExtra("boardCategory",boardCategory)
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
                    binding.boardTypeArea.text = dataModel!!.boardType

                    val myUid = FBAuth.getUid()
                    writerUid = dataModel.uid
                    writerUsername = dataModel!!.username
                    getProfileImage(writerUid)
                    if (myUid.equals(writerUid)) {
//                        Toast.makeText(baseContext, "글쓴이 O", Toast.LENGTH_LONG).show()
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
        val storageReference = FBRef.storageRef.child("board").child(key + ".png")

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

    private fun getProfileImage(writerUid:String) {
        val userUID = Firebase.auth.currentUser?.uid
        val storageProfileRef =
            FBRef.storageRef.child("profileImage").child(writerUid).child("profileImage.png")
        val profileImage = binding.profileImage
        storageProfileRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                if (profileImage != null) {
                    Glide.with(this)
                        .load(task.result)
                        .into(profileImage)
                }
            } else {
                profileImage!!.isVisible = false
            }
        })
    }
}