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
import com.example.blueleaf.databinding.FragmentInformationBinding


class InformationFragment : Fragment() {
    private lateinit var binding: FragmentInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_information, container, false)

        binding.airPurifyingPlant.setOnClickListener(){
            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category", "category1")
            startActivity(intent)
        }
        binding.cactus.setOnClickListener(){
            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category", "category2")
            startActivity(intent)
        }

        binding.snakePlant.setOnClickListener(){
            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category","category3")
            startActivity(intent)
        }

        binding.moneyTree.setOnClickListener(){
            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category","category4")
            startActivity(intent)
        }

        binding.flower.setOnClickListener(){
            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category","category5")
            startActivity(intent)
        }

        binding.indoorVegetableGarden.setOnClickListener(){
            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category","category6")
            startActivity(intent)
        }

        binding.homeTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_informationFragment_to_homeFragment)
        }

        binding.boardTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_informationFragment_to_boardFragment)
        }

        binding.bookmarkTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_informationFragment_to_bookmarkFragment)
        }

        binding.plantTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_informationFragment_to_plantFragment)
        }

        return binding.root
    }


}