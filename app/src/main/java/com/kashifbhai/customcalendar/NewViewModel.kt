package com.kashifbhai.customcalendar

import androidx.lifecycle.ViewModel

class NewViewModel: ViewModel() {
    var number = 0
    fun addOne()
    {
        number++
    }
}