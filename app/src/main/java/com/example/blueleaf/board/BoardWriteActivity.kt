package com.example.blueleaf.board

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.BookmarkModel
import com.example.blueleaf.databinding.ActivityBoardWriteBinding
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.example.blueleaf.utils.FBRef.Companion.storageRef
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class BoardWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardWriteBinding

    private val TAG = BoardWriteActivity::class.java.simpleName
    private lateinit var key: String
    private lateinit var image: String
    private var isImageUpload = false

    private lateinit var selectedBoard: String

    val database = Firebase.database.reference
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_write)

        var username = ""

        // username 연결
        database.child("users").child(FBAuth.getUid()).child("userName").get()
            .addOnSuccessListener {
                username = it.value.toString()
                Log.i("firebase", "Got value ${it.value}")
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        // 게시판 선택 spinner 관련
        var spinnerData = resources.getStringArray(R.array.boards)
        var spinnerAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerData)
        binding.boardSpinner.adapter = spinnerAdapter

        // spinner 리스너
        binding.boardSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedBoard = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        // write
        binding.writeBtn.setOnClickListener {
            val title = binding.titleArea.text.toString()
            val content = binding.contentArea.text.toString()
            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()



            Log.d(TAG, title)
            Log.d(TAG, content)

            // 파이어베이스 store에 이미지를 저장하고 싶습니다
            // 만약에 내가 게시글을 클릭했을 때, 게시글에 대한 정보를 받아와야 하는데
            // 이미지 이름에 대한 정보를 모르기
            // 이미지 이름을 문서의 key값으로 해줘서 이미지에 대한 정보를 찾기 쉽게 해놓음

            // key값 설정
            when (selectedBoard) {
                "정보 게시판" -> {
                    key = FBRef.boardInfoRef.push().key.toString()
                    image = key
                    FBRef.boardInfoRef
                        .child(key)
                        .setValue(BoardModel(image,title,content,uid,username,time))
                }
                "식물 자랑 게시판" -> {
                    key = FBRef.boardShowRef.push().key.toString()
                    image = key
                    FBRef.boardShowRef
                        .child(key)
                        .setValue(BoardModel(image,title,content,uid,username,time))
                }
                "거래 게시판" -> {
                    key = FBRef.boardTransRef.push().key.toString()
                    image = key
                    FBRef.boardTransRef
                        .child(key)
                        .setValue(BoardModel(image,title,content,uid,username,time))
                }
            }

            Toast.makeText(this, "게시글 입력 완료", Toast.LENGTH_LONG).show()

            if (isImageUpload == true) {
                imageUpload(key)
            }
            finish()

        }

        // 이미지 업로드
        binding.imageArea.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true
        }

    }

    private fun imageUpload(key: String) {
        // Get the data from an ImageView as bytes
//        val storage = Firebase.storage
//        val storageRef = storage.reference

        val mountainsRef = storageRef.child("board").child(key + ".png")

        val imageView = binding.imageArea
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            binding.imageArea.setImageURI(data?.data)
        }
    }
}