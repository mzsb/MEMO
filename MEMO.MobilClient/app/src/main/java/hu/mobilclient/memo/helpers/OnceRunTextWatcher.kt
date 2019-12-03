package hu.mobilclient.memo.helpers

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher

abstract class OnceRunTextWatcher : TextWatcher {
    private val interval: Long = 100
    private val handler: Handler = Handler()
    private var runned = true
        set(runned){
            if(!runned){
                handler.postDelayed({this.runned = true}, interval)
            }
            field = runned
        }

    open val onceAfterTextChanged: (p0: Editable?) -> Unit = {}

    override fun afterTextChanged(p0: Editable?){
        if(runned) {
            runned = false
            onceAfterTextChanged(p0)
        }
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
}