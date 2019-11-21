package hu.mobilclient.memo.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.DialogFragment
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.FragmentNetworkSettingsBinding
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.network.ApiService
import kotlinx.android.synthetic.main.fragment_network_settings.view.*


class NetworkSettingFragment : DialogFragment() {

    private val settings: NetworkSettings = NetworkSettings()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val contextThemeWrapper: Context = ContextThemeWrapper(activity, R.style.AppTheme)

        val binding: FragmentNetworkSettingsBinding = DataBindingUtil.inflate(
                inflater.cloneInContext(contextThemeWrapper), R.layout.fragment_network_settings, container, false)

        binding.setVariable(BR.settings,settings)
        binding.setVariable(BR.fragment,this)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    fun cancelClick(view: View) = dismiss()

    fun saveClick(view: View){
        (activity as NetworkActivityBase).serviceManager.connection?.setConnectionData(ip = settings.Ip.get(), port = settings.Port.get())

        EmotionToast.showSuccess()
        dismiss()
    }

    fun resetClick(view: View){
        settings.Ip.set(ApiService.DEFAULTSERVERIP)
        settings.Port.set(ApiService.DEFAULTSERVERPORT)
    }

    inner class NetworkSettings(var Ip: ObservableField<String> = ObservableField(App.instance.getSharedPreferences(Constants.NETWORKDATA, 0)
                                                                                                    .getString(Constants.IP, ApiService.DEFAULTSERVERIP)
                                                                                                    ?: ApiService.DEFAULTSERVERIP),
                                var Port: ObservableField<String> = ObservableField(App.instance.getSharedPreferences(Constants.NETWORKDATA, 0)
                                                                                                      .getString(Constants.PORT, ApiService.DEFAULTSERVERPORT)
                                                                                                      ?: ApiService.DEFAULTSERVERPORT))

}