package com.example.blueleaf.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.blueleaf.R
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlin.math.roundToInt


class TemperDialog(context: Context, private val parentView: View, private val onFilterByTemper: (Int, Int) -> Unit) : Dialog(context) {
    private lateinit var closeButton: Button
    private lateinit var rangeSeekBar: RangeSeekBar
    private var previousLeftValue: Int = 0
    private var previousRightValue: Int = 50
    private var ignoreRangeChanged = false
    private var roundedLeftValue: Int=0
    private var roundedRightValue: Int=0





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // parentView의 배경을 어둡게 처리
        parentView.setBackgroundColor(Color.parseColor("#6b6e6c"))
        parentView.alpha = 0.4f

        setCanceledOnTouchOutside(false)

        // 다이얼로그의 배경을 투명하게 설정
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setDimAmount(0f)

        val view: View = LayoutInflater.from(context).inflate(R.layout.temper_dialog, null)
        setContentView(view)
        val mainColor = ContextCompat.getColor(context, R.color.MainColor)


        rangeSeekBar = view.findViewById(R.id.temperSeekBar)
        rangeSeekBar.setRange(0F, 50F) // 범위 설정
        rangeSeekBar.setProgress(0F, 50F) // 초기 값 설정
        rangeSeekBar.leftSeekBar.setIndicatorText("0º")
        rangeSeekBar.rightSeekBar.setIndicatorText("50º")
        // 배경으로 사용할 Drawable 파일 가져오기



        rangeSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar?,
                                        leftValue: Float,
                                        rightValue: Float,
                                        isFromUser: Boolean
            ) {

                Log.d("tag", "Value: $leftValue")
                if (!ignoreRangeChanged) {
                    roundedLeftValue = (leftValue / 5).roundToInt() * 5
                    roundedRightValue = (rightValue / 5).roundToInt() * 5

                    if (roundedLeftValue != previousLeftValue || roundedRightValue != previousRightValue) {
                        ignoreRangeChanged = true
                        rangeSeekBar.setProgress(
                            roundedLeftValue.toFloat(),
                            roundedRightValue.toFloat()
                        )
                        ignoreRangeChanged = false
                    }

                    rangeSeekBar.leftSeekBar.setIndicatorText("${roundedLeftValue}º")
                    rangeSeekBar.rightSeekBar.setIndicatorText("${roundedRightValue}º")

                    previousLeftValue = roundedLeftValue
                    previousRightValue = roundedRightValue

                }


            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                // RangeSeekBar 핸들을 터치하여 트래킹을 시작할 때 호출되는 콜백 메소드
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                // RangeSeekBar 트래킹을 멈출 때 호출되는 콜백 메소드
            }
        })



        closeButton = view.findViewById(R.id.closeBtn)
        //seekBar = view.findViewById(R.id.wateringSeekBar)

        val drawable = ContextCompat.getDrawable(context, R.drawable.gradient_background)

        closeButton.setOnClickListener {
            dismiss()
            parentView.alpha = 1f
            parentView.background = drawable
            onFilterByTemper(roundedLeftValue, roundedRightValue)
        }
    }
}