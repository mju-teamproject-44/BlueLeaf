package com.example.blueleaf.plantManage

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ManageListItemDayBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AdapterDay(val tempMonth: Int, val dayList: MutableList<Date>, val key: String, val todoList: MutableList<TodoModel>): RecyclerView.Adapter<AdapterDay.DayView>() {
    val ROW = 5

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    inner class DayView(val binding: ManageListItemDayBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.manage_list_item_day, parent, false)
        return DayView(ManageListItemDayBinding.bind(view))
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        val today = dateFormat.parse(getTodayString())

        //각 일자를 눌렀을 때,
        holder.binding.itemDayLayout.setOnClickListener{

            //과거의 날짜를 눌렀을 때에는 리스너가 끝나도록 해야됨.
            val selectDay = dayList[position]
            val calcDay = calcDDay(selectDay, today)
            if(calcDay <  0){
                Toast.makeText(holder.itemView.context, "과거의 날짜는 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //날짜를 눌렀을 때, 일정 추가 페이지로 이동 및 정보 전달(key, 선택한 날짜)
            val intent = Intent(holder.itemView.context, TodoAddActivity::class.java)
            intent.putExtra("key", key)
            intent.putExtra("selectDate", dateFormat.format(dayList[position]))
            holder.itemView.context.startActivity(intent)

        }

        //dayList에 맞춰서 일 텍스트 설정
        holder.binding.itemDayText.text = dayList[position].date.toString()

        //TodoList에서 일자를 확인하고, 각 맞는 아이콘으로 변경한다.
        for(i in todoList){
            val targetdate = dateFormat.parse(i.target_date)
            val cycleDate : Int = i.cycle_date
            val todo_code = i.todo_code

            val tododday = calcDDay(targetdate, dayList[position])


            //주기 세팅
            if((tododday % cycleDate == 0) &&
                (targetdate.time <= dayList[position].time) &&
                (calcDDay(dayList[position], today) < 120)){

                setIcon(todo_code, holder)
            }

            //각 년 월에 맞는지 확인
            if((targetdate.month == dayList[position].month) && (targetdate.year == dayList[position].year)){
                //각 일에만 해당
                if(targetdate.date == dayList[position].date){
                    setIcon(todo_code, holder)
                }
            }
        }

        //일 텍스트 색 설정
        holder.binding.itemDayText.setTextColor(when(position % 7 ){
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        })

        //과거 일자 색 변경
        if(calcDDay(dayList[position], today) < 0){
            holder.binding.itemDayText.setTextColor(Color.RED)
        }

        //해당 월 밖의 일 설정
        if(tempMonth != dayList[position].month){
            holder.binding.itemDayText.alpha = 0.4f
        }
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }

    private fun calcDDay(d1 : Date, d2 : Date): Int {
        val i  = (d1.time - d2.time) / (60 * 60 * 24 * 1000)
        return i.toInt()
    }

    private fun getTodayString(): String{
        val today = Calendar.getInstance()
        return dateFormat.format(today.time)
    }

    private fun setIcon(todo_code: Int, holder: DayView){
        //설정 전 초기화
        holder.binding.itemDayIconWater.visibility = ImageView.GONE
        holder.binding.itemDayIconPlant.visibility = ImageView.GONE
        holder.binding.itemDayIconFe.visibility = ImageView.GONE
        holder.binding.itemDayIconSun.visibility = ImageView.GONE

        //분류에 따라 아이콘 변경
        when(todo_code){
            0 ->{
                //Water
                holder.binding.itemDayIconWater.visibility = ImageView.VISIBLE
            }

            1 -> {
                //Plant
                holder.binding.itemDayIconPlant.visibility = ImageView.VISIBLE
            }

            2 -> {
                //Fe
                holder.binding.itemDayIconFe.visibility = ImageView.VISIBLE
            }

            3 -> {
                //Sun
                holder.binding.itemDayIconSun.visibility = ImageView.VISIBLE
            }
        }
    }
}