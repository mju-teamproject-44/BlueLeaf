package com.example.blueleaf.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.blueleaf.MainActivity
import com.example.blueleaf.R
import com.example.blueleaf.SplashActivity
import com.example.blueleaf.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding

    //Database Reference
    private lateinit var database : DatabaseReference
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        //* Display & Editing UserName (Jinhyun)
        // #1. Variables
        database = Firebase.database.reference
        val userUID = Firebase.auth.currentUser?.uid
        var userName: String = "잎파랑이 사용자"
        var userEmail: String = "temporary@email.com"

        // #2. Function
        fun setUserName(newName: String){
            binding.homeUsernameTextView.text = newName
            binding.homeUsernameEditText.hint = newName

        }
        fun setEditMode(isEditing: Boolean){
            if(isEditing){
                binding.homeUsernameTextView.visibility = TextView.INVISIBLE
                binding.homeEditImageView.visibility = ImageView.GONE
                binding.homeUsernameEditText.visibility = TextView.VISIBLE
                binding.homeSaveImageView.visibility = ImageView.VISIBLE
            }
            else{
                binding.homeUsernameTextView.visibility = TextView.VISIBLE
                binding.homeEditImageView.visibility = ImageView.VISIBLE
                binding.homeUsernameEditText.visibility = TextView.GONE
                binding.homeSaveImageView.visibility = ImageView.GONE
            }
        }

       try {
           // #3. Details - Display UserName & Email
           if (userUID == null) {
               throw Exception("userUID is null")
           }
           //유저명 가져오기
           database.child("users").child(userUID!!).child("userName").get()
               .addOnSuccessListener {
                   userName = it.value.toString()
                   setUserName(userName)
                   Log.d("Success_userName", "userName = $userName")

               }.addOnFailureListener {
                   setUserName(userName)
                   Log.e("Fail_userName", "Failed to get user name.")
                   throw Exception("userName not found.")
               }
           //이메일 가져오기
           database.child("users").child(userUID!!).child("userEmail").get()
               .addOnSuccessListener {
                   userEmail = it.value.toString()
                   binding.homeEmailTextView.text = userEmail
                   Log.d("Success_userEmail", "userEmail = $userEmail")

               }.addOnFailureListener {
                   binding.homeEmailTextView.text = userEmail
                   Log.e("Fail_userEmail", "Failed to get user email.")
                   throw Exception("userEmail not found.")
               }

           //프로필 이미지 다운로드 후 업데이트
           imageDownload()

           // #4. Details - Editing UserName
           binding.homeEditImageView.setOnClickListener {
               setEditMode(true)
               binding.homeSaveImageView.setOnClickListener {
                   //Edit Local
                   val newUserName: String = binding.homeUsernameEditText.text.toString()
                   setUserName(newUserName)

                   //Edit Database
                   database.child("users").child(userUID!!).child("userName").setValue(newUserName)

                   setEditMode(false)
               }
           }
       }catch(e: Exception){
           Toast.makeText(context, "사용자 정보를 불러오는데 실패했습니다.", Toast.LENGTH_LONG).show()
           Log.e("Error", e.message.toString())
       }


        //* Profile image (Jinhyun)

        binding.homeProfileImageView.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            registerForActivityResult.launch(intent)
            binding.homeProfileImageUploadButton.visibility = Button.VISIBLE
        }
        // 등록하기 버튼을 눌렀을 경우
        binding.homeProfileImageUploadButton.setOnClickListener {
            //만약 사진을 선택하지 않고 나왔을 경우
            if(::uri.isInitialized){
                imageUpload(uri)
            }
            binding.homeProfileImageUploadButton.visibility = Button.GONE
        }



        //Navigate
        binding.homeTab.setOnClickListener(){

        }

        binding.informationTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_homeFragment_to_informationFragment)
        }

        binding.boardTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_homeFragment_to_boardFragment)
        }

        binding.bookmarkTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_homeFragment_to_bookmarkFragment)
        }

        binding.plantTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_homeFragment_to_plantFragment)
        }

        return binding.root
    }

    //storage
    private fun imageUpload(uri: Uri){
        val storage = Firebase.storage
        //val storageRef = storage.reference
        val userUID = Firebase.auth.currentUser?.uid
        val storageRef = storage.getReference("profileImage").child(userUID!!)

        if(userUID != null){
            // storage에 저장할 파일명 선언
            val fileName = "profileImage"
            val mountainsRef = storageRef.child("${fileName}.png")

            val uploadTask = mountainsRef.putFile(uri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // 파일 업로드 성공
                Toast.makeText(getActivity(), "사진 업로드 성공", Toast.LENGTH_SHORT).show();
            }.addOnFailureListener {
                // 파일 업로드 실패
                Toast.makeText(getActivity(), "사진 업로드 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun imageDownload() {
        val storage = Firebase.storage
        val userUID = Firebase.auth.currentUser?.uid
        val storageRef = storage.getReference("profileImage").child(userUID!!)

        // storage에서 가져올 파일명 선언
        val fileName = "profileImage"
        val mountainsRef = storageRef.child("${fileName}.png")
        val downloadTask = mountainsRef.downloadUrl
        downloadTask.addOnSuccessListener { uri ->
            // 파일 다운로드 성공
            // Glide를 사용하여 이미지를 ImageView에 직접 가져오기
            Glide.with(this).load(uri).into(binding.homeProfileImageView)
        }.addOnFailureListener {
            // 파일 다운로드 실패
        }
    }

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    uri = result.data?.data!!

                    // 다운 받은 이미지를 ImageView에 표시
                    binding.homeProfileImageView.setImageURI(uri)
                }
            }
        }

}

