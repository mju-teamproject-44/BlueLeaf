package com.example.blueleaf.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.blueleaf.R
import com.example.blueleaf.databinding.FragmentBookmarkBinding


class BookmarkFragment : Fragment() {
   private lateinit var binding:FragmentBookmarkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bookmark,container,false)

        binding.homeTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)
        }

        binding.plantTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_plantFragment)
        }

        binding.wateringTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_wateringFragment)
        }

        binding.storeTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_storeFragment)
        }

        return binding.root
    }

}