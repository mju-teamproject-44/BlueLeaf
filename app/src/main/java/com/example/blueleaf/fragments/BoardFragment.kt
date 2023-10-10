package com.example.blueleaf.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.blueleaf.R
import com.example.blueleaf.databinding.FragmentBoardBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class BoardFragment : Fragment() {
    private lateinit var binding:FragmentBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_board,container,false)

        binding.homeTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_boardFragment_to_homeFragment)
        }

        binding.plantTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_boardFragment_to_plantFragment)
        }

        binding.bookmarkTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_boardFragment_to_bookmarkFragment)
        }

        binding.storeTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_boardFragment_to_storeFragment)
        }

        return binding.root
    }


}