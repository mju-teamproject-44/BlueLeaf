package com.example.blueleaf.plantManage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ManageListItemTodoBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class AdapterTodo(val todoList: MutableList<TodoModel>, val todoKeyList: MutableList<String>, val key: String) : RecyclerView.Adapter<AdapterTodo.TodoView>() {

    inner class TodoView(val binding: ManageListItemTodoBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.manage_list_item_todo, parent, false)
        return TodoView(ManageListItemTodoBinding.bind(view))
    }

    override fun onBindViewHolder(holder: TodoView, position: Int) {
        val todoType  = todoList[position].todo_code

        //일정명 변경
        allIconGone(holder, position)
        val todoData = holder.itemView.resources.getStringArray(R.array.todo)
        holder.binding.manageTodoTextView.text = todoData[todoType]

        //아이콘 변경
        when(todoType){
            0 ->{
               //Water
                holder.binding.manageRVWater.visibility = ImageView.VISIBLE
            }

            1 -> {
                //Plant
                holder.binding.manageRVPlant.visibility = ImageView.VISIBLE
            }

            2 -> {
                //Fe
                holder.binding.manageRVFe.visibility = ImageView.VISIBLE
            }

            3 -> {
                //Sun
                holder.binding.manageRVSun.visibility = ImageView.VISIBLE
            }
        }
//
//        //Data Update
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val today = Calendar.getInstance()
//
//            val curTargetDate = dateFormat.parse(todoList[position].target_date)
//            var dday = curTargetDate.time- today.time.time
//            //dday가 0 이하인 경우
//            if(dday.toInt() < 0){
//                var cal = Calendar.getInstance()
//                while(dday.toInt() < 0){
//                    cal.time = curTargetDate
//                    cal.add(Calendar.DATE, todoList[position].cycle_date)
//
//                    dday = cal.time.time - today.time.time
//                }
//                val updateTartgetDate : String = dateFormat.format(cal.time)
//                todoRef.child(todoKeyList[position]).setValue(TodoModel(todoList[position].todo_code, updateTartgetDate, todoList[position].cycle_date))
//        }

        //기간 변경
        val selectDate = dateFormat.parse(todoList[position].target_date)
        var calcuDate = (selectDate.time - today.time.time) / (60 * 60 * 24 * 1000)

        val temp1 = calcuDate.toInt().toString()
        val temp2: String = "일 뒤"
        val temp: String = temp1 + temp2
        holder.binding.manageDdayTextView.text = temp


        //x버튼을 눌렀을 때.
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    private fun allIconGone(holder: TodoView, position: Int){
        holder.binding.manageRVWater.visibility = ImageView.GONE
        holder.binding.manageRVPlant.visibility = ImageView.GONE
        holder.binding.manageRVFe.visibility = ImageView.GONE
        holder.binding.manageRVSun.visibility = ImageView.GONE
    }
}