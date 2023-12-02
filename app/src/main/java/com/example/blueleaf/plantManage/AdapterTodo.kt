package com.example.blueleaf.plantManage

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.blueleaf.MainActivity
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ManageListItemTodoBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar

class AdapterTodo(val todoList: MutableList<TodoModel>, val todoKeyList: MutableList<String>, val key: String) : RecyclerView.Adapter<AdapterTodo.TodoView>() {

    lateinit var database: DatabaseReference
    lateinit var todoRef: DatabaseReference

    inner class TodoView(val binding: ManageListItemTodoBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.manage_list_item_todo, parent, false)
        return TodoView(ManageListItemTodoBinding.bind(view))
    }

    override fun onBindViewHolder(holder: TodoView, position: Int) {
        val todoType  = todoList[position].todo_code

        //Firebase
        database = Firebase.database.reference
        val userUID = Firebase.auth.currentUser?.uid
        todoRef = database.child("plantManage_todo").child(userUID!!).child(key)

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

        //기간 변경
        val selectDate = dateFormat.parse(todoList[position].target_date)
        var calcuDate = (selectDate.time - today.time.time) / (60 * 60 * 24 * 1000)

        val temp1 = calcuDate.toInt().toString()
        val temp2: String = "일 뒤"
        val temp: String = temp1 + temp2
        holder.binding.manageDdayTextView.text = temp


        //x버튼을 눌렀을 때.
        holder.binding.manageTodoXButton.setOnClickListener {
            todoRef.child(todoKeyList[position]).removeValue()
            Toast.makeText(holder.itemView.context,"일정 삭제 완료", Toast.LENGTH_SHORT).show()

            //화면 ui업데이트 안되는 문제 있음. 이전페이지로 이동
            val intent = Intent(holder.itemView.context, MainActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
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