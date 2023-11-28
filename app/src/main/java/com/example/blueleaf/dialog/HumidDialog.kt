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
import androidx.core.content.ContextCompat
import com.example.blueleaf.R

class HumidDialog(context: Context, private val parentView: View, private val onFilterByHumid: (String) -> Unit) : Dialog(context) {
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

        val view: View = LayoutInflater.from(context).inflate(R.layout.humid_dialog, null)
        setContentView(view)
        val mainColor = ContextCompat.getColor(context, R.color.MainColor)

        closeButton = view.findViewById(R.id.closeBtn)
        //seekBar = view.findViewById(R.id.wateringSeekBar)
        val drawable = ContextCompat.getDrawable(context, R.drawable.gradient_background)

        closeButton.setOnClickListener {
            if (check.isNotEmpty()) {
                dismiss()
                parentView.alpha = 1f
                parentView.background = drawable
                onFilterByHumid(check)
            } else {
                // 필터링을 하지 않고 다이얼로그를 닫음
                dismiss()
                parentView.alpha = 1f
                parentView.background = drawable
            }
        }


        lowerButton=view.findViewById(R.id.lowButton)
        middleButton=view.findViewById(R.id.mediumButton)
        highButton=view.findViewById(R.id.highButton)

        lowerButton.setOnClickListener{
            check = "1"
            lowerButton.background = ContextCompat.getDrawable(lowerButton.context, R.drawable.button_background_after_gray)
            lowerButton.setTextColor(ContextCompat.getColor(lowerButton.context, R.color.white))
        }
        middleButton.setOnClickListener{
            check="2"
            middleButton.background = ContextCompat.getDrawable(middleButton.context, R.drawable.button_background_after_gray)
            middleButton.setTextColor(ContextCompat.getColor(middleButton.context, R.color.white))
        }
        highButton.setOnClickListener{
            check="3"
            highButton.background = ContextCompat.getDrawable(highButton.context, R.drawable.button_background_after_gray)
            highButton.setTextColor(ContextCompat.getColor(highButton.context, R.color.white))
        }
    }
}