package com.example.blueleaf.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.UserAdapter
import com.example.blueleaf.contentsList.UserModel
import com.example.blueleaf.databinding.FragmentBookmarkBinding
import com.example.blueleaf.utils.FBAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Locale


class BookmarkFragment : Fragment() {

    lateinit var binding: FragmentBookmarkBinding
    lateinit var adapter: UserAdapter
    lateinit var userList: MutableList<UserModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBookmarkBinding.inflate(layoutInflater)
        val database = Firebase.database
        val myRef = database.getReference("users")
        // var userList: MutableList<UserModel>


        userList = mutableListOf()
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear() // 같은 거 계속 찍히는 거 방지 코드. 후에 문제 되면 삭제
                for (dataModel in snapshot.children) {
                    var item = dataModel.getValue(UserModel::class.java)
                    // 비회원 사용자 채팅, 자기 자신과 채팅은 하지 못하게 함.
                    if(item?.uid.equals("비회원 사용자") || item?.uid.equals(FBAuth.getUid())) {
                        continue
                    }
                    userList.add(item!!)
                }

                adapter = UserAdapter(requireContext(), userList)
                binding.userRecyclerView.adapter = adapter
                val layoutManager = LinearLayoutManager(context)
                binding.userRecyclerView.layoutManager = GridLayoutManager(context,2)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)

        binding.userSearchView.clearFocus()
        binding.userSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                filterList(newText.toString())
                return true
            }

        })


        binding.homeTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)
        }

        binding.informationTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_informationFragment)
        }

        binding.boardTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_boardFragment)
        }

        binding.plantTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_plantFragment)
        }

        return binding.root
    }

    private fun filterList(query : String){
        if(query != null) {
            val filteredList = mutableListOf<UserModel>()
            for (i in userList) {
                if (i.userName?.lowercase(Locale.ROOT)!!.contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()){
                //Toast.makeText(this,"wef","wef")
            } else{
                adapter.setFilteredList(filteredList)
            }
        }
    }


}

