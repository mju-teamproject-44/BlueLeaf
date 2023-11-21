package com.example.blueleaf

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blueleaf.databinding.LayoutPlantPhotoItemBinding

class PlantRecyclerViewAdapter(
    private var dataset: List<Plant>,
    private val recyclerView: RecyclerView // RecyclerView 변수 추가
) : RecyclerView.Adapter<PlantRecyclerViewAdapter.ViewHolder>() {
    private var originalDataset: List<Plant> = dataset.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("PlantRecyclerViewAdapter", "onCreateViewHolder called")

        val binding =
            LayoutPlantPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("PlantRecyclerViewAdapter", "onBindViewHolder called for position: $position")

        holder.bind(dataset[position])

        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                val intent = Intent(holder.itemView.context, DetailPlantActivity::class.java)
                val plantData = dataset[holder.adapterPosition]
                intent.putExtra("plantData", plantData)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("PlantRecyclerViewAdapter", "getItemCount called. Dataset size: ${dataset.size}")

        return dataset.size
    }

    fun updateData(newData: List<Plant>) {
       /** dataset = newData.toMutableList()
        originalDataset = newData.toList()
        notifyDataSetChanged()
        Log.d("PlantRecyclerViewAdapter", "Data updated. New dataset size: ${dataset.size}")
        //Log.d("PlantRecyclerViewAdapter", "Layout invalidated: ${recyclerView.isLayoutRequested}")
        recyclerView.layoutManager?.let {
            it.requestLayout()
            Log.d("PlantRecyclerViewAdapter", "Layout invalidated: ${recyclerView.isLayoutRequested}")
        }
        recyclerView.invalidate()  // 레이아웃 강제 재그리기**/
        dataset = newData
        notifyDataSetChanged()
        Log.d("PlantRecyclerViewAdapter", "Data updated. New dataset size: ${dataset.size}")
        Log.d("PlantRecyclerViewAdapter", "New dataset: ${dataset.map { it.name }}")  // 새로운 로그 추가
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 2)  // 레이아웃 매니저 재설정

        recyclerView.invalidate()  // 레이아웃 강제 재그리기


    }

    class ViewHolder(private val binding: LayoutPlantPhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Toast.makeText(
                    itemView.context,
                    "클릭된 아이템: ${adapterPosition}",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(itemView.context, DetailPlantActivity::class.java)
                itemView.context.startActivity(intent)
            }
        }

        fun bind(data: Plant) {
            with(binding) {
                koreanName.text = data.name
                EngName.text = data.name_eng
                Glide.with(itemView.context)
                    .load(data.image)
                    .into(photoimage)
            }
        }
    }
}
