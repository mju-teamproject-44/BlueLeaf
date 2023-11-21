package com.example.blueleaf.plantManage

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.blueleaf.MainActivity
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ActivityNoPlantManageBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class NoPlantManageActivity : AppCompatActivity() {

    private var mBinding: ActivityNoPlantManageBinding? = null
    private val binding get() = mBinding!!

    private lateinit var database: DatabaseReference
    private lateinit var key: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding
        mBinding = ActivityNoPlantManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firebase
        database = Firebase.database.reference
        val userUID = Firebase.auth.currentUser?.uid
        val plantRef = database.child("plantManage").child(userUID!!)


        //좌상단 뒤로가기 버튼
        binding.noPlantManageBackImageView.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        //식물 추가 버튼
        binding.noPlantManageAddPlantImageView.setOnClickListener {
            //다이얼 로그를 띄운다.
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.manage_plant_add_dialog, null)

            val dialogText = dialogView.findViewById<EditText>(R.id.plantAddDialogEditText)

            builder.setView(dialogView)
                .setPositiveButton("확인"){ dialogInterface, i ->
                    val plantName = dialogText.text.toString()

                    Log.d("SuccessAddPlant", dialogText.text.toString())
                    key = plantRef.push().key.toString()

                    plantRef.child(key)
                        .setValue(PlantModel(plantName, LocalDate.now().toString()))

                    val intent = Intent(this, PlantManageActivity::class.java)
                    intent.putExtra("key", key)
                    Log.d("Dialog Finish", key)
                    startActivity(intent)
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    Log.d("CancelAddPlant", "Canceled")
                }
                .show()
        }
    }
}