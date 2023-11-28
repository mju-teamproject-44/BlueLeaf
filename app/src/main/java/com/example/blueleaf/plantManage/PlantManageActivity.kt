package com.example.blueleaf.plantManage

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blueleaf.databinding.ActivityPlantManageBinding
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.blueleaf.MainActivity
import com.example.blueleaf.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Array.set
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class PlantManageActivity : AppCompatActivity() {

    //Firebase
    lateinit var database: DatabaseReference
    lateinit var todoRef: DatabaseReference
    lateinit var plantRef: DatabaseReference

    //ExtraPath
    lateinit var key: String

    //PlantTodo List
    private val plantTodoKeyList = mutableListOf<String>()
    private val plantTodoDataList = mutableListOf<TodoModel>()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    //Binding
    private var mBinding: ActivityPlantManageBinding? = null
    private val binding get() = mBinding!!

    lateinit var todoListAdapter: AdapterTodo
    lateinit var monthListAdapter: AdapterMonth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //#Binding Settings
        mBinding = ActivityPlantManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //#Firebase Settings
        database = Firebase.database.reference
        val userUID = Firebase.auth.currentUser?.uid

        //#Get Extra Data
        //key From(HomeFragment, TodoAddActivity)
        key = intent.getStringExtra("key").toString()
        plantRef = database.child("plantManage").child(userUID!!).child(key)
        todoRef = database.child("plantManage_todo").child(userUID).child(key)

        //#Get Firebase Data - PlantModel
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

        //#Get Firebase Data - TodoModel, Todokey
        todoRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //정보를 받아들이고, list에 저장한다.
                for (data in snapshot.children){
                    Log.d("PlantModel", data.toString())
                    val plantTodoItem = data.getValue(TodoModel::class.java)
                    plantTodoDataList.add(plantTodoItem!!)
                    plantTodoKeyList.add(data.key.toString())
                }


                //Data Sort
                plantTodoDataList.sortBy {
                    dateFormat.parse(it.target_date).time
                }

                //RVAdapter update
                todoListAdapter.notifyDataSetChanged()
                monthListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled", "${error.toException()}")
            }
        })


        //Calendar RecyclerView
        val monthListManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        monthListAdapter = AdapterMonth(key, plantTodoDataList)

        binding.plantManageCalendar.apply {
            layoutManager = monthListManager
            adapter = monthListAdapter
            scrollToPosition(Int.MAX_VALUE / 2)
        }
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.plantManageCalendar)


        //일정 Recyclerview
        val todoListManager = LinearLayoutManager(this)
        todoListAdapter = AdapterTodo(plantTodoDataList, plantTodoKeyList, key)
        binding.plantManageTodolist.apply {
            layoutManager = todoListManager
            adapter = todoListAdapter
        }


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