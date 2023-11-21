package com.example.blueleaf.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.blueleaf.R
import com.example.blueleaf.board.BoardInformationFragment
import com.example.blueleaf.board.BoardInsideActivity
import com.example.blueleaf.board.BoardListLVAdapter
import com.example.blueleaf.board.BoardModel
import com.example.blueleaf.board.BoardSearchActivity
import com.example.blueleaf.board.BoardShowFragment
import com.example.blueleaf.board.BoardTransFragment
import com.example.blueleaf.board.BoardWriteActivity
import com.example.blueleaf.board.ViewPager2Adapter
import com.example.blueleaf.databinding.FragmentBoardBinding
import com.example.blueleaf.utils.FBRef
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
// pr test
class BoardFragment : Fragment() {
    private lateinit var binding: FragmentBoardBinding



    private val TAG = BoardFragment::class.java.simpleName


    // tab 이용 관련


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board, container, false)

        initViewPager()

        binding.searchBtn.setOnClickListener(){
            val intent = Intent(context, BoardSearchActivity::class.java)
            startActivity(intent)
        }

        binding.writeBtn.setOnClickListener() {
            val intent = Intent(context, BoardWriteActivity::class.java)
            startActivity(intent)
        }

        binding.homeTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_boardFragment_to_homeFragment)
        }

        binding.informationTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_boardFragment_to_informationFragment)
        }

        binding.bookmarkTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_boardFragment_to_bookmarkFragment)
        }

        binding.plantTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_boardFragment_to_plantFragment)
        }



        return binding.root
    }

    private fun initViewPager() {
        // ViewPager2 Adapter 셋팅
        var viewPager2Adapter = ViewPager2Adapter(context as FragmentActivity)
        viewPager2Adapter.addFragment(BoardInformationFragment())
        viewPager2Adapter.addFragment(BoardShowFragment())
        viewPager2Adapter.addFragment(BoardTransFragment())

        // Adapter 연결
        binding.viewPager2.apply {
            adapter = viewPager2Adapter

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }
            })
        }

        // ViewPager2, TabLayout 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "정보 게시판"
                1 -> tab.text = "식물 자랑 게시판"
                2 -> tab.text = "거래 게시판"
            }
        }.attach()
    }




}