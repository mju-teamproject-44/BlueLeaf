package com.example.blueleaf

import android.content.Intent
import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blueleaf.databinding.LayoutPlantPhotoItemBinding

class PlantRecyclerViewAdapter(private val dataset: List<Plant>) :RecyclerView.Adapter<PlantRecyclerViewAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutPlantPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
        return dataset.size
    }
    private var originalDataset: List<Plant> = emptyList()

    fun updateData(newData: List<Plant>) {
        originalDataset = newData
        notifyDataSetChanged()
    }




    class ViewHolder(private val binding: LayoutPlantPhotoItemBinding) : RecyclerView.ViewHolder(binding.root),  View.OnClickListener{
        init {
            itemView.setOnClickListener(this) // ViewHolder를 클릭 가능하게 설정
        }

        override fun onClick(v: View?) {
            // 클릭 이벤트 처리
            if (adapterPosition != RecyclerView.NO_POSITION) {
                // 클릭 이벤트 처리 코드를 여기에 추가
                Toast.makeText(itemView.context, "클릭된 아이템: ${adapterPosition}", Toast.LENGTH_SHORT).show()
                val intent = Intent(itemView.context, DetailPlantActivity::class.java)
                itemView.context.startActivity(intent)

            }
        }
        fun bind(data: Plant){
            with(binding){
                koreanName.text=data.name
                EngName.text=data.name_eng
                Glide.with(itemView.context)
                    .load(data.image) // data.image에 이미지 URL이 들어있다고 가정
                    .into(photoimage)

            }
        }
    }

}