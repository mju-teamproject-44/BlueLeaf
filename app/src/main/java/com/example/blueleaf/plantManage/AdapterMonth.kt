package com.example.blueleaf.plantManage

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ManageListItemMonthBinding
import java.util.Date

class AdapterMonth:RecyclerView.Adapter<AdapterMonth.MonthView>() {

    val center = Int.MAX_VALUE/2
    private var calendar = Calendar.getInstance()

    inner class MonthView(val binding: ManageListItemMonthBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthView{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.manage_list_item_month, parent, false)
        return MonthView(ManageListItemMonthBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MonthView, position: Int) {
        //캘린더의 time의 현재 날짜로 초기화 한다.
        calendar.time = Date()
        //set을 이용하여 현재 월의 1일로 이동한다.
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        //add를 사용하여 월 단위로 amount만큼 이동한다.
        calendar.add(Calendar.MONTH, position-center)
        //현재의 월 값을 저장한다.
        holder.binding.itemMonthText.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
        val tempMonth = calendar.get(Calendar.MONTH)

        val dayList: MutableList<Date> = MutableList(5*7){Date()}
        for(i in 0..4){
            for(k in 0..6){
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }
        val dayListManager = GridLayoutManager(holder.itemView.context, 7)
        val dayListAdapter = AdapterDay(tempMonth, dayList)

        holder.binding.itemMonthDayList.apply {
            layoutManager = dayListManager
            adapter = dayListAdapter
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}