package com.kashifbhai.customcalendar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.kashifbhai.customcalendar.databinding.ActivityNewScreenBinding

class NewScreen : AppCompatActivity() {
    private lateinit var binding: ActivityNewScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_screen)

        binding = ActivityNewScreenBinding.inflate(layoutInflater)


        binding.okayBtn.setOnClickListener {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.navhostfragment) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.new_Fragment)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}