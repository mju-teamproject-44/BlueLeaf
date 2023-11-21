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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.blueleaf.Plant
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.UserModel
import com.example.blueleaf.databinding.FragmentHomeBinding
import com.example.blueleaf.plantManage.NoPlantManageActivity
import com.example.blueleaf.plantManage.PlantManageActivity
import com.example.blueleaf.plantManage.PlantModel
import com.example.blueleaf.setting.SettingActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding

    //Database Reference
    private lateinit var database : DatabaseReference
    private lateinit var uri: Uri

    //Plant Manage
    private val plantDataList = mutableListOf<PlantModel>()
    private val plantKeyList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        //* Display & Editing UserName (Jinhyun)
        database = Firebase.database.reference
        val userUID = Firebase.auth.currentUser?.uid
        val userRef = database.child("users").child(userUID!!)
        val plantManageRef = database.child("plantManage").child(userUID!!)

        val userDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var userM : UserModel = UserModel("Temporary", "Temporary")

                userM = snapshot.getValue<UserModel>()!!
                val userName_ = userM.userName.toString()
                val userEmail_ = userM.userEmail.toString()
                setUserData(userName_, userEmail_)
                Log.d("userDataListener, name", userName_)
                Log.d("UserDataListener, email", userEmail_)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        userRef.addValueEventListener(userDataListener)

        plantManageRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    Log.d("PlantModel", data.toString())
                    val plantItem = data.getValue(PlantModel::class.java)
                    plantDataList.add(plantItem!!)
                    plantKeyList.add(data.key.toString())
                }

                for (i: Int in 0..2){
                    if(i >= plantKeyList.size)
                        break

                    when(i){
                        0 -> {
                            binding.homePlantFirstAddButton.visibility = ImageView.GONE
                            binding.homePlantFirstTextView.visibility = TextView.VISIBLE
                            binding.homePlantFirstTextView.text = plantDataList[i].name
                            binding.homePlantSecondAddButton.visibility = ImageView.VISIBLE
                        }
                        1 -> {
                            binding.homePlantSecondAddButton.visibility = ImageView.GONE
                            binding.homePlantSecondTextView.visibility = TextView.VISIBLE
                            binding.homePlantSecondTextView.text = plantDataList[i].name
                            binding.homePlantThirdAddButton.visibility = ImageView.VISIBLE
                        }
                        2 -> {
                            binding.homePlantThirdAddButton.visibility = ImageView.GONE
                            binding.homePlantThirdTextView.visibility = TextView.VISIBLE
                            binding.homePlantThirdTextView.text = plantDataList[i].name
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })



       try {
           // #3. Details - Display UserName & Email
           if (userUID == null) {
               throw Exception("userUID is null")
           }

           //프로필 이미지 다운로드 후 업데이트
           imageDownload_preload()

           // #4. Details - Editing UserName
           binding.homeEditImageView.setOnClickListener {
               setEditMode(true)
               binding.homeUsernameEditText.setOnEditorActionListener{v, actionId, event ->
                   //Edit Local
                   val newUserName: String = binding.homeUsernameEditText.text.toString()
                   setUserData(newUserName)
                   //Edit Database
                   database.child("users").child(userUID!!).child("userName").setValue(newUserName)
                   setEditMode(false)
                   false
               }
               binding.homeSaveImageView.setOnClickListener {
                   //Edit Local
                   val newUserName: String = binding.homeUsernameEditText.text.toString()
                   setUserData(newUserName)
                   //Edit Database
                   database.child("users").child(userUID!!).child("userName").setValue(newUserName)
                   setEditMode(false)
               }
           }
       }catch(e: Exception){
           Toast.makeText(context, "사용자 정보를 불러오는데 실패했습니다.", Toast.LENGTH_LONG).show()
           Log.e("Error", e.message.toString())
       }

        binding.homePlantFirst.setOnClickListener{
            activity?.let{
                var intent: Intent
                if(plantKeyList.size < 1){
                    intent = Intent(context, NoPlantManageActivity::class.java)
                } else{
                    intent = Intent(context, PlantManageActivity::class.java)
                    intent.putExtra("key", plantKeyList[0])
                }
                startActivity(intent)
            }
        }

        binding.homePlantSecond.setOnClickListener{
            activity?.let{
                var intent: Intent
                if(plantKeyList.size < 2){
                    intent = Intent(context, NoPlantManageActivity::class.java)
                } else{
                    intent = Intent(context, PlantManageActivity::class.java)
                    intent.putExtra("key", plantKeyList[1])
                }
                startActivity(intent)
            }
        }

        binding.homePlantThird.setOnClickListener{
            activity?.let{
                var intent: Intent
                if(plantKeyList.size < 3){
                    intent = Intent(context, NoPlantManageActivity::class.java)
                } else{
                    intent = Intent(context, PlantManageActivity::class.java)
                    intent.putExtra("key", plantKeyList[2])
                }
                startActivity(intent)
            }
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

        // logout
        binding.settingBtn.setOnClickListener{
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
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

    private fun imageDownload_preload() {
        val storage = Firebase.storage
        val userUID = Firebase.auth.currentUser?.uid
        val storageRef = storage.getReference("profileImage").child(userUID!!)

        // storage에서 가져올 파일명 선언
        val fileName = "profileImage"
        val mountainsRef = storageRef.child("${fileName}.png")
        val downloadTask = mountainsRef.downloadUrl
        downloadTask.addOnSuccessListener { uri -> // 파일 다운로드 성공
            // Glide
            // #1. preload
            Glide.with(this)
                .load(uri)
                .preload()
            // #2. 기본 호출
            Glide.with(this).load(uri).into(binding.homeProfileImageView)

            // #3. 캐시에 저장된 이미지 있을 때만
            Glide.with(this).load(uri).onlyRetrieveFromCache(true).into(binding.homeProfileImageView)

            // #4. 디스크 캐시 전략
            Glide.with(this).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.homeProfileImageView)

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

    //Functions
    private fun setUserData(newName: String, newEmail: String){
        binding.homeUsernameTextView.text = newName
        binding.homeUsernameEditText.hint = newName
        binding.homeEmailTextView.text = newEmail
    }
    private fun setUserData(newName: String){
        binding.homeUsernameTextView.text = newName
        binding.homeUsernameEditText.hint = newName
    }
    private fun setEditMode(isEditing: Boolean){
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

}

