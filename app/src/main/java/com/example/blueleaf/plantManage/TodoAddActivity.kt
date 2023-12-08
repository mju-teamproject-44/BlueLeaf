package com.example.blueleaf.plantManage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.blueleaf.R
import com.example.blueleaf.databinding.ActivityTodoAddBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

//#Binding
private var mBinding: ActivityTodoAddBinding? = null
private val binding get() = mBinding!!

//#Firebase
lateinit var database: DatabaseReference
lateinit var todoRef: DatabaseReference

//Date
private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

//#ExtraPath
lateinit var key: String
lateinit var todokey: String
lateinit var selectDate_s: String
lateinit var selectDate: Date
private var dday: Int = 0
private var selectedTodo: Int = 0

class TodoAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //#Binding Settings
        mBinding = ActivityTodoAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //#EditText No EnterKey
        binding.todoCycleEditText.setOnKeyListener{view, keycode, event ->
            if(event.action == KeyEvent.ACTION_DOWN
                && keycode == KeyEvent.KEYCODE_ENTER){
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.todoCycleEditText.windowToken, 0)
                true
            }
            false
        }

        //#getExtra
        key = intent.getStringExtra("key").toString()
        selectDate_s = intent.getStringExtra("selectDate").toString() //String 형태


        //#Firebase setting
        database = Firebase.database.reference
        val userUID = Firebase.auth.currentUser?.uid
        todoRef = database.child("plantManage_todo").child(userUID!!).child(key)

        //#Spinner
        var todoData = resources.getStringArray(R.array.todo)
        var todoAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoData)

        binding.todoSpinner.adapter = todoAdapter
        binding.todoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTodo = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }

        //#CancelButton Listener
        binding.todoCancleButton.setOnClickListener {
            val intent = Intent(this, PlantManageActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }

        //#AddButton Listener
        binding.todoAddButton.setOnClickListener {
            val todoCycle :Int = Integer.parseInt(binding.todoCycleEditText.text.toString())

            todokey = todoRef.push().key.toString()
            todoRef.child(todokey)
                .setValue(TodoModel(selectedTodo, selectDate_s, todoCycle))

            //Activity Move
            val intent = Intent(this, PlantManageActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }
    }

}