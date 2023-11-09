package com.example.blueleaf.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.BookmarkRVAdapter
import com.example.blueleaf.contentsList.ContentModel
import com.example.blueleaf.databinding.FragmentBookmarkBinding


class BookmarkFragment : Fragment() {

    private lateinit var binding: FragmentBookmarkBinding
    private val TAG = BookmarkFragment::class.java.simpleName // 북마크 프래그먼트 참조.단순 클래스 이름
    val bookmarkIdList = mutableListOf<String>() // 북마크 id 리스트
    val items = mutableListOf<ContentModel>()
    val itemKeyList = mutableListOf<String>()
    lateinit var rvAdapter: BookmarkRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)
        // 2. 사용자가 북마크한 정보를 다 가져옴



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

