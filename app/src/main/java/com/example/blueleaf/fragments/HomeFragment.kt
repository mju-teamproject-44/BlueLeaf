package com.example.blueleaf.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.blueleaf.R
import com.example.blueleaf.board.MyBoardPostsActivity
import com.example.blueleaf.contentsList.UserModel
import com.example.blueleaf.databinding.FragmentHomeBinding
import com.example.blueleaf.plantManage.NoPlantManageActivity
import com.example.blueleaf.plantManage.PlantManageActivity
import com.example.blueleaf.plantManage.PlantModel
import com.example.blueleaf.plantManage.TodoModel
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding

    //Firebase
    private lateinit var database : DatabaseReference
    private lateinit var uri: Uri
    lateinit var userRef: DatabaseReference
    lateinit var plantManageRef: DatabaseReference
    lateinit var plantTodoRef: DatabaseReference

    //#Datas
    private lateinit var key: String //식물 추가시에만 사용
    private val plantDataList = mutableListOf<PlantModel>()
    private val plantKeyList = mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        //Firebase Settings
        database = Firebase.database.reference
        val userUID = Firebase.auth.currentUser?.uid
        userRef = database.child("users").child(userUID!!)
        plantManageRef = database.child("plantManage").child(userUID!!)
        plantTodoRef = database.child("plantManage_todo").child(userUID!!)


        //Get user data from Firebase
        val userDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //유저 정보를 받는다.
                var userM : UserModel = UserModel("Temporary", "Temporary")
                userM = snapshot.getValue<UserModel>()!!

                //유저 정보를 ui에 업데이트 한다.
                setUserData(userM.userName.toString(), userM.userEmail.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                //Error
            }
        }
        userRef.addValueEventListener(userDataListener)


        //Get plantManage data from Firebase
        plantManageRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //식물 정보를 받는다.
                for (data in snapshot.children){
                    val plantItem = data.getValue(PlantModel::class.java)
                    plantDataList.add(plantItem!!)
                    plantKeyList.add(data.key.toString())
                }

                //식물의 개수 만큼 반복. (O(n^2))
                for(i in 0..<plantKeyList.size){
                    //만약 식물이 존재 하지 않는다면 break한다.
                    if(plantKeyList.size == 0){
                        break
                    }

                    //나의 식물 ui 설정
                    setPlant(i)

                    plantTodoRef.child(plantKeyList[i]).get().addOnSuccessListener {
                        val tempTodoKeyList = mutableListOf<String>()
                        val tempTodoDataList = mutableListOf<TodoModel>()

                        //일정의 FB위치 = plantManage_todo -> userUID -> plantkey -> todokey -> todoModel
                        for(data in it.children){
                            tempTodoKeyList.add(data.key.toString())
                        }

                        //일정의 개수 만큼 반복
                        for(j in 0..<tempTodoKeyList.size){
                            //만약 일정의 개수가 0이면 break 한다.
                            if(tempTodoKeyList.size == 0){
                                break;
                            }

                            plantTodoRef.child(plantKeyList[i]).child(tempTodoKeyList[j]).get().addOnSuccessListener {
                                //식물 벌 일정을 받음
                                val todoItem : TodoModel? = it.getValue(TodoModel::class.java)
                                tempTodoDataList.add(todoItem!!)

                                //일정의 개수가 0이면 종료한다.
                                if(tempTodoDataList.size == 0){
                                    return@addOnSuccessListener
                                }

                                val today = Calendar.getInstance() //오늘 날짜.
                                var selectDate = dateFormat.parse(tempTodoDataList[j].target_date) //목표일
                                var calcDate = calcDDay(selectDate, today.time)

                                //만약 계산한 날이 음수(DDay를 넘긴 경우)
                                val tempDate = Calendar.getInstance()
                                tempDate.time = selectDate

                                if(calcDate < 0){
                                    Log.d("일정 음수", "!!!")
                                    tempDate.add(Calendar.DATE, tempTodoDataList[j].cycle_date)
                                    tempTodoDataList[j].target_date = dateFormat.format(tempDate.time)
                                    val tempModel = TodoModel(tempTodoDataList[j].todo_code, tempTodoDataList[j].target_date, tempTodoDataList[j].cycle_date)
                                    plantTodoRef.child(plantKeyList[i]).child(tempTodoKeyList[j]).setValue(tempModel)
                                }

                                //식물 별 일정의 List를 남은 기간 순으로 정렬.
                                tempTodoDataList.sortBy {
                                    dateFormat.parse(it.target_date).time
                                }

                                //정렬 이후 index 0 -> DDay가 가장 짧은 일정
                                selectDate = dateFormat.parse(tempTodoDataList[0].target_date)
                                calcDate = calcDDay(selectDate, today.time)

                                val calcDate_s = "D-$calcDate" //D-Day toString
                                val todoType: Int = tempTodoDataList[0].todo_code //일정 분류

                                //식물 별, 일정 분류 별로 나누어 ui 업데이트
                                homePlantTodoIconReset(i, todoType, calcDate_s)
                            }
                        }
                    }.addOnFailureListener {
                        //Fail
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

       try {
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

        //각 식물칸 이벤트
        binding.homePlantFirst.setOnClickListener{
            movePlant(0)
        }
        binding.homePlantSecond.setOnClickListener{
            movePlant(1)
        }
        binding.homePlantThird.setOnClickListener{
            movePlant(2)
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

        // 나의 게시글 페이지로 이동
        binding.myBoardPosts.setOnClickListener{
            val intent = Intent(context, MyBoardPostsActivity::class.java)
            startActivity(intent)
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

    //Functions
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

    private fun homePlantTodoIconReset(i : Int, todoType : Int, calcDate_s: String){
        when (i) { //i = 식물, todoType = 일정 분류 코드(0~3)
            0 -> {
                binding.homePlantFirstLinear.visibility = LinearLayout.VISIBLE
                binding.ddayfirst.text = calcDate_s

                //초기화
                binding.firstLinearIconWater.visibility = ImageView.GONE
                binding.firstLinearIconPlant.visibility = ImageView.GONE
                binding.firstLinearIconFe.visibility = ImageView.GONE
                binding.firstLinearIconSun.visibility = ImageView.GONE

                when (todoType) {
                    0 -> {
                        binding.firstLinearIconWater.visibility = ImageView.VISIBLE
                    }
                    1 -> {
                        binding.firstLinearIconPlant.visibility = ImageView.VISIBLE
                    }
                    2 -> {
                        binding.firstLinearIconFe.visibility = ImageView.VISIBLE
                    }
                    3 -> {
                        binding.firstLinearIconSun.visibility = ImageView.VISIBLE
                    }
                }
            }

            1 -> {
                binding.homePlantSecondLinear.visibility = LinearLayout.VISIBLE
                binding.ddaysecond.text = calcDate_s

                //초기화
                binding.secondLinearIconWater.visibility = ImageView.GONE
                binding.secondLinearIconPlant.visibility = ImageView.GONE
                binding.secondLinearIconFe.visibility = ImageView.GONE
                binding.secondLinearIconSun.visibility = ImageView.GONE

                when (todoType) {
                    0 -> {
                        binding.secondLinearIconWater.visibility = ImageView.VISIBLE
                    }
                    1 -> {
                        binding.secondLinearIconPlant.visibility = ImageView.VISIBLE
                    }
                    2 -> {
                        binding.secondLinearIconFe.visibility = ImageView.VISIBLE
                    }
                    3 -> {
                        binding.secondLinearIconSun.visibility = ImageView.VISIBLE
                    }
                }
            }

            2 -> {
                binding.homePlantThirdLinear.visibility = LinearLayout.VISIBLE
                binding.ddaythird.text = calcDate_s

                //초기화
                binding.thirdLinearIconWater.visibility = ImageView.GONE
                binding.thirdLinearIconPlant.visibility = ImageView.GONE
                binding.thirdLinearIconFe.visibility = ImageView.GONE
                binding.thirdLinearIconSun.visibility = ImageView.GONE

                when (todoType) {
                    0 -> {
                        binding.thirdLinearIconWater.visibility = ImageView.VISIBLE
                    }
                    1 -> {
                        binding.thirdLinearIconPlant.visibility = ImageView.VISIBLE
                    }
                    2 -> {
                        binding.thirdLinearIconFe.visibility = ImageView.VISIBLE
                    }
                    3 -> {
                        binding.thirdLinearIconSun.visibility = ImageView.VISIBLE
                    }
                }
            }
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

    private fun calcDDay(d1 : Date, d2 : Date): Long {
        val i  = (d1.time - d2.time) / (60 * 60 * 24 * 1000)
        return i
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addPlantDialog(plantManageRef : DatabaseReference){
        //다이얼 로그를 띄운다.
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.manage_plant_add_dialog, null)

        val dialogText = dialogView.findViewById<EditText>(R.id.plantAddDialogEditText)

        builder.setView(dialogView)
            .setPositiveButton("확인"){ dialogInterface, i ->
                val plantName = dialogText.text.toString()

                Log.d("SuccessAddPlant", dialogText.text.toString())
                key = plantManageRef.push().key.toString()

                plantManageRef.child(key)
                    .setValue(PlantModel(plantName, LocalDate.now().toString()))

                val intent = Intent(requireContext(), PlantManageActivity::class.java)
                intent.putExtra("key", key)
                Log.d("Dialog Finish", key)
                startActivity(intent)
            }
            .setNegativeButton("취소") { dialogInterface, i ->
                Log.d("CancelAddPlant", "Canceled")
            }
            .show()
    }

    //나의 식물 정보 표시
    private fun setPlant(i : Int){
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

    //나의 식물 페이지로 이동
    @RequiresApi(Build.VERSION_CODES.O)
    private fun movePlant(i : Int){
        activity?.let{
            var intent: Intent
            if(plantKeyList.size < 1){
                intent = Intent(context, NoPlantManageActivity::class.java)
                startActivity(intent)
            } else if(plantKeyList.size < i + 1){
                addPlantDialog(plantManageRef)
            } else{
                intent = Intent(context, PlantManageActivity::class.java)
                intent.putExtra("key", plantKeyList[i])
                startActivity(intent)
            }
        }
    }
}

