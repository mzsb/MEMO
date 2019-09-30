package hu.mobilclient.memo.helpers

import android.app.Activity
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.databinding.ActivityLoginBinding

object EmotionToast {

    var toastCount: Int = 0
    var yOffset: Int = 100

    private fun show(activity: Activity, message: String, drawableId: Int, colorId: Int, duration: Int = Toast.LENGTH_SHORT){
        activity.runOnUiThread {
            val inflater = activity.layoutInflater
            val layout = inflater.inflate(R.layout.emotion_toast, activity.findViewById(R.id.toast_layout_root))

            val image = layout.findViewById(R.id.image) as ImageView
            image.setImageResource(drawableId)

            val text = layout.findViewById(R.id.text) as TextView
            text.text = message
            text.setTextColor(ContextCompat.getColor(activity, colorId))

            toastCount++
            val toast = Toast(activity.applicationContext)
            toast.setGravity(Gravity.BOTTOM, 0, 150 + if(toastCount > 1) { toastCount = 0; yOffset} else 0)
            toast.duration = duration
            toast.view = layout
            toast.show()
        }
    }

    fun showError(activity: Activity, message: String?){
        show(activity, message?: activity.getString(
                R.string.error_occurred),
                R.drawable.ic_error_outline_48dp,
                R.color.design_default_color_error)
    }

    fun showSad(activity: Activity, message: String?){
        show(activity, message?: activity.getString(
                R.string.error_occurred),
                R.drawable.ic_bad_mood_48dp,
                R.color.design_default_color_error)
    }

    fun showHelp(activity: Activity, message: String?){
        show(activity, message?: activity.getString(
                R.string.error_occurred),
                R.drawable.ic_lightbulb_outline_48dp,
                R.color.accent,
                Toast.LENGTH_LONG)
    }
}