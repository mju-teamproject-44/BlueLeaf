package com.example.blueleaf.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.ContentListActivity
import com.example.blueleaf.databinding.FragmentHomeBinding
import com.example.blueleaf.databinding.FragmentTipBinding


class TipFragment : Fragment() {
    private lateinit var binding: FragmentTipBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tip, container, false)

        binding.category1.setOnClickListener(){
            val intent = Intent(context,ContentListActivity::class.java)
            startActivity(intent)
        }
        binding.homeTap.setOnClickListener(){
            it.findNavController().navigate(R.id.action_tipFragment_to_homeFragment)
        }

        binding.talkTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_tipFragment_to_talkFragment)
        }

        binding.bookmarkTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_tipFragment_to_bookmarkFragment)
        }

        binding.storeTag.setOnClickListener(){
            it.findNavController().navigate(R.id.action_tipFragment_to_storeFragment)
        }

        return binding.root
    }


}