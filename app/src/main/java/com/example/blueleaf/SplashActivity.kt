package com.example.blueleaf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({

            // 일정 시간이 지나면 MainActivity로 이동
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)

            // 이전 키를 눌렀을 때 splash 화면으로 이동을 방지하기 위해
            // 이동한 다음 사용안함으로 finish 처리
            finish()


        },3000) // 시간 3초 이후 실행

        // 나와 함께하는 식물 -> '함께' 부분 색 따로 지정
        val splashText = findViewById<TextView>(R.id.splash_text)
        val ssb = SpannableStringBuilder(splashText.text)

        ssb.apply{
            setSpan(ForegroundColorSpan(getColor(R.color.lightGreen)), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        }

        splashText.text = ssb
    }
}