package com.example.blueleaf.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.UserAdapter
import com.example.blueleaf.contentsList.UserModel
import com.example.blueleaf.databinding.FragmentBookmarkBinding
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class BookmarkFragment : Fragment() {

    lateinit var binding: FragmentBookmarkBinding
    lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBookmarkBinding.inflate(layoutInflater)
        val database = Firebase.database
        val myRef = database.getReference("users")
        var userList: MutableList<UserModel>
        userList = mutableListOf()
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("users", "Call")
                for (dataModel in snapshot.children) {
                    Log.d("users", dataModel.toString())
                    var item = dataModel.getValue(UserModel::class.java)
                    Log.d("users", item.toString())
                    userList.add(item!!)
                }

                Log.d("users", userList.toString())
                adapter = UserAdapter(requireContext(), userList)
                binding.userRecyclerView.adapter = adapter
                val layoutManager = LinearLayoutManager(context)
                binding.userRecyclerView.layoutManager = layoutManager

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


}

