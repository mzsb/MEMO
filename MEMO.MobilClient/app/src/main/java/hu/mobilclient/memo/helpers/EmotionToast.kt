package hu.mobilclient.memo.helpers

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R

object EmotionToast {

    private var toastCount: Int = Constants.ZERO

    var maxToastCount: Int = 2
    var yOffset: Int = 100

    @SuppressLint("InflateParams")
    private fun show(message: String, drawableId: Int, colorId: Int, duration: Int = Toast.LENGTH_SHORT){
        App.runOnUiThread(Runnable {
            val inflater = LayoutInflater.from(App.instance.applicationContext)
            val layout = inflater.inflate(R.layout.emotion_toast, null)

            val image = layout.findViewById(R.id.image) as ImageView
            image.setImageResource(drawableId)

            val text = layout.findViewById(R.id.text) as TextView
            text.text = message
            text.setTextColor(ContextCompat.getColor(App.instance, colorId))

            toastCount++
            val toast = Toast(App.instance)
            toast.setGravity(Gravity.BOTTOM, Constants.ZERO, 150 + if(toastCount == maxToastCount) { toastCount = Constants.ZERO; yOffset} else Constants.ZERO)
            toast.duration = duration
            toast.view = layout
            toast.show()
        })
    }

    fun showError(message: String?){
        show(message?: App.instance.getString(
                R.string.error_occurred), R.drawable.ic_error_outline_48dp,
                R.color.design_default_color_error)
    }

    fun showSad(message: String?){
        show(message?: App.instance.getString(
                R.string.error_occurred), R.drawable.ic_bad_mood_48dp,
                R.color.design_default_color_error)
    }

    fun showSuccess(message: String? = App.instance.getString(R.string.success_save)){
        show(message?: App.instance.getString(
                R.string.error_occurred), R.drawable.ic_success_24dp,
                R.color.green)
    }

    fun showHelp(message: String?){
        show(message?: App.instance.getString(
                R.string.error_occurred), R.drawable.ic_lightbulb_outline_48dp,
                R.color.accent,
                Toast.LENGTH_LONG)
    }
}