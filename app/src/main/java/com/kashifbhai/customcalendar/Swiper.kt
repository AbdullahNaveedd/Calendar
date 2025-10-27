package com.kashifbhai.customcalendar

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2

class Swiper : AppCompatActivity() {
    private lateinit var seekBar: SeekBar
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ImageAdapter
    private lateinit var next: Button
    private lateinit var slideText: TextView
    private var isValidTouch = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiper)

        seekBar = findViewById(R.id.swipeSeekBar)
        viewPager = findViewById(R.id.viewPager)
        next = findViewById(R.id.next)
        slideText = findViewById(R.id.slideText)

        val items = listOf(
            ImageItem(R.drawable.food1, "Food1"),
            ImageItem(R.drawable.food2, "Food2"),
            ImageItem(R.drawable.food3, "Food3")
        )
        adapter = ImageAdapter(items)
        viewPager.adapter = adapter

        next.setOnClickListener {
            val intent = Intent(this, PaymentInProgress::class.java)
            startActivity(intent)
        }

        slideText.visibility = View.VISIBLE
        seekBar.apply {
        }

        seekBar.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val thumb = seekBar.thumb

                    val seekBarWidth = seekBar.width - seekBar.paddingLeft - seekBar.paddingRight
                    val thumbPosition =
                        seekBar.paddingLeft + (seekBar.progress.toFloat() / seekBar.max) * seekBarWidth
                    val touchX = event.x
                    val thumbWidth = thumb.intrinsicWidth
                    val distance = Math.abs(touchX - thumbPosition)

                    if (distance > thumbWidth) {
                        Log.d("Touch", "Blocked - Distance: $distance")
                        isValidTouch = false
                        return@setOnTouchListener true
                    } else {
                        Log.d("Touch", "Allowed - Distance: $distance")
                        isValidTouch = true
                        return@setOnTouchListener false
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (!isValidTouch) {
                        return@setOnTouchListener true
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (!isValidTouch) {
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress > 0) {
                    slideText.visibility = View.GONE
                    sb?.progressDrawable =
                        ContextCompat.getDrawable(this@Swiper, R.drawable.seekbar_track_red)
                } else {
                    slideText.visibility = View.VISIBLE
                }
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {
                if (isValidTouch) {
                    slideText.visibility = View.GONE
                }
            }

            override fun onStopTrackingTouch(sb: SeekBar?) {
                Log.d(
                    "SeekBar",
                    "onStopTrackingTouch - Progress: ${sb?.progress}, isValidTouch: $isValidTouch"
                )

                if (sb != null) {
                    if (sb.progress >= 99) {
                        Log.d("SeekBar", "Locked at end")
                        adapter.updateLockedVisibility(true)
                        slideText.visibility = View.GONE
                    } else {
                        Log.d("SeekBar", "Animating back to start from ${sb.progress}")
                        adapter.updateLockedVisibility(false)
                        sb.thumbOffset = -3

                        ValueAnimator.ofInt(sb.progress, 0).apply {
                            duration = 300
                            addUpdateListener { animator ->
                                val animatedProgress = animator.animatedValue as Int
                                sb.progress = animatedProgress
                                Log.d("Animator", "Progress: $animatedProgress")
                                if (animatedProgress == 0) {
                                    slideText.visibility = View.VISIBLE
                                }
                            }
                            start()
                        }
                    }
                }
                isValidTouch = false
            }
        })
    }
    override fun onResume() {
        super.onResume()
        seekBar.progress = 0
        slideText.visibility = View.VISIBLE
        adapter.updateLockedVisibility(false)
        seekBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.seekbar_track_red)
    }
}