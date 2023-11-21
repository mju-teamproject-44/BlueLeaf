package com.example.blueleaf.plantManage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blueleaf.databinding.ActivityPlantManageBinding
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.blueleaf.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PlantManageActivity : AppCompatActivity() {

    lateinit var key: String
    lateinit var database: DatabaseReference
    //lateinit var plantModel: PlantModel

    private var mBinding: ActivityPlantManageBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityPlantManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        key = intent.getStringExtra("key").toString()
        database = Firebase.database.reference
        val userUID = Firebase.auth.currentUser?.uid
        val plantRef = database.child("plantManage").child(userUID!!).child(key)
        Log.d("key", key)

        plantRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val plantM = snapshot.getValue(PlantModel::class.java)
                Log.d("onDataChange", plantM.toString())
                if (plantM != null) {
                    binding.plantManagePlantNameTextView.text = plantM.name
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled", "${error.toException()}")
            }
        })


        //Calendar RecyclerView
        val monthListManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val monthListAdapter = AdapterMonth()
        binding.plantManageCalendar.apply {
            layoutManager = monthListManager
            adapter = monthListAdapter
            scrollToPosition(Int.MAX_VALUE / 2)
        }
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.plantManageCalendar)


        //좌 상단 뒤로가기 버튼
        binding.plantManageBackImageView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //임시로 식물 삭제 설정(수정 예정)
        binding.plantManageMorePlantImageView.setOnClickListener{
            plantRef.removeValue()
            Toast.makeText(this, "식물 삭제 완료", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}