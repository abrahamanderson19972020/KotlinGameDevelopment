package com.abraham.catchkennygame

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
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
    private var score = 0
    private  val handler = Handler(Looper.getMainLooper())
    private val moveInternal:Long = 1000

    private val moveRunnable = object:Runnable{
        override fun run() {
            moveImageRandomly()
            handler.postDelayed(this, moveInternal)
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
        timer = object : CountDownTimer(30000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.textTimer.text = "TIMER: ${secondsLeft.toString()}"
            }

            override fun onFinish() {
                binding.textTimer.text = "TIMER: 0"
                binding.textGameOver.text = "GAME OVER"
                handler.removeCallbacks(moveRunnable)
                imageView.animate().cancel()
                imageView.x = 0f
                imageView.y = 0f
            }

        }
        timer.start()

        imageView.setOnClickListener {
            score ++
            scoreText.text = "SCORE: ${score}"
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