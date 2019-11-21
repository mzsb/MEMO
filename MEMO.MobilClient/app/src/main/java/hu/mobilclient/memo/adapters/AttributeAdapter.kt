package hu.mobilclient.memo.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.databinding.AttributeFilterMenuBinding
import hu.mobilclient.memo.databinding.AttributeListItemBinding
import hu.mobilclient.memo.filters.AttributeFilter
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.managers.ServiceManager
import hu.mobilclient.memo.model.Attribute
import hu.mobilclient.memo.model.Dictionary
import kotlinx.android.synthetic.main.attribute_filter_content.view.*
import kotlinx.android.synthetic.main.attribute_filter_menu.view.*
import kotlinx.android.synthetic.main.attribute_list_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class AttributeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var serviceManager: ServiceManager
    lateinit var userId: UUID

    private val TYPE_FILTER = 0
    private val TYPE_ATTRIBUTE = 1

    private val filterPosition = Constants.ZERO

    private val displayedAttributes = ArrayList<Attribute>()
    private val allAttributes = ArrayList<Attribute>()
    private val ownAttributes = ArrayList<Attribute>()
    private var attributeFilter = AttributeFilter.loadFilter()

    lateinit var itemLongClickListener: OnAttributeClickedListener

    private val filterOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_open)
    private val searchOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.search_open)
    private val filterMenuOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_menu_open)
    private val filterMenuClose: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_menu_close)
    private val filterClose: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_close)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_ATTRIBUTE -> {
                val binding: AttributeListItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.attribute_list_item, parent, false)

                AttributeViewHolder(binding.root, binding)
            }
            TYPE_FILTER ->{
                val binding: AttributeFilterMenuBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.attribute_filter_menu, parent, false)

                FilterViewHolder(binding.root, binding)
            }
            else -> throw IllegalArgumentException(App.instance.getString(R.string.not_existing_list_item_type))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == filterPosition) {
            TYPE_FILTER
        } else TYPE_ATTRIBUTE
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView

        when(holder.itemViewType) {
            TYPE_ATTRIBUTE -> {
                if(holder is AttributeViewHolder) {

                    val attribute = displayedAttributes[position]

                    holder.binding.setVariable(BR.attribute, attribute)

                    val userNameTextView = itemView.fg_account_attribute_list_item_tv_user_name
                    val parametersLinearLayout = itemView.fg_account_attribute_list_item_ll_parameter_holder
                    val parametersHiderButton = itemView.fg_account_attribute_list_item_iv_parameters_hider

                    if(parametersLinearLayout.childCount < attribute.AttributeParameters.size) {
                        for (parameter in attribute.AttributeParameters) {
                            val parameterTextView = TextView(App.instance)
                            parameterTextView.text = parameter.Value
                            parameterTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                            parameterTextView.typeface = ResourcesCompat.getFont(App.instance, R.font.aldrich)
                            parameterTextView.setTextColor(ContextCompat.getColor(App.instance, R.color.primary_light))
                            val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = Gravity.START
                            }
                            parameterTextView.layoutParams = params
                            parametersLinearLayout.addView(parameterTextView)
                        }
                    }

                    if(App.isAdmin()){
                        userNameTextView.paintFlags = userNameTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                        userNameTextView.visibility = View.VISIBLE
                    }
                    else{
                        userNameTextView.visibility = View.GONE
                    }

                    parametersHiderButton.setOnClickListener {
                        holder.parametersVisibility = !holder.parametersVisibility

                        (it as ImageView).setImageResource(if (holder.parametersVisibility) R.drawable.ic_drop_up_24dp else R.drawable.ic_drop_down_24dp)
                        parametersLinearLayout.visibility = if (holder.parametersVisibility) View.VISIBLE else View.GONE
                    }

                    itemView.setOnClickListener {
                        itemLongClickListener.onAttributeLongClicked(attribute)
                    }
                }
            }
            TYPE_FILTER -> {
                if(holder is FilterViewHolder) {

                    holder.binding.setVariable(BR.filter, attributeFilter)

                    attributeFilter.setTypes()
                    attributeFilter.setTotalAttributeCount(displayedAttributes)
                    attributeFilter.setTotalValueCount(displayedAttributes)

                    val filterMenuSearchButton = itemView.fg_account_attribute_filter_menu_iv_search
                    val filterMenuFilterButton = itemView.fg_account_attribute_filter_menu_iv_filter
                    val filterMenuPanel = itemView.fg_account_attribute_filter_menu_cl_menu
                    val filterContentHolder = itemView.fg_account_attribute_filter_menu_cl_content
                    val filterSearchPanel = itemView.fg_account_attribute_filter_content_ll_search
                    val filterFilterPanel = itemView.fg_account_attribute_filter_content_ll_filter
                    val filterContentCloseButton = itemView.fg_account_attribute_filter_content_iv_close
                    val filterContentResetButton = itemView.fg_account_attribute_filter_content_iv_reset
                    val filterTypeSpinner = itemView.fg_account_attribute_filter_content_sp_type
                    val filterAttributeNameEditText = itemView.fg_account_attribute_filter_content_et_attribute_name
                    val filterUserNameEditText = itemView.fg_account_attribute_filter_content_et_user_name
                    val filterMinParameterCountEditText = itemView.fg_account_attribute_filter_content_et_min_parameter_count
                    val filterMaxParameterCountEditText = itemView.fg_account_attribute_filter_content_et_max_parameter_count

                    filterMenuSearchButton.setOnClickListener {
                        it.isEnabled = false
                        filterMenuFilterButton.isEnabled = false
                        filterMenuPanel.startAnimation(filterMenuClose)
                        filterMenuPanel.visibility = View.GONE
                        filterContentHolder.startAnimation(searchOpen)
                        filterContentHolder.visibility = View.VISIBLE
                        filterSearchPanel.visibility = View.VISIBLE
                        filterContentCloseButton.setImageResource(R.drawable.ic_search_24dp)
                        filterContentCloseButton.isEnabled = true

                        filterContentResetButton.setOnClickListener {
                            attributeFilter.resetSearch()
                            useFilter()
                        }
                    }

                    filterMenuFilterButton.setOnClickListener {
                        it.isEnabled = false
                        filterMenuSearchButton.isEnabled = false
                        filterMenuPanel.startAnimation(filterMenuClose)
                        filterMenuPanel.visibility = View.GONE
                        filterContentHolder.startAnimation(filterOpen)
                        filterContentHolder.visibility = View.VISIBLE
                        filterFilterPanel.visibility = View.VISIBLE
                        filterContentCloseButton.setImageResource(R.drawable.ic_filter_24dp)
                        filterContentCloseButton.isEnabled = true

                        filterContentResetButton.setOnClickListener {
                            attributeFilter.resetFilter()
                            filterTypeSpinner.setSelection(attributeFilter.types.indexOf(attributeFilter.Type))
                            useFilter()
                        }
                    }

                    filterContentCloseButton.setOnClickListener {
                        it.isEnabled = false
                        filterContentHolder.visibility = View.GONE
                        filterContentHolder.startAnimation(filterClose)
                        filterMenuPanel.visibility = View.VISIBLE
                        filterMenuPanel.startAnimation(filterMenuOpen)
                        filterSearchPanel.visibility = View.GONE
                        filterFilterPanel.visibility = View.GONE
                        filterMenuSearchButton.isEnabled = true
                        filterMenuFilterButton.isEnabled = true
                    }

                    filterAttributeNameEditText.addTextChangedListener(attributeFilter.textWatcher)
                    filterUserNameEditText.addTextChangedListener(attributeFilter.textWatcher)

                    filterMinParameterCountEditText.addTextChangedListener(attributeFilter.textWatcher)
                    filterMaxParameterCountEditText.addTextChangedListener(attributeFilter.textWatcher)

                    filterTypeSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_accent, attributeFilter.types)

                    attributeFilter.typeOnItemSelectedListener.init = true
                    filterTypeSpinner.setSelection(attributeFilter.types.indexOf(attributeFilter.Type))
                    filterTypeSpinner.onItemSelectedListener = attributeFilter.typeOnItemSelectedListener
                }
            }
        }

    }

    private fun useFilter(){
        initializeAdapter(beforeInit,afterInit)
    }

    override fun getItemCount(): Int = displayedAttributes.size

    private var beforeInit = {}
    private var afterInit = {}

    fun initializeAdapter(beforeInit: ()-> Unit, afterInit: ()->Unit){
        this.beforeInit = beforeInit
        this.afterInit = afterInit
        attributeFilter.useFilter = ::useFilter
        when {
            attributeFilter.All.get() ->
                if(!allAttributes.any()){
                    beforeInit()
                    serviceManager.attribute?.get({allAttributes ->
                        this.allAttributes.addAll(allAttributes)
                        setAttributes(allAttributes)
                        ownAttributes.clear()
                        afterInit()
                    },{
                        EmotionToast.showSad(App.instance.getString(R.string.unable_load_all_attributes))
                    })
                }
                else{
                    setAttributes(allAttributes)
                }
            !attributeFilter.All.get() ->
                if(!ownAttributes.any()){
                    beforeInit()
                    serviceManager.attribute?.getByUserId(userId,{ ownAttributes ->
                        this.ownAttributes.addAll(ownAttributes)
                        setAttributes(ownAttributes)
                        allAttributes.clear()
                        afterInit()
                    },{
                        EmotionToast.showSad(App.instance.getString(R.string.unable_load_own_attributes))
                    })
                }
                else{
                    setAttributes(ownAttributes)
                }
        }
    }

    private fun setAttributes(attributes: List<Attribute>) {
        displayedAttributes.clear()
        displayedAttributes.addAll(0, attributeFilter.filter(attributes))
        displayedAttributes.add(filterPosition, Attribute())
        notifyDataSetChanged()
    }

    fun reset(){
        ownAttributes.clear()
        allAttributes.clear()
    }

    inner class AttributeViewHolder(itemView: View, val binding: AttributeListItemBinding, var parametersVisibility : Boolean = false) : RecyclerView.ViewHolder(itemView)

    inner class FilterViewHolder(itemView: View, val binding: AttributeFilterMenuBinding): RecyclerView.ViewHolder(itemView)

    interface OnAttributeClickedListener {
        fun onAttributeLongClicked(attribute: Attribute)
    }
}