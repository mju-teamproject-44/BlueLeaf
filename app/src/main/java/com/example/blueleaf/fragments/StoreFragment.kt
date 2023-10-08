package com.example.blueleaf.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.blueleaf.R
import com.example.blueleaf.databinding.FragmentStoreBinding


class StoreFragment : Fragment() {
    private lateinit var binding: FragmentStoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_store,container,false)

        binding.homeTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_storeFragment_to_homeFragment)
        }

        binding.plantTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_storeFragment_to_plantFragment)
        }

        binding.wateringTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_storeFragment_to_wateringFragment)
        }

        binding.bookmarkTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_storeFragment_to_bookmarkFragment)
        }

        return binding.root
    }


}