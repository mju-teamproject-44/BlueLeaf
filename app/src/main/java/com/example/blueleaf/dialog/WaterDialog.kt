import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.blueleaf.R

class WaterDialog(context: Context, private val parentView: View, private val onFilterByWater: (Int) -> Unit
) : Dialog(context) {
    private lateinit var closeButton: Button
    private lateinit var seekBar: SeekBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // parentView의 배경을 어둡게 처리

        parentView.setBackgroundColor(Color.parseColor("#6b6e6c"))
        parentView.alpha = 0.4f

        setCanceledOnTouchOutside(false)

        // 다이얼로그의 배경을 투명하게 설정
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setDimAmount(0f)

        val view: View = LayoutInflater.from(context).inflate(R.layout.water_dialog, null)
        setContentView(view)
        val mainColor = ContextCompat.getColor(context, R.color.MainColor)

        closeButton = view.findViewById(R.id.closeBtn)
        seekBar = view.findViewById(R.id.wateringSeekBar)
        val drawable = ContextCompat.getDrawable(context, R.drawable.gradient_background)

        closeButton.setOnClickListener {
            dismiss()
            parentView.alpha = 1f
            parentView.background = drawable
            val selectedValue = seekBar.progress
            onFilterByWater(selectedValue)
        }

    }
}