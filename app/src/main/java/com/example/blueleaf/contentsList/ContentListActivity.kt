package com.example.blueleaf.contentsList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
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

    fun quickSort(arr: MutableList<ContentModel>, left: Int = 0, right: Int = arr.size - 1) {
        if(left >= right) return

        val pivot = partition(arr, left, right)

        quickSort(arr, left, pivot - 1)
        quickSort(arr, pivot + 1, right)
    }

    fun partition(arr: MutableList<ContentModel>, left: Int, right: Int): Int {
        var low = left + 1
        var high = right
        val pivot = arr[left].score

        while (low <= high) {
            while (arr[low].score > pivot && low < high) low++
            while (arr[high].score <= pivot && low <= high) high--

            if(low < high)
                swap(arr, low, high)
            else
                break
        }

        swap(arr, left, high)

        return high
    }

    fun swap(arr: MutableList<ContentModel>, i: Int, j: Int) {
        var temp = arr[i]
        arr[i] = arr[j]
        arr[j] = temp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_list)

        val rv:RecyclerView = findViewById(R.id.rv)
        val infoTitle = findViewById<TextView>(R.id.infoTitle)
        val sortButton = findViewById<Button>(R.id.sortButton)
        val items = mutableListOf<ContentModel>() // fb 아이템 저장
        val database = Firebase.database
        val itemKeyList = mutableListOf<String>() // fb 아이템 키값을 저장한다.
        // rvAdapter = ContentRVAdapter(baseContext, items, itemKeyList, bookmarkIdList)
        val category = intent.getStringExtra("category")
        var krCategoryName = "";

        if(category == "category1") {
            myRef = database.getReference("contents")
            krCategoryName = "공기 정화 식물"
        } else if(category == "category2") {
            myRef = database.getReference("contents2")
            krCategoryName = "선인장"
        } else if(category == "category3") {
            myRef = database.getReference("contents3")
            krCategoryName = "산세베리아"
        } else if(category == "category4") {
            myRef = database.getReference("contents4")
            krCategoryName = "금전수"
        } else if(category == "category5") {
            myRef = database.getReference("contents5")
            krCategoryName = "꽃"
        } else if(category == "category6") {
            myRef = database.getReference("contents6")
            krCategoryName = "실내 정원 식물"
        }
        rvAdapter = ContentRVAdapter(baseContext, items, itemKeyList, bookmarkIdList,category!!)

        sortButton.setOnClickListener {
            quickSort(items)
            rvAdapter.notifyDataSetChanged()
        }

        infoTitle.text = krCategoryName
        // 데이터 읽기 (컨텐츠 리스트 읽기), 컨텐츠 받아오기
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 스냅샷에 데이터가 있다!
                Log.d("ContentsListActivity", dataSnapshot.toString())
                items.clear()
                Log.d("sort1",items.toString())

                for (dataModel in dataSnapshot.children) {
                    // 모든 원인은 비동기(동시에 일어나지 않는)속성 때문이다.
                    // DB데이터를 끌어와 스택 PUSH 하는 과정
                    // 비동기라서 리사이클러뷰가 이미 만들어졌을 때 아직 데이터는 나중에 불러와
                    // 그래서 이미 만들어진 리사이클러뷰에 데이터가 안들어가 있음
                    val item = dataModel.getValue(ContentModel::class.java)
                    items.add(item!!)
                    itemKeyList.add(dataModel.key.toString()) // 아이템들의 키를 받아옴. (카테고리를 클릭해야 받아 온다?)
                }

                rvAdapter.notifyDataSetChanged()

                // 그래서 데이터가 다 받아오고 나서는 이 어댑터를 새롭게 refresh 해줘라. 동기화 해줘라!
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ContentsListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }

        myRef.addValueEventListener(postListener)
        rv.adapter = rvAdapter
        rv.layoutManager = GridLayoutManager(this,1)
        getBookmarkData()
    }

    private fun getBookmarkData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                bookmarkIdList.clear()

                for (dataModel in dataSnapshot.children) {
                    bookmarkIdList.add(dataModel.key.toString())
                }
                Log.d("Bookmark : ", bookmarkIdList.toString())
                rvAdapter.notifyDataSetChanged()



            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }

}