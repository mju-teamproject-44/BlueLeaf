package com.example.blueleaf.chat

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.blueleaf.R
import com.example.blueleaf.databinding.AnonyBlockDialogBinding


class Anony_dialog:AppCompatActivity() {

    private lateinit var binding: AnonyBlockDialogBinding
    private lateinit var back_btn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AnonyBlockDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        back_btn = findViewById(R.id.back_btn)
        // 창 닫기
        back_btn.setOnClickListener{
            finish()
        }


    }


}
