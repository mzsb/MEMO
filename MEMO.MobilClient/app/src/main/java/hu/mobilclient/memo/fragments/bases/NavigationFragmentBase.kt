package hu.mobilclient.memo.fragments.bases

import android.os.Bundle
import androidx.fragment.app.Fragment
import hu.mobilclient.memo.activities.NavigationActivity
import hu.mobilclient.memo.fragments.interfaces.IUpdateable
import hu.mobilclient.memo.managers.ServiceManager

abstract class NavigationFragmentBase : Fragment(), IUpdateable {

    protected lateinit var activity: NavigationActivity
    protected lateinit var serviceManager: ServiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as NavigationActivity
        serviceManager = activity.serviceManager
    }

    protected fun ifInitialized(doIfInitialized: ()->Unit){
        if(view != null) {
            doIfInitialized()
        }
    }
}