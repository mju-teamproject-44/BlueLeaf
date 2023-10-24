package com.example.blueleaf.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.blueleaf.Plant
import com.example.blueleaf.PlantRecyclerViewAdapter
import com.example.blueleaf.R
import com.example.blueleaf.databinding.FragmentPlantBinding
import com.example.blueleaf.plantList
import com.google.gson.Gson
import java.io.IOException


class PlantFragment : Fragment() {
    private lateinit var binding: FragmentPlantBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_plant, container, false)

        binding.homeTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_plantFragment_to_homeFragment)
        }

        binding.informationTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_plantFragment_to_informationFragment)
        }

        binding.boardTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_plantFragment_to_boardFragment)
        }

        binding.bookmarkTab.setOnClickListener() {
            it.findNavController().navigate(R.id.action_plantFragment_to_bookmarkFragment)
        }

        val testdata = getJsonData("plant.json")
        binding.recyclerview.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = PlantRecyclerViewAdapter(testdata?.plants ?: emptyList())
        }

        return binding.root
    }

    private fun getJsonData(filename: String): plantList? {
        val assetManager = resources.assets
        var result: plantList? = null
        try {
            val inputStream = assetManager.open(filename)
            val reader = inputStream.bufferedReader()
            val gson = Gson()

            result = gson.fromJson(reader, plantList::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    // 나머지 메서드들...
}
