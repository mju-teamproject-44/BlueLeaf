package com.example.blueleaf.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.SearchView
import androidx.appcompat.widget.SearchView

import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.blueleaf.Plant
import com.example.blueleaf.PlantRecyclerViewAdapter
import com.example.blueleaf.R
import com.example.blueleaf.databinding.FragmentPlantBinding
import com.example.blueleaf.plantList
import com.example.blueleaf.utils.GridSpacingItemDecoration
import com.google.gson.Gson
import java.io.IOException
import java.text.Normalizer
import java.util.Locale


class PlantFragment : Fragment() {
    private lateinit var binding: FragmentPlantBinding
    private var originalDataset: List<Plant> = mutableListOf()
    private val adapter: PlantRecyclerViewAdapter by lazy { PlantRecyclerViewAdapter(originalDataset) }
    private val koreanNameMap = HashMap<String, Plant>()



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
           // addItemDecoration(GridSpacingItemDecoration(2, 30, true))
        }
        //val testdata = getJsonData("plant.json")
        testdata?.plants?.forEach { plant ->
            val normalizedKoreanName = Normalizer.normalize(plant.name, Normalizer.Form.NFD)
                .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            koreanNameMap[normalizedKoreanName] = plant
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색 버튼을 눌렀을 때 수행할 동작을 구현
                if (query != null) {
                    Log.d("PlantFragment", "Query submitted: $query")
                    filterList(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색어가 변경될 때 수행할 동작을 구현
                if (newText != null) {
                    Log.d("PlantFragment", "Query textChange: $newText")
                    filterList(newText)
                }
                return true
            }
        })

        return binding.root
    }
    private fun filterList(charText: String) {
        val normalizedCharText = charText.toLowerCase(Locale.KOREAN)


        if (charText.isEmpty()) {
            // 검색어가 비어 있으면 전체 데이터를 유지
            adapter.updateData(originalDataset)
        } else {
            // 검색어가 비어 있지 않으면 일치하는 항목을 찾아 추가
            val filteredPlants = mutableListOf<Plant>()
            for ((key, plant) in koreanNameMap) {
                val normalizedKoreanName = Normalizer.normalize(plant.name, Normalizer.Form.NFD)
                    .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                    .toLowerCase(Locale.getDefault())
                    .trim()
                Log.d("PlantFragment", "Normalized Char Text: $normalizedCharText")
                Log.d("PlantFragment", "Normalized Korean Name: $normalizedKoreanName")

                // 추가로, 문자열 비교 결과를 로그로 출력
                val stringsEqual = normalizedKoreanName == normalizedCharText
                Log.d("PlantFragment", "Strings equal: $stringsEqual")

                if (normalizedKoreanName.equals(normalizedCharText, ignoreCase = true)) {
                    Log.d("PlantFragment", "if문 안에 진입")
                    filteredPlants.add(plant)
                }
            }
            Log.d("PlantFragment", "Filtered Plants: $filteredPlants")
            adapter.updateData(filteredPlants)
        }
    }




    private fun getJsonData(filename: String): plantList? {
        val assetManager = resources.assets
        var result: plantList? = null
        try {
            val inputStream = assetManager.open(filename)
            val reader = inputStream.bufferedReader()
            val gson = Gson()

            result = gson.fromJson(reader, plantList::class.java)

            // 이 부분을 제거합니다.
            // originalDataset = result?.plants ?: emptyList()

            // 한글 이름을 키로 해시맵에 추가
            koreanNameMap.clear() // 기존 데이터를 지우고 다시 추가합니다.
            result?.plants?.forEach { plant ->
                val normalizedKoreanName = Normalizer.normalize(plant.name, Normalizer.Form.NFD)
                    .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                koreanNameMap[normalizedKoreanName] = plant
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    // 나머지 메서드들...
}
