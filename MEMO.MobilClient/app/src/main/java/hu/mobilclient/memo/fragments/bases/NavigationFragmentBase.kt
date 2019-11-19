package hu.mobilclient.memo.fragments.bases

import android.os.Bundle
import androidx.fragment.app.Fragment
import hu.mobilclient.memo.activities.NavigationActivity
import hu.mobilclient.memo.fragments.interfaces.IUpdateable
import hu.mobilclient.memo.helpers.NavigationArguments
import hu.mobilclient.memo.managers.ServiceManager

abstract class NavigationFragmentBase : Fragment(), IUpdateable {

    protected lateinit var serviceManager: ServiceManager
    protected lateinit var  args : NavigationArguments

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceManager = (activity as NavigationActivity).serviceManager
        args = (activity as NavigationActivity).args
    }
}