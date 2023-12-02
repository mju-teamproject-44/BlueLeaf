package com.example.blueleaf.plantManage

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ManageListItemDayBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AdapterDay(val tempMonth: Int, val dayList: MutableList<Date>, val key: String, val todoList: MutableList<TodoModel>): RecyclerView.Adapter<AdapterDay.DayView>() {
    val ROW = 5

    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    inner class DayView(val binding: ManageListItemDayBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.manage_list_item_day, parent, false)
        return DayView(ManageListItemDayBinding.bind(view))
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        //각 일자를 눌렀을 때,
        holder.binding.itemDayLayout.setOnClickListener{

            //과거의 날짜를 눌렀을 때에는 리스너가 끝나도록 해야됨.
            val selectDay = dayList[position]
            val today = Calendar.getInstance()
            val calcDay = selectDay.time - today.time.time
            if(calcDay <  0){
                Toast.makeText(holder.itemView.context, "과거의 날짜는 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //날짜를 눌렀을 때, 일정 추가 페이지로 이동 및 정보 전달(key, 선택한 날짜)
            val intent = Intent(holder.itemView.context, TodoAddActivity::class.java)
            intent.putExtra("key", key)
            intent.putExtra("selectDate", format.format(dayList[position]))
            holder.itemView.context.startActivity(intent)

        }

        //dayList에 맞춰서 일 텍스트 설정
        holder.binding.itemDayText.text = dayList[position].date.toString()

        //TodoList에서 일자를 확인하고, 각 맞는 아이콘으로 변경한다.
        for(i in todoList){
            val targetdate = format.parse(i.target_date)

            //각 년 월에 맞는지 확인
            if((targetdate.month == dayList[position].month) && (targetdate.year == dayList[position].year)){
                //각 일에만 해당
                if(targetdate.date == dayList[position].date){

                    //설정 전 초기화
                    holder.binding.itemDayIconWater.visibility = ImageView.GONE
                    holder.binding.itemDayIconPlant.visibility = ImageView.GONE
                    holder.binding.itemDayIconFe.visibility = ImageView.GONE
                    holder.binding.itemDayIconSun.visibility = ImageView.GONE

                    //분류에 따라 아이콘 변경
                    when(i.todo_code){
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
        }

        //일 텍스트 색 설정
        holder.binding.itemDayText.setTextColor(when(position % 7 ){
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        })

        //해당 월 밖의 일 설정
        if(tempMonth != dayList[position].month){
            holder.binding.itemDayText.alpha = 0.4f
        }
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }


}