package com.example.blueleaf.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.blueleaf.R

class DifficultDialog(context: Context, private val parentView: View, private val onFilterByDifficult: (String) -> Unit) : Dialog(context) {
    private lateinit var closeButton: Button
    private lateinit var lowerButton: Button
    private lateinit var middleButton: Button
    private lateinit var highButton: Button
    private var check:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // parentView의 배경을 어둡게 처리
        parentView.setBackgroundColor(Color.parseColor("#6b6e6c"))
        parentView.alpha = 0.4f

        setCanceledOnTouchOutside(false)

        // 다이얼로그의 배경을 투명하게 설정
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setDimAmount(0f)

        val view: View = LayoutInflater.from(context).inflate(R.layout.difficult_dialog, null)
        setContentView(view)
        val mainColor = ContextCompat.getColor(context, R.color.MainColor)

        closeButton = view.findViewById(R.id.closeBtn)
        //seekBar = view.findViewById(R.id.wateringSeekBar)

        closeButton.setOnClickListener {
            dismiss()
            parentView.alpha = 1f
            parentView.setBackgroundColor(mainColor)
            onFilterByDifficult(check)
        }

        lowerButton=view.findViewById(R.id.lowButton)
        middleButton=view.findViewById(R.id.mediumButton)
        highButton=view.findViewById(R.id.highButton)

        lowerButton.setOnClickListener{
            check = "초급자"
            lowerButton.background = ContextCompat.getDrawable(lowerButton.context, R.drawable.button_background_after_green)
            lowerButton.setTextColor(ContextCompat.getColor(lowerButton.context, R.color.white))
        }
        middleButton.setOnClickListener{
            check="중급자"
            middleButton.background = ContextCompat.getDrawable(middleButton.context, R.drawable.button_background_after_green)
            middleButton.setTextColor(ContextCompat.getColor(middleButton.context, R.color.white))
        }
        highButton.setOnClickListener{
            check="상급자"
            highButton.background = ContextCompat.getDrawable(highButton.context, R.drawable.button_background_after_green)
            highButton.setTextColor(ContextCompat.getColor(highButton.context, R.color.white))
        }
    }
}