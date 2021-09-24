package com.example.idlemicra
import android.R.attr
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var MAX_VALUE = 2147483647

        // Main Game
        var gameStart = true
        var doReset = false
        var resetDone = false

        // Game start
        var dataLoaded = false

        // Loading
        var loadingTextProgress = 0
        var loadingTextChange: Boolean
        var loadingStatus = true
        var loadingProgressMax = 10000

        // Dev
        var managerChange: Boolean

        // Main Page

        // Stone
        var stoneAmount = 0
        var stoneExtraction = 1
        var stoneProgressMax = 12000
        var stoneProgress = 50
        var stoneBonus = 10
        var stoneManager = false
        var stoneManagerFirst = true

        // Silver
        var silverAmount = 0
        var silverExtraction = 1
        var silverProgressMax = 36000
        var silverProgress = 50
        var silverBonus = 10
        var silverManager = false
        var silverManagerFirst = true

        // Iron
        var ironAmount = 0
        var ironExtraction = 1
        var ironProgressMax = 108000
        var ironProgress = 50
        var ironBonus = 10
        var ironManager = false
        var ironManagerFirst = true

        var x : Double? = 0.0
        var y : Double? = 0.0

        fun saveData(){
            val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.apply{
                putInt("STONE_AMOUNT_KEY", stoneAmount)
                putInt("SILVER_AMOUNT_KEY", silverAmount)
                putInt("IRON_AMOUNT_KEY", ironAmount)
            }.apply()
        }

        fun loadText() {
            main_stone_btn.text = "Stone\n$stoneAmount"
            main_silver_btn.text = "Silver\n$silverAmount"
            main_iron_btn.text = "Iron\n$ironAmount"
        }

        fun loadData() {
            val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val savedStoneAmount: Int = sharedPreferences.getInt("STONE_AMOUNT_KEY", stoneAmount)
            val savedSilverAmount: Int = sharedPreferences.getInt("SILVER_AMOUNT_KEY", silverAmount)
            val savedIronAmount: Int = sharedPreferences.getInt("IRON_AMOUNT_KEY", ironAmount)

            stoneAmount = savedStoneAmount
            silverAmount = savedSilverAmount
            ironAmount = savedIronAmount

            loadText()
            dataLoaded = true
        }

        main_page_content.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = main_page_content.x.toDouble() - event.rawX
                    y = main_page_content.y.toDouble() - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    if(event.rawY + y!!.toInt() <= 0.0 && event.rawY + y!!.toInt() >= -2260) {
                        main_page_content.y = event.rawY + y!!.toInt()
                    }else{
                    }
                }
            }

            true
        }

        val managerGetMaterials = lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                lifecycleScope.launch {
                    if (main_stone_progress.progress < stoneProgressMax && stoneManager) {
                        main_stone_progress.progress += stoneProgress
                    }
                    if (main_silver_progress.progress < silverProgressMax && silverManager) {
                        main_silver_progress.progress += silverProgress
                    }
                    if (main_iron_progress.progress < ironProgressMax && ironManager) {
                        main_iron_progress.progress += ironProgress
                    }
                }
                delay(25L)
            }
        }
        val managerDoneMaterials = lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                lifecycleScope.launch {
                    if (main_stone_progress.progress >= stoneProgressMax && stoneAmount <= MAX_VALUE) {
                        stoneAmount += stoneExtraction
                        main_stone_progress.progress = 0
                        main_stone_btn.text = "Stone\n$stoneAmount"
                    }
                    if (main_silver_progress.progress >= silverProgressMax && silverAmount <= MAX_VALUE) {
                        silverAmount += silverExtraction
                        main_silver_progress.progress = 0
                        main_silver_btn.text = "silver\n$silverAmount"
                    }
                    if (main_iron_progress.progress >= ironProgressMax && ironAmount <= MAX_VALUE) {
                        ironAmount += ironExtraction
                        main_iron_progress.progress = 0
                        main_iron_btn.text = "iron\n$ironAmount"
                    }
                    test_dev_text.text = main_page_content.y.toString()
                }
                delay(20L)
            }
        }

        val saveJob = lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                lifecycleScope.launch {
                    if(!doReset) {
                        saveData()
                    }
                }
                delay(50L)
            }
        }

        fun mainPage() {
            loading_page.visibility = View.GONE
            main_page.visibility = View.VISIBLE
            main_stone_btn.setOnClickListener {
                main_stone_progress.progress += (stoneProgress * stoneBonus)
            }
            main_silver_btn.setOnClickListener {
                main_silver_progress.progress += (silverProgress * silverBonus)
            }
            main_iron_btn.setOnClickListener {
                main_iron_progress.progress += (ironProgress * ironBonus)
            }
        }

        fun loadingPage() {
            loading_page.visibility = View.VISIBLE
            main_page.visibility = View.GONE
            dev_page.visibility = View.GONE
            loading_progress.progress = 0
            loadingStatus = true
            gameStart = false
            object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if(loadingStatus) {
                        loadingTextChange = false
                        if (loadingTextProgress == 0) {
                            if (!loadingTextChange) {
                                loading_text.text = "Loading."
                                loadingTextProgress = 1
                                loadingTextChange = true
                            }
                        }
                        if (loadingTextProgress == 1 && !loadingTextChange) {
                            loading_text.text = "Loading.."
                            loadingTextProgress = 2
                            loadingTextChange = true
                        }
                        if (loadingTextProgress == 2 && !loadingTextChange) {
                            loading_text.text = "Loading..."
                            loadingTextProgress = 0
                            loadingTextChange = true
                        }
                    }else{
                        cancel()
                    }
                }
                override fun onFinish() {
                    if(loadingStatus){
                        start()
                    }
                }
            }.start()

            object : CountDownTimer(10000, 25){
                override fun onTick(millisUntilFinished: Long) {
                    if(loading_progress.progress <= loadingProgressMax/4){
                        loading_progress.progress += 125
                    }else{
                        if(doReset){
                            if(loading_progress.progress <= loadingProgressMax/3){
                                loading_progress.progress += 125
                            }else{
                                if(resetDone){
                                    loading_progress.progress += 125
                                }
                            }
                        }else{
                            if(dataLoaded){
                                loading_progress.progress += 125
                            }
                        }
                    }
                    if(loading_progress.progress >= loadingProgressMax){
                        dataLoaded = false
                        resetDone = false
                        doReset = false
                        cancel()
                    }
                }
                override fun onFinish() {
                    if(loadingStatus){
                        start()
                    }
                }
            }.start()

            object : CountDownTimer(1000, 25){
                override fun onTick(millisUntilFinished: Long) {
                    if(loadingStatus){
                        if(loading_progress.progress >= loadingProgressMax) {
                            loadingStatus = false
                            loading_text.text = "Done"
                            loading_continue_text.visibility = View.VISIBLE
                            loading_page.setOnClickListener {
                                mainPage()
                                loading_text.text = "Loading"
                                loading_continue_text.visibility = View.INVISIBLE
                                loading_page.setOnClickListener(null)
                                cancel()
                            }
                        }
                    }
                }
                override fun onFinish() {
                    start()
                }
            }.start()
        }

        fun resetJob() {
            loadingPage()
            dev_switch.isChecked = false
            stoneAmount = 0
            main_stone_progress.progress = 0
            stoneManager = false
            silverAmount = 0
            main_silver_progress.progress = 0
            silverManager = false
            ironAmount = 0
            main_iron_progress.progress = 0
            ironManager = false
            saveData()
            loadText()
            resetDone = true
        }

        fun devPage() {
            manager_btn.setOnClickListener {
                if(manager_change_btn.text == "stone") {
                    stoneManager = !stoneManager
                    if (!stoneManagerFirst && stoneManager) {
                        stoneManagerFirst = true
                    }
                    Toast.makeText(this, "Stone - State: " + stoneManager.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                if(manager_change_btn.text == "silver") {
                    silverManager = !silverManager
                    if (!silverManagerFirst && silverManager) {
                        silverManagerFirst = true
                    }
                    Toast.makeText(this, "Silver - State: " + silverManager.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                if(manager_change_btn.text == "iron") {
                    ironManager = !ironManager
                    if (!ironManagerFirst && ironManager) {
                        ironManagerFirst = true
                    }
                    Toast.makeText(this, "Iron - State: " + ironManager.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            manager_change_btn.setOnClickListener {
                managerChange = true
                if(manager_change_btn.text == "stone" && managerChange){
                    manager_btn.text = "silverManager"
                    manager_change_btn.text = "silver"
                    managerChange = false
                }
                if(manager_change_btn.text == "silver" && managerChange){
                    manager_btn.text = "ironManager"
                    manager_change_btn.text = "iron"
                    managerChange = false
                }
                if(manager_change_btn.text == "iron" && managerChange){
                    manager_btn.text = "stoneManager"
                    manager_change_btn.text = "stone"
                    managerChange = false
                }
            }
            reset_btn.setOnClickListener {
                if(doReset){
                    resetJob()
                }else{
                    reset_btn.setTextColor(Color.parseColor("#ff1100"))
                    reset_btn.text = "Confirm"
                    doReset = true
                    object : CountDownTimer(3000, 20){
                        override fun onTick(millisUntilFinished: Long) {
                            if(loadingStatus){
                                onFinish()
                            }
                        }
                        override fun onFinish() {
                            if(!loadingStatus){
                                doReset = false
                            }
                            reset_btn.setTextColor(Color.parseColor("#ffffff"))
                            reset_btn.text = "Reset"
                        }
                    }.start()
                }
            }
        }

        val alwaysCheck = lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                lifecycleScope.launch {
                    if(!doReset) {
                        if (dev_switch.isChecked) {
                            dev_page.visibility = View.VISIBLE
                            dev_switch.text = "Dev"
                        } else {
                            dev_switch.text = "Player"
                            dev_page.visibility = View.GONE
                        }
                    }
                }
                delay(200L)
            }
        }

        if(gameStart){
            loadData()
            loadingPage()
            devPage()
        }
    }
}