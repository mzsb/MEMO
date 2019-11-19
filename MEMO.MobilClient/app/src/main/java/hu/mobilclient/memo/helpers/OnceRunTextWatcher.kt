package hu.mobilclient.memo.helpers

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher

abstract class OnceRunTextWatcher : TextWatcher {
    private val interval: Long = 100
    private val handler: Handler = Handler()
    private val running = Runnable { runned = true }
    private var runned = true
        set(runned){
            if(!runned){
                handler.postDelayed(running, interval)
            }
            field = runned
        }

    open val onceAfterTextChanged: ()->Unit = {}

    override fun afterTextChanged(p0: Editable?){
        if(runned) {
            runned = false
            onceAfterTextChanged()
        }
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
}