package com.kashifbhai.customcalendar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PaymentInProgress : AppCompatActivity() {

    private lateinit var okbtn: Button
    private lateinit var textView: TextView
    private lateinit var incrementbtn: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_payment_in_progress)

        okbtn = findViewById(R.id.okay_btn)
        textView = findViewById(R.id.num)
        incrementbtn = findViewById(R.id.incrementbtn)

        okbtn.setOnClickListener {
            val intent = Intent(this, NewScreen::class.java)
            startActivity(intent)
        }

        val viewModel: NewViewModel = ViewModelProvider(this).get(NewViewModel::class.java)
        textView.text = viewModel.number.toString()

        incrementbtn.setOnClickListener {
            viewModel.addOne()
            textView.text = viewModel.number.toString()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}