package hu.mobilclient.memo.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.databinding.UserFilterMenuBinding
import hu.mobilclient.memo.databinding.UserListItemBinding
import hu.mobilclient.memo.filters.UserFilter
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.managers.ServiceManager
import hu.mobilclient.memo.model.memoapi.User
import kotlinx.android.synthetic.main.user_filter_content.view.*
import kotlinx.android.synthetic.main.user_filter_menu.view.*
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var serviceManager: ServiceManager
    lateinit var userId: UUID

    private val TYPE_FILTER = 0
    private val TYPE_USER = 1

    private val filterPosition = Constants.ZERO

    private val displayedUsers = ArrayList<User>()
    private val allUsers = ArrayList<User>()
    private val ownUsers = ArrayList<User>()
    private var userFilter = UserFilter.loadFilter()

    lateinit var itemLongClickListener: OnUserClickedListener

    private val filterOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_open)
    private val searchOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.search_open)
    private val filterMenuOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_menu_open)
    private val filterMenuClose: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_menu_close)
    private val filterClose: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_close)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_USER -> {
                val binding: UserListItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.user_list_item, parent, false)

                UserViewHolder(binding.root, binding)
            }
            TYPE_FILTER ->{
                val binding: UserFilterMenuBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.user_filter_menu, parent, false)

                FilterViewHolder(binding.root, binding)
            }
            else -> throw IllegalArgumentException(App.instance.getString(R.string.not_existing_list_item_type))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == filterPosition) {
            TYPE_FILTER
        } else TYPE_USER
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView

        when(holder.itemViewType) {
            TYPE_USER -> {
                if(holder is UserViewHolder) {

                    val user = displayedUsers[position]

                    holder.binding.setVariable(BR.user, user)

                    itemView.setOnLongClickListener {
                        itemLongClickListener.onUserLongClicked(user)
                    }
                }
            }
            TYPE_FILTER -> {
                if(holder is FilterViewHolder) {

                    holder.binding.setVariable(BR.filter, userFilter)

                    userFilter.setTotalUserCount(displayedUsers)
                    userFilter.setTotalDictionaryCount(displayedUsers)

                    val filterMenuSearchButton = itemView.fg_account_user_filter_menu_iv_search
                    val filterMenuFilterButton = itemView.fg_account_user_filter_menu_iv_filter
                    val filterMenuPanel = itemView.fg_account_user_filter_menu_cl_menu
                    val filterContentHolder = itemView.fg_account_user_filter_menu_cl_content
                    val filterSearchPanel = itemView.fg_account_user_filter_content_ll_search
                    val filterFilterPanel = itemView.fg_account_user_filter_content_ll_filter
                    val filterContentCloseButton = itemView.fg_account_user_filter_content_iv_close
                    val filterContentResetButton = itemView.fg_account_user_filter_content_iv_reset
                    val filterUserNameEditText = itemView.fg_account_user_filter_content_et_user_name
                    val filterMinDictionaryCountEditText = itemView.fg_account_user_filter_content_et_min_dictionary_count
                    val filterMaxDictionaryCountEditText = itemView.fg_account_user_filter_content_et_max_dictionary_count
                    val filterMinTranslationCountEditText = itemView.fg_account_user_filter_content_et_min_translation_count
                    val filterMaxTranslationCountEditText = itemView.fg_account_user_filter_content_et_max_translation_count


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
                            userFilter.resetSearch()
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
                            userFilter.resetFilter()
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

                    filterUserNameEditText.addTextChangedListener(userFilter.textWatcher)

                    filterMinDictionaryCountEditText.addTextChangedListener(userFilter.textWatcher)
                    filterMaxDictionaryCountEditText.addTextChangedListener(userFilter.textWatcher)

                    filterMinTranslationCountEditText.addTextChangedListener(userFilter.textWatcher)
                    filterMaxTranslationCountEditText.addTextChangedListener(userFilter.textWatcher)
                }
            }
        }

    }

    private fun useFilter(){
        initializeAdapter(beforeInit,afterInit)
    }

    override fun getItemCount(): Int = displayedUsers.size

    private var beforeInit = {}
    private var afterInit = {}
    private var onError = {}

    fun initializeAdapter(beforeInit: ()-> Unit = this.beforeInit,
                          afterInit: ()->Unit = this.afterInit,
                          onError: ()->Unit = this.onError){
        this.beforeInit = beforeInit
        this.afterInit = afterInit
        userFilter.useFilter = ::useFilter
        reset()
        when {
            userFilter.All.get() ->
                if(!allUsers.any()){
                    beforeInit()
                    serviceManager.user.get({allUsers ->
                        this.allUsers.addAll(allUsers)
                        this.allUsers.remove(this.allUsers.single{ it.Id == App.currentUser.Id })
                        setUsers(allUsers)
                        ownUsers.clear()
                        afterInit()
                    },{
                        EmotionToast.showSad(App.instance.getString(R.string.unable_to_load_all_user))
                        onError()
                    })
                }
                else{
                    setUsers(allUsers)
                }
            !userFilter.All.get() ->
                if(!ownUsers.any()){
                    beforeInit()
                    serviceManager.user.getViewersByUserId(userId,{ ownUsers ->
                        this.ownUsers.addAll(ownUsers)
                        setUsers(ownUsers)
                        allUsers.clear()
                        afterInit()
                    },{
                        EmotionToast.showSad(App.instance.getString(R.string.unable_to_load_users))
                        onError()
                    })
                }
                else{
                    setUsers(ownUsers)
                }
        }
    }

    private fun setUsers(Users: List<User>) {
        displayedUsers.clear()
        displayedUsers.addAll(0, userFilter.filter(Users))
        displayedUsers.add(filterPosition, User())
        notifyDataSetChanged()
    }

    private fun reset(){
        ownUsers.clear()
        allUsers.clear()
    }

    inner class UserViewHolder(itemView: View, val binding: UserListItemBinding) : RecyclerView.ViewHolder(itemView)

    inner class FilterViewHolder(itemView: View, val binding: UserFilterMenuBinding): RecyclerView.ViewHolder(itemView)

    interface OnUserClickedListener {
        fun onUserLongClicked(user: User): Boolean
    }
}