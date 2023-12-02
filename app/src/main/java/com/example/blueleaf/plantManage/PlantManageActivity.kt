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
import java.text.SimpleDateFormat

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

                //Quick Sort를 이용해서, 두 리스트를 동시에 정렬한다.
                todoQuickSort(plantTodoDataList, plantTodoKeyList, 0, plantTodoDataList.size-1)

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

    private fun todoQuickSort(plantTodoDataList: MutableList<TodoModel>, plantTodoKeyList: MutableList<String>, start: Int, end: Int) {
        if (start + 1 > end) return
        val pivot_arr = plantTodoDataList[end]
        val pivot_arr2 = plantTodoKeyList[end]
        val pivot = dateFormat.parse(plantTodoDataList[end].target_date).time
        var i = start - 1

        for (j in start..end-1) {
            if (dateFormat.parse(plantTodoDataList[j].target_date).time < pivot) {
                i += 1
                val term = plantTodoDataList[j]
                plantTodoDataList[j] = plantTodoDataList[i]
                plantTodoDataList[i] = term

                val term2 = plantTodoKeyList[j]
                plantTodoKeyList[j] = plantTodoKeyList[i]
                plantTodoKeyList[i] = term2
            }
        }

        plantTodoDataList[end] = plantTodoDataList[i+1]
        plantTodoDataList[i+1] = pivot_arr

        plantTodoKeyList[end] = plantTodoKeyList[i+1]
        plantTodoKeyList[i+1] = pivot_arr2

        todoQuickSort(plantTodoDataList, plantTodoKeyList, start, i) // pivot 기존 왼쪽 배열 정렬
        todoQuickSort(plantTodoDataList, plantTodoKeyList, i+2, end) // pivot 기존 오른쪽 배열 정렬
    }


}