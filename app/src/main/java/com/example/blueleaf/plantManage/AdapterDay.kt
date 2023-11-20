package com.example.blueleaf.plantManage

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ManageListItemDayBinding
import java.util.Date

class AdapterDay(val tempMonth: Int, val dayList: MutableList<Date>): RecyclerView.Adapter<AdapterDay.DayView>() {
    val ROW = 5

    inner class DayView(val binding: ManageListItemDayBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.manage_list_item_day, parent, false)
        return DayView(ManageListItemDayBinding.bind(view))
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        holder.binding.itemDayLayout.setOnClickListener{
            Toast.makeText(holder.itemView.context, "${dayList[position]}", Toast.LENGTH_SHORT).show()
        }
        holder.binding.itemDayText.text = dayList[position].date.toString()

        holder.binding.itemDayText.setTextColor(when(position % 7 ){
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        })

        if(tempMonth != dayList[position].month){
            holder.binding.itemDayText.alpha = 0.4f
        }
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }
}