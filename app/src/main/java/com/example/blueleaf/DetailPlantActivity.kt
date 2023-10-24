package com.example.blueleaf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.blueleaf.databinding.ActivityDetailPlantBinding
import com.google.gson.Gson
import java.io.IOException

class DetailPlantActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPlantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPlantBinding.inflate(layoutInflater) // 뷰 바인딩 초기화
        setContentView(binding.root) // 뷰 바인딩을 활용하여 레이아웃 설정
        //setContentView(R.layout.activity_detail_plant)
        val testdata = getJsonData("plant.json")

        val plantData = intent.getSerializableExtra("plantData") as Plant
        binding.DetailkoreanName.text = plantData?.name ?: "기본 이름" // 데이터가 없을 경우 기본값 설정
        binding.DetailEngName.text = plantData?.name_eng ?: "기본 영어 이름"
        binding.Detaildescription.text=plantData?.description ?: "기본설명"
        binding.water.text=plantData?.water ?: "기본 물주기"
        binding.sun.text=plantData?.sunlight ?: "기본 햇빛"
        binding.difficult.text=plantData?.difficulty ?: "기본 난이도"
        binding.humid.text=plantData?.humidity ?: "기본 물주기"

        Glide.with(this)
            .load(plantData?.image) // 이미지 URL 가져오기, plantData?.image는 이미지 URL을 나타내는 것으로 가정
            .into(binding.Detailphotoimage)
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

}