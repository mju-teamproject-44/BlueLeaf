package com.example.blueleaf.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.blueleaf.R
import com.example.blueleaf.databinding.FragmentPlantBinding


class PlantFragment : Fragment() {
    private lateinit var binding: FragmentPlantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_plant,container,false)

        binding.homeTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_plantFragment_to_homeFragment)
        }

        binding.informationTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_plantFragment_to_informationFragment)
        }

        binding.boardTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_plantFragment_to_boardFragment)
        }

        binding.bookmarkTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_plantFragment_to_bookmarkFragment)
        }

        return binding.root
    }


}