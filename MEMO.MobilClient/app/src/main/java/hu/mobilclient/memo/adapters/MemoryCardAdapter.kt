package hu.mobilclient.memo.adapters

import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.databinding.MemoryCardBinding
import hu.mobilclient.memo.model.practice.MemoryCard
import kotlinx.android.synthetic.main.memory_card.view.*

class MemoryCardAdapter  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val memoryCards = ArrayList<MemoryCard>()
    private val selectedCards = ArrayList<MemoryCardViewHolder>()
    private val foundCards = ArrayList<MemoryCard>()
    private val handler = Handler()

    lateinit var onEndListener: OnEndListener

    private val flip1: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.memory_card_flip_1)
    private val flip2: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.memory_card_flip_2)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding: MemoryCardBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.memory_card, parent, false)

        return MemoryCardViewHolder(binding.root, binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is MemoryCardViewHolder) {
            val itemView = holder.itemView

            val memoryCard = memoryCards[position]

            holder.binding.setVariable(BR.card, memoryCard)

            if(memoryCard.IsFlipped.get() && !memoryCard.IsFound){
                selectedCards.add(holder)
            }

            if(memoryCard.IsFound){
                foundCards.add(memoryCard)
                itemView.startAnimation(flip1)
                itemView.postDelayed({itemView.startAnimation(flip2)},flip1.duration)
                handler.postDelayed({
                    itemView.background = App.instance.getDrawable(R.drawable.bg_rounded_accent_2)
                    itemView.memory_card_tv_value.setTextColor(ContextCompat.getColor(App.instance, R.color.primary_light))
                    memoryCard.IsFlipped.set(true)
                }, flip1.duration)
            }
            else{
                itemView.background = App.instance.getDrawable(R.drawable.bg_rounded_white_3)
                itemView.memory_card_tv_value.setTextColor(ContextCompat.getColor(App.instance, R.color.primary_dark))
            }

            itemView.setOnClickListener{
                if(!foundCards.contains(memoryCard) && !selectedCards.contains(holder)){
                    if(selectedCards.size < 2){
                        selectedCards.add(holder)
                        itemView.startAnimation(flip1)
                        itemView.postDelayed({it.startAnimation(flip2)},flip1.duration)
                        handler.postDelayed({
                            memoryCard.IsFlipped.set(true)
                        }, flip1.duration)
                        if(selectedCards.size == 2 && foundCards.size == memoryCards.size - 2){
                            selectedCards.map{
                                it.binding.card!!.IsFound = true
                                foundCards.add(it.binding.card!!)
                                it.itemView.background = App.instance.getDrawable(R.drawable.bg_rounded_accent_2)
                                it.itemView.memory_card_tv_value.setTextColor(ContextCompat.getColor(App.instance, R.color.primary_light))
                            }
                            handler.postDelayed({
                                onEndListener.onEnd()
                            }, 1000)
                        }
                    }
                    else{
                        if(selectedCards.first().binding.card!!.TranslationId == selectedCards.last().binding.card!!.TranslationId){
                            selectedCards.map { it.binding.card!!.IsFound = true }
                            selectedCards.map{
                                it.binding.card!!.IsFound = true
                                foundCards.add(it.binding.card!!)
                                it.itemView.background = App.instance.getDrawable(R.drawable.bg_rounded_accent_2)
                                it.itemView.memory_card_tv_value.setTextColor(ContextCompat.getColor(App.instance, R.color.primary_light))
                            }
                            selectedCards.clear()
                            selectedCards.add(holder)
                            itemView.startAnimation(flip1)
                            itemView.postDelayed({it.startAnimation(flip2)},flip1.duration)
                            handler.postDelayed({
                                memoryCard.IsFlipped.set(true)
                            }, flip1.duration)
                        }
                        else{
                            selectedCards.map {
                                it.itemView.startAnimation(flip1)
                                it.itemView.postDelayed({it.itemView.startAnimation(flip2)},flip1.duration)
                                handler.postDelayed({
                                    it.binding.card!!.IsFlipped.set(false)
                                }, flip1.duration)
                            }
                            selectedCards.clear()
                        }
                    }
                }
            }
        }
    }

    fun isNotEmpty(): Boolean{
        return memoryCards.isNotEmpty()
    }

    private fun reset(){
        memoryCards.clear()
        foundCards.clear()
        selectedCards.clear()
    }

    fun initializeAdapter(memoryCards: List<MemoryCard>){
        reset()
        this.memoryCards.addAll(memoryCards.shuffled())
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = memoryCards.size

    inner class MemoryCardViewHolder(itemView: View, val binding: MemoryCardBinding) : RecyclerView.ViewHolder(itemView)

    interface OnEndListener {
        fun onEnd()
    }
}