package com.example.blueleaf.contentsList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ContentListActivity : AppCompatActivity() {
    lateinit var myRef : DatabaseReference
    val bookmarkIdList = mutableListOf<String>()
    lateinit var rvAdapter: ContentRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_list)

        val rv:RecyclerView = findViewById(R.id.rv)
        val items = mutableListOf<ContentModel>()
        val database = Firebase.database
        val itemKeyList = mutableListOf<String>() // 키값을 저장한다.
        val rvAdapter = ContentRVAdapter(baseContext, items, itemKeyList, bookmarkIdList)
        val category = intent.getStringExtra("category")
        Log.d("CLA", category.toString())

        if(category == "category1") {
            myRef = database.getReference("contents")
        } else if(category == "category2") {
            myRef = database.getReference("contents2")
        } else if(category == "category3") {
            myRef = database.getReference("contents3")
        } else if(category == "category4") {
            myRef = database.getReference("contents4")
        } else if(category == "category5") {
            myRef = database.getReference("contents5")
        } else if(category == "category6") {
            myRef = database.getReference("contents6")
        }
        // 데이터 읽기 (컨텐츠 리스트 읽기)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 스냅샷에 데이터가 있다!
                Log.d("ContentsListActivity", dataSnapshot.toString())

                for (dataModel in dataSnapshot.children) {
                    // 모든 원인은 비동기(동시에 일어나지 않는)속성 때문이다.
                    // DB데이터를 끌어와 스택 PUSH 하는 과
                    // 비동기라서 리사이클러뷰가 이미 만들어졌을 때 아직 데이터는 나중에 불러와
                    // 그래서 이미 만들어진 리사이클러뷰에 데이터가 안들어가 있음
                    val item = dataModel.getValue(ContentModel::class.java)
                    items.add(item!!)
                    itemKeyList.add(dataModel.key.toString())
                }

                rvAdapter.notifyDataSetChanged()
                // 그래서 데이터가 다 받아오고 나서는 이 어댑터를 새롭게 refresh 해줘라. 동기화 해줘라!
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentsListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }

        myRef.addValueEventListener(postListener)
        rv.adapter = rvAdapter
        rv.layoutManager = GridLayoutManager(this,2)

    }

}