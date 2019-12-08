package hu.mobilclient.memo.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.DialogFragment
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.databinding.FragmentUsageModeBinding
import hu.mobilclient.memo.helpers.Constants

class UsageModeFragment(val afterSelection: ()->Unit = {}) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val contextThemeWrapper: Context = ContextThemeWrapper(activity, R.style.AppTheme)

        val binding: FragmentUsageModeBinding = DataBindingUtil.inflate(
                inflater.cloneInContext(contextThemeWrapper), R.layout.fragment_usage_mode, container, false)

        binding.setVariable(BR.fragment, this)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        App.usageMode = App.Companion.UsageMode.developer

        return binding.root
    }

    fun onSelectionChanged(group: RadioGroup, checkedId: Int) {
        when(checkedId){
            R.id.fg_usage_mode_rb_developer -> App.usageMode = App.Companion.UsageMode.developer
            R.id.fg_usage_mode_rb_tester -> App.usageMode = App.Companion.UsageMode.tester
        }
    }

    fun nextClick(view: View){
        App.instance.getSharedPreferences(Constants.USAGEMODE_DATA, 0).edit()
                .putString(Constants.USAGEMODE, App.usageMode.toString())
                .apply()
        dialog?.dismiss()
        afterSelection()
    }
}