package com.example.blueleaf.fragments

//import android.widget.SearchView

import WaterDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.blueleaf.Plant
import com.example.blueleaf.PlantRecyclerViewAdapter
import com.example.blueleaf.databinding.FragmentPlantBinding
import com.example.blueleaf.dialog.DifficultDialog
import com.example.blueleaf.dialog.HumidDialog
import com.example.blueleaf.dialog.TemperDialog
import com.example.blueleaf.plantList
import com.google.gson.Gson
import java.io.IOException
import java.text.Normalizer
import java.util.Locale


class PlantFragment : Fragment() {
    private lateinit var binding: FragmentPlantBinding
    private var originalDataset: List<Plant> = mutableListOf()
    private val adapter: PlantRecyclerViewAdapter by lazy { PlantRecyclerViewAdapter(originalDataset, binding.recyclerview) }
    private val koreanNameMap = HashMap<String, Plant>()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, com.example.blueleaf.R.layout.fragment_plant, container, false)

        binding.homeTab.setOnClickListener() {
            it.findNavController().navigate(com.example.blueleaf.R.id.action_plantFragment_to_homeFragment)
        }

        binding.informationTab.setOnClickListener() {
            it.findNavController().navigate(com.example.blueleaf.R.id.action_plantFragment_to_informationFragment)
        }

        binding.boardTab.setOnClickListener() {
            it.findNavController().navigate(com.example.blueleaf.R.id.action_plantFragment_to_boardFragment)
        }

        binding.bookmarkTab.setOnClickListener() {
            it.findNavController().navigate(com.example.blueleaf.R.id.action_plantFragment_to_bookmarkFragment)
        }
        originalDataset = getJsonData("plant.json")?.plants ?: emptyList()

        val testdata = getJsonData("plant.json")
        binding.recyclerview.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = this@PlantFragment.adapter
            this@PlantFragment.adapter.updateData(originalDataset)  // 이 부분을 apply 블록 내로 옮깁니다.

        }

        //val testdata = getJsonData("plant.json")
        testdata?.plants?.forEach { plant ->
            val normalizedKoreanName = Normalizer.normalize(plant.name, Normalizer.Form.NFD)
                .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            koreanNameMap[normalizedKoreanName] = plant
        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("PlantFragment", "Query textChange: $query")
                // 검색 버튼을 눌렀을 때 수행할 동작을 구현
                filterList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("PlantFragment", "Query textChange: $newText")
                // 검색어가 변경될 때 수행할 동작을 구현
                filterList(newText)
                return true
            }
        })

        binding.difficultyButton.setOnClickListener {
            val difficultDialog = DifficultDialog(requireContext(), binding.root){ selectedValue ->
                sortByDifficulty(selectedValue)
            }
            difficultDialog.show()
            updateButtonStyle(binding.difficultyButton)
        }

        binding.temperatureButton.setOnClickListener {
            val temperDialog = TemperDialog(requireContext(), binding.root){selectedLeft, selectedRight ->
                sortByTemperature(selectedLeft, selectedRight)
            }
            temperDialog.show()
            updateButtonStyle(binding.temperatureButton)
        }

        binding.humidityButton.setOnClickListener {
            val humidDialog = HumidDialog(requireContext(), binding.root){ selectedValue ->
                sortByHumidity(selectedValue)
            }
            humidDialog.show()
            updateButtonStyle(binding.humidityButton)
        }

        binding.waterButton.setOnClickListener {
            val waterDialog = WaterDialog(requireContext(), binding.root) { selectedValue ->
                sortByWater(selectedValue)
            }
            waterDialog.show()
            updateButtonStyle(binding.waterButton)
        }

        binding.nameButton.setOnClickListener {
            sortByName()
            //updateButtonStyle(binding.nameButton)
        }
        val view: View = LayoutInflater.from(context).inflate(com.example.blueleaf.R.layout.fragment_plant, null)

        val scrollToTopButton: ImageButton = binding.scrollToTopButton
        val nestedScrollView: NestedScrollView = binding.nestedScrollView

        scrollToTopButton.setOnClickListener { nestedScrollView.scrollTo(0, 0) }



        return binding.root
    }

    private fun sortByDifficulty(difficulty: String) {
        // 난이도에 따라 리사이클러뷰 정렬 로직 추가
        Log.d("PlantFragment", "Original dataset before sorting: ${originalDataset.map { it.name }}")

        /**originalDataset = originalDataset.sortedByDescending {
            val difficultyValue = it.difficulty.toIntOrNull() ?: Int.MIN_VALUE
            Log.d("PlantFragment", "Plant ${it.name}, Difficulty: $difficultyValue")
            difficultyValue
        }

        Log.d("PlantFragment", "Sorted dataset by difficulty: ${originalDataset.map { it.name }}")
        adapter.updateData(originalDataset)**/
        val filteredDataset = originalDataset.filter { plant ->
            plant.difficulty == difficulty
        }

        adapter.updateData(filteredDataset)
    }


    private fun sortByTemperature(left:Int, right:Int) {
        // 온도에 따라 리사이클러뷰 정렬 로직 추가
        val filteredDataset = originalDataset.filter { plant ->
            val temperMin = plant.temper_min.toIntOrNull() ?: Int.MIN_VALUE
            val temperMax = plant.temper_max.toIntOrNull() ?: Int.MAX_VALUE
            temperMin >= left && temperMax <= right
        }

        adapter.updateData(filteredDataset)
    }

    private fun sortByHumidity(humid:String) {
        // 습도에 따라 리사이클러뷰 정렬 로직 추가
        val filteredDataset = originalDataset.filter { plant ->
            plant.dialog_humid == humid
        }

        adapter.updateData(filteredDataset)
    }

    private fun sortByWater(value: Int) {
        Log.d("PlantFragment", "Original dataset before filtering: ${originalDataset.map { it.name }}")

        val filteredDataset = originalDataset.filter { plant ->
            val dialogWaterValue = plant.dialog_water.toIntOrNull() ?: -1
            dialogWaterValue == value
        }

        Log.d("PlantFragment", "Filtered dataset by water: ${filteredDataset.map { it.name }}")
        adapter.updateData(filteredDataset)
    }

    private fun sortByName() {
        // 이름 정렬은 의미 없는거 같아서 그냥 원래 순서로 돌아가는 것으로 변경
        val originalOrderDataset = getJsonData("plant.json")?.plants ?: emptyList()
        adapter.updateData(originalOrderDataset)
    }
    private var lastClickedButton: TextView? = null

    private fun updateButtonStyle(clickedButton: TextView) {
        // 모든 버튼의 스타일 초기화
        val allButtons = listOf(
            binding.difficultyButton,
            binding.temperatureButton,
            binding.humidityButton,
            binding.waterButton
        )

        // nameButton이 클릭되었을 경우
        if (clickedButton == binding.nameButton) {
            allButtons.forEach { button ->
                button.background = ContextCompat.getDrawable(requireContext(), com.example.blueleaf.R.drawable.button_background)
                button.setTextColor(ContextCompat.getColor(requireContext(), com.example.blueleaf.R.color.white))
            }

            // 마지막으로 클릭된 버튼 업데이트
            lastClickedButton = null // 이전에 클릭된 버튼을 null로 설정하여 초기화
        } else {
            // 이전에 클릭된 버튼을 초기 상태로 되돌림
            lastClickedButton?.let {
                it.background = ContextCompat.getDrawable(requireContext(), com.example.blueleaf.R.drawable.button_background)
                it.setTextColor(ContextCompat.getColor(requireContext(), com.example.blueleaf.R.color.white))
            }

            // 클릭된 버튼의 스타일 변경
            clickedButton.background = ContextCompat.getDrawable(requireContext(), com.example.blueleaf.R.drawable.button_background_after)
            clickedButton.setTextColor(ContextCompat.getColor(requireContext(), com.example.blueleaf.R.color.black))

            // 마지막으로 클릭된 버튼 업데이트
            lastClickedButton = clickedButton
        }

    }


    private fun filterList(charText: String?) {
        Log.d("PlantFragment", "filterList: charText=$charText")
        if (charText != null) {
            val normalizedCharText = normalizeString(charText)

            if (charText.isEmpty()) {
                // 검색어가 비어 있으면 전체 데이터를 유지
                adapter.updateData(originalDataset)
            } else {
                // 검색어가 비어 있지 않으면 일치하는 항목을 찾아 추가
                val filteredPlants = mutableListOf<Plant>()
                for ((key, plant) in koreanNameMap) {
                    val normalizedKoreanName = normalizeString(plant.name)

                    if (normalizedKoreanName.equals(normalizedCharText)) {
                        filteredPlants.add(plant)
                    }
                }
                adapter.updateData(filteredPlants)
            }
        }
    }


    private fun normalizeString(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .toLowerCase(Locale.getDefault())
            .trim()

        Log.d("PlantFragment", "normalizeString: input=$input, normalized=$normalized")
        return normalized
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

}