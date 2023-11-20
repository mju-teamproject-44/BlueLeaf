package com.example.blueleaf.plantManage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blueleaf.databinding.ActivityPlantManageBinding
import androidx.recyclerview.widget.PagerSnapHelper

class PlantManageActivity : AppCompatActivity() {

    private var mBinding: ActivityPlantManageBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityPlantManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //가로 전환을 위한 HORIZONTAL 속성
        val monthListManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val monthListAdapter = AdapterMonth()

        binding.plantManageCalendar.apply {
            layoutManager = monthListManager
            adapter = monthListAdapter
            scrollToPosition(Int.MAX_VALUE/2)
        }
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.plantManageCalendar)

        binding.plantManageBackImageView.setOnClickListener {
        }
    }

}