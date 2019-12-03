package hu.mobilclient.memo.helpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR

class BindableArrayAdapter<T, B : ViewDataBinding>(context: Context, @LayoutRes private val layoutResource: Int, private val dictionaries: List<T>):
        ArrayAdapter<T>(context, layoutResource, dictionaries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: B = DataBindingUtil.inflate(
                LayoutInflater.from(context), layoutResource, parent, false)

        binding.setVariable(BR.dictionary,dictionaries[position])

        return binding.root
    }
}