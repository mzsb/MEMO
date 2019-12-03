package hu.mobilclient.memo.activities

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.adapters.MemoryCardAdapter
import hu.mobilclient.memo.data.PracticeDatabase
import hu.mobilclient.memo.data.memoryGame.MemoryCardEntity
import hu.mobilclient.memo.databinding.SpinnerItemDictionaryBlueBinding
import hu.mobilclient.memo.helpers.BindableArrayAdapter
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.memoapi.Dictionary
import hu.mobilclient.memo.model.practice.MemoryCard
import kotlinx.android.synthetic.main.activity_memory_game.*
import java.util.*
import kotlin.collections.ArrayList


class MemoryGameActivity : NetworkActivityBase(), MemoryCardAdapter.OnEndListener {

    private val adapter = MemoryCardAdapter()
    private lateinit var recyclerView: RecyclerView
    private val memoryCards = ArrayList<MemoryCard>()
    private lateinit var controlsConstraintLayout: ConstraintLayout
    private lateinit var dictionariesSpinner: Spinner
    private lateinit var progressBar: ProgressBar
    private lateinit var backButton: ImageView
    private var isGameLoaded = false

    private var isBackNavigation: Boolean = false

    private lateinit var vanish: Animation
    private lateinit var popUp: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_game)

        vanish = AnimationUtils.loadAnimation(App.instance, R.anim.vanish)
        popUp = AnimationUtils.loadAnimation(App.instance, R.anim.pop_up)

        controlsConstraintLayout = ac_memory_game_cl_controls
        dictionariesSpinner = ac_memory_game_sp_dictionaries
        progressBar = ac_memory_game_pb
        backButton = ac_memory_game_iv_back

        recyclerView = ac_memory_game_rv
        recyclerView.layoutManager = GridLayoutManager(applicationContext, Constants.MEMORYCARD_COLUMN_COUNT)
        adapter.onEndListener = this
        recyclerView.adapter = adapter
        progressBar.popUp()
    }

    override fun onPause() {
        super.onPause()
        if(memoryCards.any { it.IsFlipped.get() }) {
            Thread {
                PracticeDatabase.getInstance(this@MemoryGameActivity).memoryCardDao().deleteMemoryCards()
                PracticeDatabase.getInstance(this@MemoryGameActivity).memoryCardDao().insertMemoryCards(memoryCards.toEntity())
            }.start()
        }
        else{
            Thread {
                PracticeDatabase.getInstance(this@MemoryGameActivity).memoryCardDao().deleteMemoryCards()
            }.start()
        }
    }

    override fun onResume() {
        super.onResume()
        initializeDictionaries()
    }

    private fun initializeDictionaries(){
        serviceManager.dictionary.getByUserId(App.getCurrentUserId(), { dictionaries ->

            dictionariesSpinner.adapter = BindableArrayAdapter<Dictionary, SpinnerItemDictionaryBlueBinding>(App.instance, R.layout.spinner_item_dictionary_blue, dictionaries)

            dictionariesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, position: Int, id: Long) {
                    if (!isGameLoaded) {
                        controlsConstraintLayout.visibility = View.GONE
                        if (progressBar.visibility != View.VISIBLE) {
                            progressBar.popUp()
                        }
                        serviceManager.translation.getByDictionaryId(dictionaries[position].Id,
                                callback = { translations ->
                                    memoryCards.clear()
                                    translations.map { translation ->
                                        memoryCards.add(MemoryCard(
                                                TranslationId = translation.Id,
                                                Value = translation.Original,
                                                IsFound = false,
                                                IsFlipped = ObservableBoolean(false)))
                                        memoryCards.add(MemoryCard(
                                                TranslationId = translation.Id,
                                                Value = translation.Translated,
                                                IsFound = false,
                                                IsFlipped = ObservableBoolean(false)))
                                    }
                                    adapter.initializeAdapter(memoryCards)
                                    progressBar.vanish()
                                    controlsConstraintLayout.popUp()
                                },
                                errorCallback = {
                                    progressBar.vanish()
                                    controlsConstraintLayout.popUp()
                                    EmotionToast.showSad(getString(R.string.unable_to_load_translations))
                                })
                    } else {
                        isGameLoaded = false
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {}
            }

            if (dictionaries.isNotEmpty()) {
                Thread {
                    memoryCards.clear()
                    memoryCards.addAll(PracticeDatabase.getInstance(this@MemoryGameActivity).memoryCardDao().getMemoryCards().toModel())
                    runOnUiThread {
                        if (memoryCards.isEmpty()) {
                            dictionariesSpinner.setSelection(Constants.ZERO, true)
                            dictionariesSpinner.performItemClick(
                                    dictionariesSpinner.getChildAt(Constants.ZERO) as View,
                                    Constants.ZERO,
                                    dictionariesSpinner.adapter.getItemId(Constants.ZERO))
                        } else {
                            isGameLoaded = true
                            adapter.initializeAdapter(memoryCards)
                            progressBar.vanish()
                            recyclerView.popUp()
                        }
                    }
                }.start()
            }
        }, {
            backClick(backButton)
            EmotionToast.showSad(getString(R.string.unable_load_own_dictionaries))
        })
    }

    private fun View.popUp(){
        visibility = View.VISIBLE
        startAnimation(popUp)
    }

    private fun View.vanish(){
        startAnimation(vanish)
        postDelayed({
           visibility = View.GONE
        }, vanish.duration)
    }

    fun playClick(view: View){
        if(adapter.isNotEmpty()) {
            controlsConstraintLayout.vanish()
            recyclerView.popUp()
        }
        else{
            EmotionToast.showSad(getString(R.string.empty_dictionary))
        }
    }

    fun backClick(view: View){
        isBackNavigation = true
        onBackPressed()
    }

    fun restartClick(view: View){
        if(adapter.isNotEmpty()) {
            controlsConstraintLayout.vanish()
            memoryCards.map {
                it.IsFound = false
                it.IsFlipped.set(false)
            }
            adapter.initializeAdapter(memoryCards)
            recyclerView.popUp()
        }
        else{
            EmotionToast.showSad(getString(R.string.empty_dictionary))
        }
    }

    override fun onEnd() {
        memoryCards.map {
            it.IsFound = false
            it.IsFlipped.set(false)
        }
        recyclerView.vanish()
        controlsConstraintLayout.popUp()
    }

    override fun onBackPressed() {
        if(isBackNavigation) {
            super.onBackPressed()
        }
        else {
            if (controlsConstraintLayout.visibility == View.GONE && recyclerView.visibility == View.VISIBLE) {
                recyclerView.vanish()
                controlsConstraintLayout.popUp()
            }
        }
    }

    private fun List<MemoryCard>.toEntity(): List<MemoryCardEntity>{
        val entities = ArrayList<MemoryCardEntity>()
        this.map {memoryCard->
            entities.add(MemoryCardEntity(Id = null,
                                         TranslationId =  memoryCard.TranslationId.toString(),
                                         Value = memoryCard.Value,
                                         IsFlipped = memoryCard.IsFlipped.get().toString(),
                                         IsFound = memoryCard.IsFound.toString()))
        }
        return entities
    }

    private fun List<MemoryCardEntity>.toModel(): List<MemoryCard>{
        val models = ArrayList<MemoryCard>()
        this.map {memoryCardEntity ->
            models.add(MemoryCard(
                    TranslationId =  UUID.fromString(memoryCardEntity.TranslationId),
                    Value = memoryCardEntity.Value,
                    IsFlipped = ObservableBoolean(memoryCardEntity.IsFlipped == true.toString()),
                    IsFound = memoryCardEntity.IsFound == true.toString()))
        }
        return models
    }
}
