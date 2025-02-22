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
import androidx.constraintlayout.widget.ConstraintLayout

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
        var gameOn = false
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
        var devActualWorker = "stone"
        var devBonusWorker = 1
        var devCheat = false
        var devBonusMoney = 999999999

        // Money Page
        var moneyAmount = 0
        var totalMaterials = 0
        var totalPriceMaterials = 0

        // Main Page

        // Stone
        var stoneAmount = 0
        var stoneExtraction = 1
        var stoneProgressMax = 10000
        var stoneProgress = 50
        var stoneBonus = 10
        var stoneWorker = false
        var stoneWorkerPrice = 10
        var stoneWorkerFirst = true
        var stonePrice = 1
        var stoneLocked = false
        var stoneWorkerNotEnough = false
        var stoneUnlocked = true
        var stoneAccept = false

        // Silver
        var silverAmount = 0
        var silverExtraction = 1
        var silverProgressMax = 20000
        var silverProgress = 50
        var silverBonus = 10
        var silverWorker = false
        var silverWorkerPrice = 150
        var silverWorkerFirst = true
        var silverPrice = 5
        var silverLocked = false
        var silverWorkerNotEnough = false
        var silverUnlocked = false
        var silverStoneToGet = 50
        var silverAccept = false
        var silverFirst = true

        // Iron
        var ironAmount = 0
        var ironExtraction = 1
        var ironProgressMax = 40000
        var ironProgress = 50
        var ironBonus = 10
        var ironWorker = false
        var ironWorkerPrice = 2000
        var ironWorkerFirst = true
        var ironPrice = 25
        var ironLocked = false
        var ironWorkerNotEnough = false
        var ironUnlocked = false
        var ironStoneToGet = 250
        var ironSilverToGet = 50
        var ironFirst = true

        var x : Double? = 0.0
        var y : Double? = 0.0

        fun saveData() {
            val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.apply{
                putInt("MONEY_AMOUNT_KEY", moneyAmount)

                putBoolean("SILVER_FIRST_KEY", silverFirst)
                putBoolean("IRON_FIRST_KEY", ironFirst)

                putInt("STONE_AMOUNT_KEY", stoneAmount)
                putInt("SILVER_AMOUNT_KEY", silverAmount)
                putInt("IRON_AMOUNT_KEY", ironAmount)

                putInt("SILVER_STONE_KEY", silverStoneToGet)
                putInt("IRON_STONE_KEY", ironStoneToGet)
                putInt("IRON_SILVER_KEY", ironSilverToGet)

                putBoolean("STONE_WORKER_KEY", stoneWorker)
                putBoolean("SILVER_WORKER_KEY", silverWorker)
                putBoolean("IRON_WORKER_KEY", ironWorker)

                putBoolean("SILVER_UNLOCKED_KEY", silverUnlocked)
                putBoolean("IRON_UNLOCKED_KEY", ironUnlocked)
            }.apply()
        }

        fun setDefaultValues() {
            // progress
            main_stone_progress.max = stoneProgressMax
            main_silver_progress.max = silverProgressMax
            main_iron_progress.max = ironProgressMax
            // materials price
            money_stone_price.text = "Price\n$stonePrice"
            money_silver_price.text = "Price\n$silverPrice"
            money_iron_price.text = "Price\n$ironPrice"
            // workers price
            stone_worker_price.text = "Price\n$stoneWorkerPrice"
            silver_worker_price.text = "Price\n$silverWorkerPrice"
            iron_worker_price.text = "Price\n$ironWorkerPrice"
        }

        fun loadText() {
            if(money_page.visibility == View.VISIBLE){
                totalMaterials = 0
                totalPriceMaterials = 0
                if(!stoneLocked){
                    totalMaterials += stoneAmount
                    totalPriceMaterials += (stoneAmount*stonePrice)
                }
                if(!silverLocked){
                    totalMaterials += silverAmount
                    totalPriceMaterials += (silverAmount*silverPrice)
                }
                if(!ironLocked){
                    totalMaterials += ironAmount
                    totalPriceMaterials += (ironAmount*ironPrice)
                }
                money_amount_text.text = "Money: $moneyAmount"
                sell_materials_btn.text = "Sell Materials\nTotal: $totalMaterials\nGain: $totalPriceMaterials"
                money_stone_amount.text = "Stone\n$stoneAmount"
                money_silver_amount.text = "Silver\n$silverAmount"
                money_iron_amount.text = "Iron\n$ironAmount"
            }
            if(main_page.visibility == View.VISIBLE) {
                main_stone_btn.text = "Stone\n$stoneAmount"
                main_silver_btn.text = "Silver\n$silverAmount"
                main_iron_btn.text = "Iron\n$ironAmount"
            }
        }

        fun loadWorkers() {
            if(stoneWorker){
                stone_worker_price.visibility = View.GONE
                stone_worker_text.visibility = View.GONE
                stone_worker_btn.visibility = View.GONE
                stone_worker.visibility = View.GONE
            }
            if(silverWorker){
                silver_worker_price.visibility = View.GONE
                silver_worker_text.visibility = View.GONE
                silver_worker_btn.visibility = View.GONE
                silver_worker.visibility = View.GONE
            }
            if(ironWorker){
                iron_worker_price.visibility = View.GONE
                iron_worker_text.visibility = View.GONE
                iron_worker_btn.visibility = View.GONE
                iron_worker.visibility = View.GONE
            }
        }

        fun unlockedMaterialsCheck(isLoading: Boolean) {
            if(silverUnlocked && silverFirst || isLoading && silverUnlocked){
                blocked_main_silver.visibility = View.GONE
                lock_main_silver.visibility = View.GONE
                lock_main_silver_text.visibility = View.GONE

                sell_silver_btn.visibility = View.VISIBLE
                sell_silver.visibility = View.VISIBLE
                money_silver_amount.visibility = View.VISIBLE
                money_silver_price.visibility = View.VISIBLE

                if(!silverWorker) {
                    silver_worker_price.visibility = View.VISIBLE
                    silver_worker_text.visibility = View.VISIBLE
                    silver_worker_btn.visibility = View.VISIBLE
                    silver_worker.visibility = View.VISIBLE
                }
                silverFirst = false
            }
            if(ironUnlocked && ironFirst || isLoading && ironUnlocked){
                blocked_main_iron.visibility = View.GONE
                lock_main_iron.visibility = View.GONE
                lock_main_iron_text.visibility = View.GONE

                sell_iron_btn.visibility = View.VISIBLE
                sell_iron.visibility = View.VISIBLE
                money_iron_amount.visibility = View.VISIBLE
                money_iron_price.visibility = View.VISIBLE

                if(!ironWorker) {
                    iron_worker_price.visibility = View.VISIBLE
                    iron_worker_text.visibility = View.VISIBLE
                    iron_worker_btn.visibility = View.VISIBLE
                    iron_worker.visibility = View.VISIBLE
                }
                ironFirst = false
            }
        }

        fun loadData() {
            val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val savedMoneyAmount: Int = sharedPreferences.getInt("MONEY_AMOUNT_KEY", moneyAmount)

            val savedSilverFirst: Boolean = sharedPreferences.getBoolean("SILVER_FIRST_KEY", silverFirst)
            val savedIronFirst: Boolean = sharedPreferences.getBoolean("IRON_FIRST_KEY", ironFirst)

            val savedStoneAmount: Int = sharedPreferences.getInt("STONE_AMOUNT_KEY", stoneAmount)
            val savedSilverAmount: Int = sharedPreferences.getInt("SILVER_AMOUNT_KEY", silverAmount)
            val savedIronAmount: Int = sharedPreferences.getInt("IRON_AMOUNT_KEY", ironAmount)

            val savedSilverStone: Int = sharedPreferences.getInt("SILVER_STONE_KEY", silverStoneToGet)
            val savedIronStone: Int = sharedPreferences.getInt("IRON_STONE_KEY", ironStoneToGet)
            val savedIronSilver: Int = sharedPreferences.getInt("IRON_SILVER_KEY", ironSilverToGet)

            val savedStoneWorker: Boolean = sharedPreferences.getBoolean("STONE_WORKER_KEY", stoneWorker)
            val savedSilverWorker: Boolean = sharedPreferences.getBoolean("SILVER_WORKER_KEY", silverWorker)
            val savedIronWorker: Boolean = sharedPreferences.getBoolean("IRON_WORKER_KEY", ironWorker)

            val savedSilverUnlocked: Boolean = sharedPreferences.getBoolean("SILVER_UNLOCKED_KEY", silverUnlocked)
            val savedIronUnlocked: Boolean = sharedPreferences.getBoolean("IRON_UNLOCKED_KEY", ironUnlocked)

            moneyAmount = savedMoneyAmount

            silverFirst = savedSilverFirst
            ironFirst = savedIronFirst

            stoneAmount = savedStoneAmount
            silverAmount = savedSilverAmount
            ironAmount = savedIronAmount

            silverStoneToGet = savedSilverStone
            ironStoneToGet = savedIronStone
            ironSilverToGet = savedIronSilver

            stoneWorker = savedStoneWorker
            silverWorker = savedSilverWorker
            ironWorker = savedIronWorker

            silverUnlocked = savedSilverUnlocked
            ironUnlocked = savedIronUnlocked

            setDefaultValues()
            loadWorkers()
            unlockedMaterialsCheck(true)
            loadText()
            dataLoaded = true
        }

        main_page_content.setOnTouchListener { v, event ->
            if(main_page.visibility == View.VISIBLE) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = main_page_content.x.toDouble() - event.rawX
                        y = main_page_content.y.toDouble() - event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (event.rawY + y!!.toInt() <= 40 && event.rawY + y!!.toInt() >= -2300) {
                            main_page_content.y = event.rawY + y!!.toInt()
                        }
                    }
                }
            }

            true
        }
        screen_slide.setOnTouchListener { v, event ->
            if(main_page.visibility == View.VISIBLE) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = main_page_content.x.toDouble() - event.rawX
                        y = main_page_content.y.toDouble() - event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (event.rawY + y!!.toInt() <= 40 && event.rawY + y!!.toInt() >= -2300) {
                            main_page_content.y = event.rawY + y!!.toInt()
                        }
                    }
                }
            }

            true
        }

        val workerGetMaterials = lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                lifecycleScope.launch {
                    if(gameOn) {
                        if (main_stone_progress.progress < stoneProgressMax && stoneWorker && stoneUnlocked) {
                            main_stone_progress.progress += stoneProgress*devBonusWorker
                        }
                        if (main_silver_progress.progress < silverProgressMax && silverWorker && silverUnlocked) {
                            main_silver_progress.progress += silverProgress*devBonusWorker
                        }
                        if (main_iron_progress.progress < ironProgressMax && ironWorker && ironUnlocked) {
                            main_iron_progress.progress += ironProgress*devBonusWorker
                        }
                    }
                }
                delay(25L)
            }
        }
        val workerDoneMaterials = lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                lifecycleScope.launch {
                    if (main_stone_progress.progress >= stoneProgressMax && stoneAmount <= MAX_VALUE) {
                        stoneAmount += stoneExtraction
                        main_stone_progress.progress = 0
                        loadText()
                        stoneAccept = true
                    }
                    if (main_silver_progress.progress >= silverProgressMax && silverAmount <= MAX_VALUE) {
                        silverAmount += silverExtraction
                        main_silver_progress.progress = 0
                        loadText()
                        silverAccept = true
                    }
                    if (main_iron_progress.progress >= ironProgressMax && ironAmount <= MAX_VALUE) {
                        ironAmount += ironExtraction
                        main_iron_progress.progress = 0
                        loadText()
                    }
                    if(!silverUnlocked){
                        if(stoneAccept && silverStoneToGet>0){
                            silverStoneToGet-=stoneExtraction
                        }
                        if(silverStoneToGet<=0){
                            blocked_main_silver.visibility = View.GONE
                            lock_main_silver.visibility = View.GONE
                            lock_main_silver_text.visibility = View.GONE
                            silverUnlocked = true
                        }else{
                            lock_main_silver_text.text = "Locked\n$silverStoneToGet Stone"
                        }
                    }
                    if(!ironUnlocked){
                        if(stoneAccept && ironStoneToGet>0){
                            ironStoneToGet-=stoneExtraction
                        }
                        if(silverAccept && ironSilverToGet>0){
                            ironSilverToGet-=silverExtraction
                        }
                        if(ironStoneToGet<=0 && ironSilverToGet<=0){
                            blocked_main_iron.visibility = View.GONE
                            lock_main_iron.visibility = View.GONE
                            lock_main_iron_text.visibility = View.GONE
                            ironUnlocked = true
                        }else{
                            lock_main_iron_text.text = "Locked\n$ironStoneToGet Stone + $ironSilverToGet Silver"
                        }
                    }
                    stoneAccept = false
                    silverAccept = false
                    test_dev_text.text = main_page_content.y.toString()
                }
                delay(20L)
            }
        }

        val unlockMaterials = lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                lifecycleScope.launch {
                    unlockedMaterialsCheck(false)
                }
                delay(50L)
            }
        }

        val screenFix = lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                lifecycleScope.launch {
                    if(main_page_content.y>0 && main_page_content.y!=0F){
                        main_page_content.y-=2
                        if(main_page_content.y<=0 && main_page_content.y>=-10){
                            main_page_content.y = 0F
                        }
                    }else{
                        if(main_page_content.y<=0 && main_page_content.y>=-5){
                            main_page_content.y = 0F
                        }
                    }
                    if(main_page_content.y<-2260 && main_page_content.y!=-2260F){
                        main_page_content.y+=2
                        if(main_page_content.y>=-2250 && main_page_content.y<=-2260){
                            main_page_content.y = -2260F
                        }
                    }else{
                        if(main_page_content.y>=-2255 && main_page_content.y<=-2260){
                            main_page_content.y = -2260F
                        }
                    }
                }
                delay(35L)
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
            loadText()
            main_stone_btn.setOnClickListener {
                if(stoneUnlocked) {
                    main_stone_progress.progress += (stoneProgress * stoneBonus)
                }
            }
            main_silver_btn.setOnClickListener {
                if(silverUnlocked) {
                    main_silver_progress.progress += (silverProgress * silverBonus)
                }
            }
            main_iron_btn.setOnClickListener {
                if(ironUnlocked) {
                    main_iron_progress.progress += (ironProgress * ironBonus)
                }
            }
        }

        fun moneyPage() {
            money_page.visibility = View.VISIBLE
            loadText()
            // sell materials
            sell_materials_btn.setOnClickListener {
                if(!stoneLocked){
                    moneyAmount += (stoneAmount*stonePrice)
                    stoneAmount = 0
                    loadText()
                }
                if(!silverLocked){
                    moneyAmount += (silverAmount*silverPrice)
                    silverAmount = 0
                    loadText()
                }
                if(!ironLocked){
                    moneyAmount += (ironAmount*ironPrice)
                    ironAmount = 0
                    loadText()
                }
            }
            // sell lock materials
            sell_stone_btn.setOnClickListener {
                if(stoneLocked){
                    stoneLocked = false
                    sell_stone_btn.setBackgroundResource(R.drawable.btn_unlocked_material)
                }else{
                    stoneLocked = true
                    sell_stone_btn.setBackgroundResource(R.drawable.btn_locked_material)
                }
                loadText()
            }
            sell_silver_btn.setOnClickListener {
                if(silverLocked){
                    silverLocked = false
                    sell_silver_btn.setBackgroundResource(R.drawable.btn_unlocked_material)
                }else{
                    silverLocked = true
                    sell_silver_btn.setBackgroundResource(R.drawable.btn_locked_material)
                }
                loadText()
            }
            sell_iron_btn.setOnClickListener {
                if(ironLocked){
                    ironLocked = false
                    sell_iron_btn.setBackgroundResource(R.drawable.btn_unlocked_material)
                }else{
                    ironLocked = true
                    sell_iron_btn.setBackgroundResource(R.drawable.btn_locked_material)
                }
                loadText()
            }
            // buy workers
            stone_worker_btn.setOnClickListener {
                if(stoneUnlocked) {
                    if (moneyAmount >= stoneWorkerPrice) {
                        moneyAmount -= stoneWorkerPrice
                        loadText()
                        stoneWorker = true
//                    val params = silver_worker.layoutParams as ConstraintLayout.LayoutParams
//                    params.topToBottom = money_worker.id
//                    silver_worker.requestLayout()
                        stone_worker_price.visibility = View.GONE
                        stone_worker_text.visibility = View.GONE
                        stone_worker_btn.visibility = View.GONE
                        stone_worker.visibility = View.GONE
                    } else {
                        if (!stoneWorkerNotEnough) {
                            stoneWorkerNotEnough = true
                            stone_worker_price.text = "Not Enough"
                            object : CountDownTimer(1000, 2000) {
                                override fun onTick(millisUntilFinished: Long) {
                                }

                                override fun onFinish() {
                                    stone_worker_price.text = "Price\n$stoneWorkerPrice"
                                    stoneWorkerNotEnough = false
                                }
                            }.start()
                        }
                    }
                }
            }
            silver_worker_btn.setOnClickListener {
                if(silverUnlocked) {
                    if (moneyAmount >= silverWorkerPrice) {
                        moneyAmount -= silverWorkerPrice
                        loadText()
                        silverWorker = true
//                    val params = silver_worker.layoutParams as ConstraintLayout.LayoutParams
//                    params.topToBottom = money_worker.id
//                    silver_worker.requestLayout()
                        loadWorkers()
                    } else {
                        if (!silverWorkerNotEnough) {
                            silverWorkerNotEnough = true
                            silver_worker_price.text = "Not Enough"
                            object : CountDownTimer(1000, 2000) {
                                override fun onTick(millisUntilFinished: Long) {
                                }

                                override fun onFinish() {
                                    silver_worker_price.text = "Price\n$silverWorkerPrice"
                                    silverWorkerNotEnough = false
                                }
                            }.start()
                        }
                    }
                }
            }
            iron_worker_btn.setOnClickListener {
                if(ironUnlocked) {
                    if (moneyAmount >= ironWorkerPrice) {
                        moneyAmount -= ironWorkerPrice
                        loadText()
                        ironWorker = true
                        loadWorkers()
                    } else {
                        if (!ironWorkerNotEnough) {
                            ironWorkerNotEnough = true
                            iron_worker_price.text = "Not Enough"
                            object : CountDownTimer(1000, 2000) {
                                override fun onTick(millisUntilFinished: Long) {
                                }

                                override fun onFinish() {
                                    iron_worker_price.text = "Price\n$ironWorkerPrice"
                                    ironWorkerNotEnough = false
                                }
                            }.start()
                        }
                    }
                }
            }
        }

        fun pageChange() {
            page_change.visibility = View.VISIBLE
            change_main_page.setOnClickListener {
                if(main_page.visibility != View.VISIBLE) {
                    main_page.visibility = View.VISIBLE
                    money_page.visibility = View.GONE
                    skill_page.visibility = View.GONE
                    mainPage()
                }
            }
            change_money_page.setOnClickListener {
                if(money_page.visibility != View.VISIBLE) {
                    main_page.visibility = View.GONE
                    money_page.visibility = View.VISIBLE
                    skill_page.visibility = View.GONE
                    moneyPage()
                }
            }
            change_skill_page.setOnClickListener {
                if(skill_page.visibility != View.VISIBLE) {
                    main_page.visibility = View.GONE
                    money_page.visibility = View.GONE
                    skill_page.visibility = View.VISIBLE
                }
            }
        }

        fun loadingPage() {
            loading_page.visibility = View.VISIBLE
            main_page.visibility = View.GONE
            money_page.visibility = View.GONE
            skill_page.visibility = View.GONE
            dev_page.visibility = View.GONE
            page_change.visibility = View.GONE
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
                                pageChange()
                                loading_text.text = "Loading"
                                loading_continue_text.visibility = View.INVISIBLE
                                loading_page.setOnClickListener(null)
                                gameOn = true
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

            moneyAmount = 0
            stoneAmount = 0
            main_stone_progress.progress = 0
            stoneWorker = false
            stoneUnlocked = true

            silverAmount = 0
            main_silver_progress.progress = 0
            silverWorker = false
            silverUnlocked = false
            silverFirst = true
            silverStoneToGet = 50

            ironAmount = 0
            main_iron_progress.progress = 0
            ironWorker = false
            ironUnlocked = false
            ironFirst = true
            ironStoneToGet = 250
            ironSilverToGet = 50

            stone_worker_price.visibility = View.VISIBLE
            stone_worker_text.visibility = View.VISIBLE
            stone_worker_btn.visibility = View.VISIBLE
            stone_worker.visibility = View.VISIBLE

            sell_silver_btn.visibility = View.GONE
            sell_silver.visibility = View.GONE
            money_silver_amount.visibility = View.GONE
            money_silver_price.visibility = View.GONE

//            val params = silver_worker.layoutParams as ConstraintLayout.LayoutParams
//            params.topToBottom = stone_worker.id
//            silver_worker.requestLayout()

            iron_worker_price.visibility = View.GONE
            iron_worker_text.visibility = View.GONE
            iron_worker_btn.visibility = View.GONE
            iron_worker.visibility = View.GONE

            sell_iron_btn.visibility = View.GONE
            sell_iron.visibility = View.GONE
            money_iron_amount.visibility = View.GONE
            money_iron_price.visibility = View.GONE

            blocked_main_silver.visibility = View.VISIBLE
            lock_main_silver.visibility = View.VISIBLE
            lock_main_silver_text.visibility = View.VISIBLE

            blocked_main_iron.visibility = View.VISIBLE
            lock_main_iron.visibility = View.VISIBLE
            lock_main_iron_text.visibility = View.VISIBLE

            saveData()
            loadData()
            loadText()
            resetDone = true
        }

        fun devWorkerCheck() {
            if(devActualWorker == "stone") {
                if (stoneWorker) {
                    worker_btn.setTextColor(Color.parseColor("#55EE55"))
                } else {
                    worker_btn.setTextColor(Color.parseColor("#EE5555"))
                }
            }
            if(devActualWorker == "silver"){
                if(silverWorker){
                    worker_btn.setTextColor(Color.parseColor("#55EE55"))
                } else {
                    worker_btn.setTextColor(Color.parseColor("#EE5555"))
                }
            }
            if(devActualWorker == "iron"){
                if(ironWorker){
                    worker_btn.setTextColor(Color.parseColor("#55EE55"))
                } else {
                    worker_btn.setTextColor(Color.parseColor("#EE5555"))
                }
            }
        }

        fun devPage() {
            devWorkerCheck()
            worker_btn.setOnClickListener {
                if(devActualWorker == "stone") {
                    stoneWorker = !stoneWorker
                    if (!stoneWorkerFirst && stoneWorker) {
                        stoneWorkerFirst = true
                    }
                }
                if(devActualWorker == "silver") {
                    silverWorker = !silverWorker
                    if (!silverWorkerFirst && silverWorker) {
                        silverWorkerFirst = true
                    }
                }
                if(devActualWorker == "iron") {
                    ironWorker = !ironWorker
                    if (!ironWorkerFirst && ironWorker) {
                        ironWorkerFirst = true
                    }
                }
                devWorkerCheck()
            }
            test_dev_text.setOnClickListener {
                when(devCheat) {
                    false -> {
                        if((moneyAmount+devBonusMoney)<MAX_VALUE && (moneyAmount+devBonusMoney)>=0) {
                            moneyAmount += devBonusMoney
                        }
                        stoneBonus = 1000
                        silverBonus = 5000
                        ironBonus = 10000
                        devBonusWorker = 100
                        devCheat = true
                        Toast.makeText(this, "devCheat On", Toast.LENGTH_SHORT).show()
                        loadText()
                    }
                    true -> {
                        stoneBonus = 10
                        silverBonus = 10
                        ironBonus = 10
                        devBonusWorker = 1
                        devCheat = false
                        Toast.makeText(this, "devCheat Off", Toast.LENGTH_SHORT).show()
                        loadText()
                    }
                }
            }
            next_worker_change_btn.setOnClickListener {
                when(devActualWorker){
                    "stone" -> {
                        devActualWorker = "silver"
                    }
                    "silver" -> {
                        devActualWorker = "iron"
                    }
                    "iron" -> {
                        devActualWorker = "stone"
                    }
                }
                worker_btn.text = "$devActualWorker\nworker"
                devWorkerCheck()
            }
            previous_worker_change_btn.setOnClickListener {
                when(devActualWorker){
                    "stone" -> {
                        devActualWorker = "iron"
                    }
                    "silver" -> {
                        devActualWorker = "stone"
                    }
                    "iron" -> {
                        devActualWorker = "silver"
                    }
                }
                worker_btn.text = "$devActualWorker\nworker"
                devWorkerCheck()
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

        dev_switch.setOnClickListener {
            if (dev_switch.isChecked) {
                dev_page.visibility = View.VISIBLE
                dev_switch.text = "Dev"
            } else {
                dev_switch.text = "Player"
                dev_page.visibility = View.GONE
            }
        }

        if(gameStart){
            loadData()
            loadingPage()
            devPage()
        }
    }
}