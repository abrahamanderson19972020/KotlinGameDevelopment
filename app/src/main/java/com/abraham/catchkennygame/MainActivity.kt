package com.abraham.catchkennygame

import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.PointerIcon
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.abraham.catchkennygame.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var timer:CountDownTimer
    private lateinit var imageView: ImageView
    private lateinit var boundaryArea:FrameLayout
    private lateinit var scoreText:TextView
    private lateinit var textLevel: TextView
    private lateinit var textGameOverMessage: TextView
    private var score = 0
    private var level = 1
    private val maxLevel = 10
    private val scorePerLevel = 10
    private var currentMoveInterval = 1000L
    private  val handler = Handler(Looper.getMainLooper())

    private val moveRunnable = object:Runnable{
        override fun run() {
            moveImageRandomly()
            handler.postDelayed(this, currentMoveInterval)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        imageView = binding.imageKenny
        boundaryArea = binding.gameArea
        scoreText = binding.textScore
        textLevel = binding.textLevel
        textGameOverMessage = binding.textGameOverMessage
        textLevel.text = "LEVEL: $level"
        timer = object : CountDownTimer(200000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.textTimer.text = "TIMER: ${secondsLeft.toString()}"
                textGameOverMessage.visibility = TextView.INVISIBLE
            }

            override fun onFinish() {
                binding.textTimer.text = "TIMER: 0"
                textGameOverMessage.visibility = TextView.VISIBLE
                textLevel.visibility = TextView.INVISIBLE
                handler.removeCallbacks(moveRunnable)
                imageView.animate().cancel()
                imageView.x = 0f
                imageView.y = 0f

                val alert = AlertDialog.Builder(this@MainActivity,R.style.CustomAlertDialog)
                alert.setTitle("Game Over")
                alert.setMessage("Do you want to play again?")
                alert.setNegativeButton("No", DialogInterface.OnClickListener{
                    dialog, which ->  Toast.makeText(this@MainActivity, "Game Over!", Toast.LENGTH_LONG).show()
                })
                alert.setPositiveButton("Yes", DialogInterface.OnClickListener{
                        dialog, which ->
                    val intentFromMain = intent
                    finish()
                    startActivity(intentFromMain)
                })
                alert.show()
            }

        }
        timer.start()

        imageView.setOnClickListener {
            score ++
            scoreText.text = "SCORE: ${score}"
            val newLevel = (score / scorePerLevel) + 1
            if(newLevel != level && newLevel <= maxLevel){
                level = newLevel
                textLevel.text = "LEVEL: $level"
                currentMoveInterval = 1000L -(level-1) * 100L
                handler.removeCallbacks(moveRunnable)
                handler.post(moveRunnable)

                if(level == maxLevel){
                    Toast.makeText(this, "🎉 You win the game!", Toast.LENGTH_LONG).show()
                    timer.cancel()
                    handler.removeCallbacks(moveRunnable)
                    imageView.animate().cancel()
                    imageView.x = 0f
                    imageView.y = 0f
                    textGameOverMessage.visibility = TextView.VISIBLE
                    textLevel.visibility = TextView.INVISIBLE
                }
            }
        }

        imageView.setOnHoverListener { view, motionEvent ->
            view.pointerIcon = PointerIcon.getSystemIcon(this, PointerIcon.TYPE_HAND)
            false
        }


        boundaryArea.post{
            handler.post(moveRunnable)
        }
    }

    private fun moveImageRandomly(){
        val maxX = boundaryArea.width -imageView.width
        val maxY = boundaryArea.height - imageView.height
        val randomX = Random.nextInt(0, maxX)
        val randomY = Random.nextInt(0, maxY)

        imageView.animate()
            .x(randomX.toFloat())
            .y(randomY.toFloat())
            .setDuration(300)
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        handler.removeCallbacks(moveRunnable)
    }
}