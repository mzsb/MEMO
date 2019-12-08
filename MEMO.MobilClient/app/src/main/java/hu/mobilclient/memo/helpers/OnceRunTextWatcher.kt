package hu.mobilclient.memo.helpers

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import java.util.*

abstract class OnceRunTextWatcher : TextWatcher {
    private val interval: Long = 1000
    private val handler: Handler = Handler()
    private var isLast = true

    open val onceAfterTextChanged: (p0: Editable?) -> Unit = {}

    override fun afterTextChanged(p0: Editable?){
        isLast = true
        handler.postDelayed({
            if (isLast) {
                isLast = false
                onceAfterTextChanged(p0)
            }
        }, interval)
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { isLast = false}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
}