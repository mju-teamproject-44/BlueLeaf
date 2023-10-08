package com.example.blueleaf.fragments

import android.os.Bundle
import android.provider.ContactsContract.RawContacts.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.BookmarkRVAdapter
import com.example.blueleaf.contentsList.ContentModel
import com.example.blueleaf.databinding.FragmentBookmarkBinding
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class BookmarkFragment : Fragment() {
   private lateinit var binding:FragmentBookmarkBinding
   private val TAG = BookmarkFragment::class.java.simpleName // 북마크 프래그먼트 참조.단순 클래스 이름
    val bookmarkIdList = mutableListOf<String>()
    val items = mutableListOf<ContentModel>()
    val itemKeyList = mutableListOf<String>()
    lateinit var rvAdapter : BookmarkRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bookmark,container,false)


        // 2. 사용자가 북마크한 정보를 다 가져옴
        getBookmarkData()
        rvAdapter = BookmarkRVAdapter(requireContext(), items, itemKeyList, bookmarkIdList)

        val rv : RecyclerView = binding.bookmarkRV
        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.homeTap.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)
        }

        binding.tipTap.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_tipFragment)
        }

        binding.talkTap.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_talkFragment)

        binding.homeTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)
        }

        binding.plantTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_plantFragment)
        }

        binding.wateringTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_wateringFragment)

        }

        binding.storeTap.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_storeFragment)
        }

        return binding.root
    }

    private fun getCategoryData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(ContentModel::class.java)

                    // 3. 전체 컨텐츠 중에서, 사용자가 북마크한 정보만 보여줌!
                    if (bookmarkIdList.contains(dataModel.key.toString())){
                        items.add(item!!)
                        itemKeyList.add(dataModel.key.toString())
                    }


                }
                rvAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.category1.addValueEventListener(postListener)
        FBRef.category2.addValueEventListener(postListener)

    }

    private fun getBookmarkData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    Log.e(TAG, dataModel.toString())
                    bookmarkIdList.add(dataModel.key.toString())

                }

                // 1. 전체 카테고리에 있는 컨텐츠 데이터들을 다 가져옴!
                getCategoryData()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }

}